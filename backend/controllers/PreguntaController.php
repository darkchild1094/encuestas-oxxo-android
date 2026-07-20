<?php

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../src/Auth.php';

// ATI y webmaster entran aqui (gestiona_preguntas).
class PreguntaController
{
    public function index(): void
    {
        Auth::requierePermiso('gestiona_preguntas');
        $pdo = Database::conexion();

        $plazaId = (int) ($_GET['plaza_id'] ?? 0);

        $plazas = $pdo->query('
            SELECT p.id, p.nombre, r.nombre AS region, n.nombre AS negocio
            FROM plaza p
            JOIN region r ON r.id = p.region_id
            JOIN negocio n ON n.id = r.negocio_id
            ORDER BY n.nombre, r.nombre, p.nombre
        ')->fetchAll();

        if (!$plazaId && $plazas) {
            $plazaId = $plazas[0]['id'];
        }

        $cuestionario = null;
        $preguntas = [];

        if ($plazaId) {
            $stmt = $pdo->prepare('SELECT * FROM cuestionario WHERE plaza_id = :p LIMIT 1');
            $stmt->execute(['p' => $plazaId]);
            $cuestionario = $stmt->fetch();

            // Si la plaza todavia no tiene cuestionario, se crea uno
            // vacio automaticamente para que el ATI/webmaster puedan
            // empezar a agregar preguntas sin pasos extra.
            if (!$cuestionario) {
                $stmt = $pdo->prepare("INSERT INTO cuestionario (plaza_id, nombre) VALUES (:p, 'Encuesta de satisfaccion')");
                $stmt->execute(['p' => $plazaId]);
                $stmt = $pdo->prepare('SELECT * FROM cuestionario WHERE plaza_id = :p LIMIT 1');
                $stmt->execute(['p' => $plazaId]);
                $cuestionario = $stmt->fetch();

                // La pregunta de TI va SIEMPRE, en todo cuestionario,
                // sin excepcion -- se inyecta sola al crearse. No se
                // puede borrar desde el CRUD (ver eliminar()).
                $stmt = $pdo->prepare("
                    INSERT INTO pregunta (cuestionario_id, creado_por_usuario_id, texto, orden, es_fija)
                    VALUES (:c, NULL, 'Como calificarias el servicio del area de TI', 9999, 1)
                ");
                $stmt->execute(['c' => $cuestionario['id']]);
            }

            // es_fija siempre al final (antes del comentario, que va
            // fuera de esta lista): ordenar por es_fija ASC hace que
            // las normales (0) salgan primero y la fija (1) quede
            // hasta abajo sin importar el 'orden' que tenga.
            $stmt = $pdo->prepare('
                SELECT * FROM pregunta
                WHERE cuestionario_id = :c AND activo = 1
                ORDER BY es_fija ASC, orden ASC
            ');
            $stmt->execute(['c' => $cuestionario['id']]);
            $preguntas = $stmt->fetchAll();
        }

        require __DIR__ . '/../views/preguntas/lista.php';
    }

    public function crear(): void
    {
        Auth::requierePermiso('gestiona_preguntas');
        $cuestionarioId = (int) ($_POST['cuestionario_id'] ?? 0);
        $texto = trim($_POST['texto'] ?? '');
        $orden = (int) ($_POST['orden'] ?? 0);

        $pdo = Database::conexion();
        $stmt = $pdo->prepare('
            INSERT INTO pregunta (cuestionario_id, creado_por_usuario_id, texto, orden)
            VALUES (:c, :u, :t, :o)
        ');
        $stmt->execute([
            'c' => $cuestionarioId,
            'u' => $_SESSION['usuario_id'],
            't' => $texto,
            'o' => $orden,
        ]);

        header('Location: ' . BASE_URL . '/preguntas?plaza_id=' . (int) ($_POST['plaza_id'] ?? 0));
        exit;
    }

    public function editar(): void
    {
        Auth::requierePermiso('gestiona_preguntas');
        $id = (int) ($_POST['id'] ?? 0);
        $texto = trim($_POST['texto'] ?? '');
        $orden = (int) ($_POST['orden'] ?? 0);

        $pdo = Database::conexion();
        $stmt = $pdo->prepare('UPDATE pregunta SET texto = :t, orden = :o WHERE id = :id');
        $stmt->execute(['t' => $texto, 'o' => $orden, 'id' => $id]);

        header('Location: ' . BASE_URL . '/preguntas?plaza_id=' . (int) ($_POST['plaza_id'] ?? 0));
        exit;
    }

    public function eliminar(): void
    {
        Auth::requierePermiso('gestiona_preguntas');
        $id = (int) ($_POST['id'] ?? 0);

        $pdo = Database::conexion();

        // La pregunta fija de TI no se puede quitar, ni siquiera con
        // soft-delete -- tiene que existir si o si en todo cuestionario.
        $stmt = $pdo->prepare('SELECT es_fija FROM pregunta WHERE id = :id');
        $stmt->execute(['id' => $id]);
        if ((bool) $stmt->fetchColumn()) {
            header('Location: ' . BASE_URL . '/preguntas?plaza_id=' . (int) ($_POST['plaza_id'] ?? 0));
            exit;
        }

        // Soft delete: nunca DELETE fisico, porque respuesta_detalle
        // tiene ON DELETE RESTRICT hacia pregunta -- si ya hay
        // respuestas guardadas, el DELETE fisico tronaria de todos
        // modos. Mejor apagar 'activo' y ya no sale en la app ni aqui.
        $stmt = $pdo->prepare('UPDATE pregunta SET activo = 0 WHERE id = :id');
        $stmt->execute(['id' => $id]);

        header('Location: ' . BASE_URL . '/preguntas?plaza_id=' . (int) ($_POST['plaza_id'] ?? 0));
        exit;
    }
}
