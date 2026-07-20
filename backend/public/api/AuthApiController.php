<?php

require_once __DIR__ . '/../../config/database.php';

class AuthApiController
{
    public function login(): void
    {
        $datos = json_decode(file_get_contents('php://input'), true) ?? [];
        $correo = trim($datos['correo'] ?? '');
        $password = $datos['password'] ?? '';

        $pdo = Database::conexion();
        $stmt = $pdo->prepare('
            SELECT u.id, u.correo, u.nombre_completo, u.password_hash,
                   u.debe_cambiar_password, u.foto_perfil,
                   u.plaza_id, pl.nombre AS plaza_nombre,
                   r.nombre AS rol, r.gestiona_preguntas, r.gestiona_usuarios,
                   r.es_encuestable, r.ve_resultados_tiendas
            FROM usuario u
            JOIN rol r ON r.id = u.rol_id
            LEFT JOIN plaza pl ON pl.id = u.plaza_id
            WHERE u.correo = :correo
            LIMIT 1
        ');
        $stmt->execute(['correo' => $correo]);
        $usuario = $stmt->fetch();

        if (!$usuario || !password_verify($password, $usuario['password_hash'])) {
            http_response_code(401);
            echo json_encode(['error' => 'Correo o password incorrectos']);
            return;
        }

        // MySQL regresa BOOLEAN como 1/0 (enteros). Sin este cast,
        // json_encode manda "1" en vez de "true" y Gson en Android
        // truena (espera boolean literal, no numero).
        $usuario['gestiona_preguntas'] = (bool) $usuario['gestiona_preguntas'];
        $usuario['gestiona_usuarios'] = (bool) $usuario['gestiona_usuarios'];
        $usuario['es_encuestable'] = (bool) $usuario['es_encuestable'];
        $usuario['ve_resultados_tiendas'] = (bool) $usuario['ve_resultados_tiendas'];
        $usuario['debe_cambiar_password'] = (bool) $usuario['debe_cambiar_password'];

        $token = bin2hex(random_bytes(32));
        $stmt = $pdo->prepare('
            INSERT INTO token_acceso (token, usuario_id, fecha_expiracion)
            VALUES (:token, :usuario_id, DATE_ADD(NOW(), INTERVAL 30 DAY))
        ');
        $stmt->execute(['token' => $token, 'usuario_id' => $usuario['id']]);

        unset($usuario['password_hash']);
        echo json_encode(['token' => $token, 'usuario' => $usuario]);
    }
}
