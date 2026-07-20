<?php

require_once __DIR__ . '/../../config/database.php';

// Catalogo negocio/region/plaza/tienda para el selector en cascada
// de la app. Cualquier usuario autenticado puede leerlo (no hace
// falta permiso especial, solo un token valido).
class CatalogoApiController
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

    private function validarToken(): bool
    {
        $header = $this->obtenerHeaderAuth();
        if (!preg_match('/Bearer\s+(\S+)/', $header, $m)) {
            return false;
        }
        $pdo = Database::conexion();
        $stmt = $pdo->prepare('SELECT 1 FROM token_acceso WHERE token = :token AND fecha_expiracion > NOW() LIMIT 1');
        $stmt->execute(['token' => $m[1]]);
        return (bool) $stmt->fetchColumn();
    }

    private function noAutorizado(): void
    {
        http_response_code(401);
        echo json_encode(['error' => 'token invalido o vencido']);
    }

    public function negocios(): void
    {
        if (!$this->validarToken()) { $this->noAutorizado(); return; }
        $pdo = Database::conexion();
        $filas = $pdo->query('SELECT id, nombre, es_default FROM negocio ORDER BY nombre')->fetchAll();
        foreach ($filas as &$f) { $f['es_default'] = (bool) $f['es_default']; }
        echo json_encode($filas);
    }

    public function regiones(): void
    {
        if (!$this->validarToken()) { $this->noAutorizado(); return; }
        $pdo = Database::conexion();
        $stmt = $pdo->prepare('SELECT id, nombre, cr, es_default FROM region WHERE negocio_id = :n ORDER BY nombre');
        $stmt->execute(['n' => (int) ($_GET['negocio_id'] ?? 0)]);
        $filas = $stmt->fetchAll();
        foreach ($filas as &$f) { $f['es_default'] = (bool) $f['es_default']; }
        echo json_encode($filas);
    }

    public function plazas(): void
    {
        if (!$this->validarToken()) { $this->noAutorizado(); return; }
        $pdo = Database::conexion();
        $stmt = $pdo->prepare('SELECT id, nombre, cr, es_default FROM plaza WHERE region_id = :r ORDER BY nombre');
        $stmt->execute(['r' => (int) ($_GET['region_id'] ?? 0)]);
        $filas = $stmt->fetchAll();
        foreach ($filas as &$f) { $f['es_default'] = (bool) $f['es_default']; }
        echo json_encode($filas);
    }

    public function tiendas(): void
    {
        if (!$this->validarToken()) { $this->noAutorizado(); return; }
        $pdo = Database::conexion();
        $stmt = $pdo->prepare('SELECT id, nombre, codigo FROM tienda WHERE plaza_id = :p ORDER BY nombre');
        $stmt->execute(['p' => (int) ($_GET['plaza_id'] ?? 0)]);
        echo json_encode($stmt->fetchAll());
    }
}
