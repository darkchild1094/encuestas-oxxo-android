<?php

require_once __DIR__ . '/../../config/database.php';

class SyncApiController
{
    // $_SERVER['HTTP_AUTHORIZATION'] muchas veces llega vacio con
    // Apache+PHP a menos que el servidor este configurado para
    // pasarlo explicitamente (CGIPassAuth). getallheaders() es el
    // fallback que si lo encuentra sin tocar config de Apache.
    private function obtenerHeaderAuth(): string
    {
        $header = $_SERVER['HTTP_AUTHORIZATION'] ?? '';
        if ($header === '' && function_exists('getallheaders')) {
            $headers = getallheaders();
            $header = $headers['Authorization'] ?? $headers['authorization'] ?? '';
        }
        return $header;
    }

    // Valida el header "Authorization: Bearer <token>" contra
    // token_acceso y regresa el usuario dueno del token, o null.
    private function usuarioDesdeToken(): ?array
    {
        $header = $this->obtenerHeaderAuth();
        if (!preg_match('/Bearer\s+(\S+)/', $header, $m)) {
            return null;
        }

        $pdo = Database::conexion();
        $stmt = $pdo->prepare('
            SELECT u.id, r.es_encuestable
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

    // GET /api/cuestionario?plaza_id=1
    // La app la llama al conectarse para refrescar el catalogo local
    // de preguntas (Room) antes de que el tecnico entre a una tienda.
    public function obtenerCuestionario(): void
    {
        $usuario = $this->usuarioDesdeToken();
        if (!$usuario) {
            http_response_code(401);
            echo json_encode(['error' => 'token invalido o vencido']);
            return;
        }

        $plazaId = (int) ($_GET['plaza_id'] ?? 0);
        $pdo = Database::conexion();

        $stmt = $pdo->prepare('SELECT id, nombre FROM cuestionario WHERE plaza_id = :p AND activo = 1 LIMIT 1');
        $stmt->execute(['p' => $plazaId]);
        $cuestionario = $stmt->fetch();

        if (!$cuestionario) {
            echo json_encode(['cuestionario' => null, 'preguntas' => []]);
            return;
        }

        $stmt = $pdo->prepare('SELECT id, texto, orden FROM pregunta WHERE cuestionario_id = :c AND activo = 1 ORDER BY es_fija ASC, orden ASC');
        $stmt->execute(['c' => $cuestionario['id']]);
        $preguntas = $stmt->fetchAll();

        echo json_encode(['cuestionario' => $cuestionario, 'preguntas' => $preguntas]);
    }

    // POST /api/encuestas
    // Body: { "encuestas": [ { id (uuid), tienda_id, cuestionario_id,
    //         comentario, fecha_creacion_local,
    //         respuestas: [ { id (uuid), pregunta_id, calificacion } ] } ] }
    // calificacion es 1-10 (escala NPS: 1-6 detractor, 7-8 pasivo, 9-10 promotor).
    // INSERT IGNORE por uuid: si el WorkManager reintenta el mismo
    // payload (ej. se corto la conexion a medio subir), no duplica.
    public function subirEncuestas(): void
    {
        $usuario = $this->usuarioDesdeToken();
        if (!$usuario) {
            http_response_code(401);
            echo json_encode(['error' => 'token invalido o vencido']);
            return;
        }
        if (!$usuario['es_encuestable']) {
            http_response_code(403);
            echo json_encode(['error' => 'este rol no puede contestar encuestas']);
            return;
        }

        $datos = json_decode(file_get_contents('php://input'), true) ?? [];
        $encuestas = $datos['encuestas'] ?? [];

        $pdo = Database::conexion();
        $pdo->beginTransaction();

        $sincronizadas = [];
        try {
            $stmtEncuesta = $pdo->prepare('
                INSERT IGNORE INTO encuesta
                    (id, usuario_id, tienda_id, cuestionario_id, comentario, fecha_creacion_local, sincronizado, fecha_sincronizacion)
                VALUES
                    (:id, :usuario_id, :tienda_id, :cuestionario_id, :comentario, :fecha_creacion_local, 1, NOW())
            ');
            $stmtRespuesta = $pdo->prepare('
                INSERT IGNORE INTO respuesta_detalle (id, encuesta_id, pregunta_id, calificacion)
                VALUES (:id, :encuesta_id, :pregunta_id, :calificacion)
            ');

            foreach ($encuestas as $e) {
                $stmtEncuesta->execute([
                    'id' => $e['id'],
                    'usuario_id' => $usuario['id'],
                    'tienda_id' => $e['tienda_id'],
                    'cuestionario_id' => $e['cuestionario_id'],
                    'comentario' => $e['comentario'] ?? null,
                    'fecha_creacion_local' => $e['fecha_creacion_local'],
                ]);

                foreach ($e['respuestas'] as $r) {
                    $stmtRespuesta->execute([
                        'id' => $r['id'],
                        'encuesta_id' => $e['id'],
                        'pregunta_id' => $r['pregunta_id'],
                        'calificacion' => $r['calificacion'],
                    ]);
                }

                $sincronizadas[] = $e['id'];
            }

            $pdo->commit();
        } catch (Exception $ex) {
            $pdo->rollBack();
            http_response_code(500);
            echo json_encode(['error' => 'fallo al guardar', 'detalle' => $ex->getMessage()]);
            return;
        }

        echo json_encode(['sincronizadas' => $sincronizadas]);
    }
}
