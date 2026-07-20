<?php

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../src/Auth.php';

// Solo ATI entra aqui (ve_resultados_tiendas). Ni webmaster ni
// usuario normal ven esta pantalla.
class RespuestaController
{
    private function query(array $filtros): array
    {
        $sql = '
            SELECT
                e.id AS encuesta_id, e.fecha_creacion_local, e.comentario,
                t.nombre AS tienda, p.nombre AS plaza, r.nombre AS region, n.nombre AS negocio,
                u.correo AS usuario,
                preg.texto AS pregunta, rd.calificacion
            FROM encuesta e
            JOIN tienda t ON t.id = e.tienda_id
            JOIN plaza p ON p.id = t.plaza_id
            JOIN region r ON r.id = p.region_id
            JOIN negocio n ON n.id = r.negocio_id
            LEFT JOIN usuario u ON u.id = e.usuario_id
            JOIN respuesta_detalle rd ON rd.encuesta_id = e.id
            JOIN pregunta preg ON preg.id = rd.pregunta_id
            WHERE t.asesor_ti_usuario_id = :asesor_actual
        ';
        // El ATI SOLO ve resultados de las tiendas donde el mismo es
        // el asesor TI asignado (tienda.asesor_ti_usuario_id) -- ya
        // no ve todas las tiendas del sistema.
        $params = ['asesor_actual' => $_SESSION['usuario_id']];

        if (!empty($filtros['plaza_id'])) {
            $sql .= ' AND p.id = :plaza_id';
            $params['plaza_id'] = $filtros['plaza_id'];
        }
        if (!empty($filtros['tienda_id'])) {
            $sql .= ' AND t.id = :tienda_id';
            $params['tienda_id'] = $filtros['tienda_id'];
        }
        if (!empty($filtros['desde'])) {
            $sql .= ' AND e.fecha_creacion_local >= :desde';
            $params['desde'] = $filtros['desde'] . ' 00:00:00';
        }
        if (!empty($filtros['hasta'])) {
            $sql .= ' AND e.fecha_creacion_local <= :hasta';
            $params['hasta'] = $filtros['hasta'] . ' 23:59:59';
        }

        $sql .= ' ORDER BY e.fecha_creacion_local DESC, preg.es_fija ASC, preg.orden';

        $pdo = Database::conexion();
        $stmt = $pdo->prepare($sql);
        $stmt->execute($params);
        return $stmt->fetchAll();
    }

    private function filtrosDesdeGet(): array
    {
        return [
            'plaza_id' => $_GET['plaza_id'] ?? null,
            'tienda_id' => $_GET['tienda_id'] ?? null,
            'desde' => $_GET['desde'] ?? null,
            'hasta' => $_GET['hasta'] ?? null,
        ];
    }

    public function index(): void
    {
        Auth::requierePermiso('ve_resultados_tiendas');
        $pdo = Database::conexion();

        $stmt = $pdo->prepare('
            SELECT id, nombre FROM tienda
            WHERE asesor_ti_usuario_id = :asesor_actual
            ORDER BY nombre
        ');
        $stmt->execute(['asesor_actual' => $_SESSION['usuario_id']]);
        $tiendas = $stmt->fetchAll();

        $filas = $this->query($this->filtrosDesdeGet());

        require __DIR__ . '/../views/respuestas/lista.php';
    }

    public function exportarExcel(): void
    {
        Auth::requierePermiso('ve_resultados_tiendas');
        $filas = $this->query($this->filtrosDesdeGet());

        // Exportacion basica: tabla HTML con content-type de Excel.
        // Abre bien en Excel y LibreOffice Calc sin depender de
        // ninguna libreria. Si despues se necesita un .xlsx real con
        // formato/formulas, se agrega PhpSpreadsheet via composer.
        header('Content-Type: application/vnd.ms-excel; charset=utf-8');
        header('Content-Disposition: attachment; filename="respuestas_' . date('Y-m-d_His') . '.xls"');

        echo "<table border='1'>";
        echo '<tr>
                <th>Fecha</th><th>Negocio</th><th>Region</th><th>Plaza</th><th>Tienda</th>
                <th>Usuario</th><th>Pregunta</th><th>Calificacion (1-10)</th><th>Comentario</th>
              </tr>';

        foreach ($filas as $fila) {
            echo '<tr>'
                . '<td>' . htmlspecialchars($fila['fecha_creacion_local']) . '</td>'
                . '<td>' . htmlspecialchars($fila['negocio']) . '</td>'
                . '<td>' . htmlspecialchars($fila['region']) . '</td>'
                . '<td>' . htmlspecialchars($fila['plaza']) . '</td>'
                . '<td>' . htmlspecialchars($fila['tienda']) . '</td>'
                . '<td>' . htmlspecialchars($fila['usuario'] ?? '(usuario eliminado)') . '</td>'
                . '<td>' . htmlspecialchars($fila['pregunta']) . '</td>'
                . '<td>' . (int) $fila['calificacion'] . '</td>'
                . '<td>' . htmlspecialchars($fila['comentario'] ?? '') . '</td>'
                . '</tr>';
        }

        echo '</table>';
        exit;
    }
}
