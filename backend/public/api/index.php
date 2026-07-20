<?php
header('Content-Type: application/json; charset=utf-8');

require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/AuthApiController.php';
require_once __DIR__ . '/SyncApiController.php';
require_once __DIR__ . '/CatalogoApiController.php';
require_once __DIR__ . '/RespuestaApiController.php';

$ruta = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
// Normalizamos la ruta quitando los posibles prefijos de subcarpeta
$ruta = str_replace(['/nps/public', '/nps'], '', $ruta);
if ($ruta === '' || $ruta === '/') {
    $ruta = '/';
} else {
    $ruta = '/' . ltrim($ruta, '/');
}

$metodo = $_SERVER['REQUEST_METHOD'];

$rutas = [
    'POST /api/login' => [AuthApiController::class, 'login'],
    'GET /api/cuestionario' => [SyncApiController::class, 'obtenerCuestionario'],
    'POST /api/encuestas' => [SyncApiController::class, 'subirEncuestas'],
    'GET /api/negocios' => [CatalogoApiController::class, 'negocios'],
    'GET /api/regiones' => [CatalogoApiController::class, 'regiones'],
    'GET /api/plazas' => [CatalogoApiController::class, 'plazas'],
    'GET /api/tiendas' => [CatalogoApiController::class, 'tiendas'],
    'GET /api/respuestas' => [RespuestaApiController::class, 'listar'],
];

$clave = "$metodo $ruta";

if (isset($rutas[$clave])) {
    [$clase, $accion] = $rutas[$clave];
    (new $clase())->$accion();
} else {
    http_response_code(404);
    echo json_encode(['error' => 'ruta no encontrada', 'ruta' => $ruta]);
}
