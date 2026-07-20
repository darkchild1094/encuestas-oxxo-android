<?php

class Auth
{
    public static function iniciar(): void
    {
        if (session_status() === PHP_SESSION_NONE) {
            session_start();
        }
    }

    // Guarda en sesion lo minimo necesario para pintar el menu y
    // checar permisos sin volver a pegarle a la BD en cada request.
    public static function login(array $usuario): void
    {
        self::iniciar();
        session_regenerate_id(true);
        $_SESSION['usuario_id'] = $usuario['id'];
        $_SESSION['rol'] = $usuario['rol_nombre'];
        $_SESSION['nombre_completo'] = $usuario['nombre_completo'] ?? '';
        $_SESSION['foto_perfil'] = $usuario['foto_perfil'] ?? null;
        $_SESSION['gestiona_preguntas'] = (bool) $usuario['gestiona_preguntas'];
        $_SESSION['gestiona_usuarios'] = (bool) $usuario['gestiona_usuarios'];
        $_SESSION['ve_resultados_tiendas'] = (bool) $usuario['ve_resultados_tiendas'];
        $_SESSION['debe_cambiar_password'] = (bool) $usuario['debe_cambiar_password'];
    }

    public static function requiereLogin(): void
    {
        self::iniciar();
        if (empty($_SESSION['usuario_id'])) {
            header('Location: ' . BASE_URL . '/login');
            exit;
        }
    }

    // $permiso: 'gestiona_preguntas' | 'gestiona_usuarios' | 've_resultados_tiendas'
    public static function requierePermiso(string $permiso): void
    {
        self::requiereLogin();
        if (empty($_SESSION[$permiso])) {
            http_response_code(403);
            echo 'No tienes permiso para ver esta seccion.';
            exit;
        }
    }

    public static function logout(): void
    {
        self::iniciar();
        $_SESSION = [];
        session_destroy();
    }
}
