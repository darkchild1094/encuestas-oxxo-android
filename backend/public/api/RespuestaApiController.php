<?php

require_once __DIR__ . '/../../config/database.php';

// Lista de respuestas para el ATI, ya filtrada por sus tiendas
// asignadas (tienda.asesor_ti_usuario_id) -- mismo criterio que
// RespuestaController.php del panel web.
class RespuestaApiController
{
    private function obtenerHeaderAuth(): string
    {
        $header = $_SERVER['HTTP_AUTHORIZATION'] ?? '';
        if ($header === '' && function_exists('getallheaders')) {
            $headers = getallheaders();
            $header = $headers['Authorization'] ?? $headers['authorization'] ?? '';
        }
        return $header;
    }

    private function usuarioDesdeToken(): ?array
    {
        $header = $this->obtenerHeaderAuth();
        if (!preg_match('/Bearer\s+(\S+)/', $header, $m)) {
            return null;
        }

        $pdo = Database::conexion();
        $stmt = $pdo->prepare('
            SELECT u.id, r.ve_resultados_tiendas
            FROM token_acceso ta
            JOIN usuario u ON u.id = ta.usuario_id
            JOIN rol r ON r.id = u.rol_id
            WHERE ta.token = :token AND ta.fecha_expiracion > NOW()
            LIMIT 1
        ');
        $stmt->execute(['token' => $m[1]]);
        $fila = $stmt->fetch();
        return $fila ?: null;
    }

    public function listar(): void
    {
        $usuario = $this->usuarioDesdeToken();
        if (!$usuario) {
            http_response_code(401);
            echo json_encode(['error' => 'token invalido o vencido']);
            return;
        }
        if (!$usuario['ve_resultados_tiendas']) {
            http_response_code(403);
            echo json_encode(['error' => 'este rol no ve resultados de tiendas']);
            return;
        }

        $pdo = Database::conexion();
        $stmt = $pdo->prepare('
            SELECT
                e.id AS encuesta_id, e.fecha_creacion_local, e.comentario,
                t.nombre AS tienda, t.codigo AS tienda_codigo,
                preg.texto AS pregunta, rd.calificacion
            FROM encuesta e
            JOIN tienda t ON t.id = e.tienda_id
            JOIN respuesta_detalle rd ON rd.encuesta_id = e.id
            JOIN pregunta preg ON preg.id = rd.pregunta_id
            WHERE t.asesor_ti_usuario_id = :asesor_actual
            ORDER BY e.fecha_creacion_local DESC, preg.es_fija ASC, preg.orden
        ');
        $stmt->execute(['asesor_actual' => $usuario['id']]);
        echo json_encode($stmt->fetchAll());
    }
}
