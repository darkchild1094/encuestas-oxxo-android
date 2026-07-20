<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);
require_once __DIR__ . '/../controllers/AuthController.php';
require_once __DIR__ . '/../controllers/UsuarioController.php';
require_once __DIR__ . '/../controllers/PreguntaController.php';
require_once __DIR__ . '/../controllers/RespuestaController.php';
require_once __DIR__ . '/../src/Auth.php';

Auth::iniciar();

// Obtenemos la ruta limpia sin importar si entran por /nps o /nps/public
$ruta = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
$ruta = str_replace(['/nps/public', '/nps'], '', $ruta);
if ($ruta === '' || $ruta === '/') {
    $ruta = '/';
} else {
    $ruta = '/' . ltrim($ruta, '/');
}

// Definimos BASE_URL para que los assets y links funcionen siempre
// Si detectamos 'public' en la URL original, lo mantenemos en BASE_URL
$urlOriginal = $_SERVER['REQUEST_URI'];
define('BASE_URL', (str_contains($urlOriginal, '/nps/public')) ? '/nps/public' : '/nps');

$metodo = $_SERVER['REQUEST_METHOD'];

$rutas = [
    'GET /login' => [AuthController::class, 'mostrarLogin'],
    'POST /login' => [AuthController::class, 'procesarLogin'],
    'GET /logout' => [AuthController::class, 'logout'],
    'GET /cambiar-password' => [AuthController::class, 'mostrarCambiarPassword'],
    'POST /cambiar-password' => [AuthController::class, 'procesarCambiarPassword'],

    'GET /' => [AuthController::class, 'inicio'],
    'GET /respuestas' => [RespuestaController::class, 'index'],
    'GET /respuestas/exportar' => [RespuestaController::class, 'exportarExcel'],

    'GET /usuarios' => [UsuarioController::class, 'index'],
    'POST /usuarios/crear' => [UsuarioController::class, 'crear'],
    'POST /usuarios/editar-datos' => [UsuarioController::class, 'editarDatos'],
    'POST /usuarios/cambiar-rol' => [UsuarioController::class, 'cambiarRol'],
    'POST /usuarios/cambiar-plaza' => [UsuarioController::class, 'cambiarPlaza'],
    'POST /usuarios/restablecer-password' => [UsuarioController::class, 'restablecerPassword'],
    'POST /usuarios/eliminar' => [UsuarioController::class, 'eliminar'],

    'GET /preguntas' => [PreguntaController::class, 'index'],
    'POST /preguntas/crear' => [PreguntaController::class, 'crear'],
    'POST /preguntas/editar' => [PreguntaController::class, 'editar'],
    'POST /preguntas/eliminar' => [PreguntaController::class, 'eliminar'],
];

$clave = "$metodo $ruta";

if (isset($rutas[$clave])) {
    [$clase, $accion] = $rutas[$clave];
    (new $clase())->$accion();
} else {
    http_response_code(404);
    echo "404 - ruta no encontrada ($ruta)";
}
