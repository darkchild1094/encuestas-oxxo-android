<?php
class Database
{
    private static ?PDO $pdo = null;
    public static function conexion(): PDO
    {
        if (self::$pdo === null) {
            // Si estas en localhost, intenta usar config local, si no usa la remota de AlwaysData
            $esLocal = in_array($_SERVER['REMOTE_ADDR'] ?? '', ['127.0.0.1', '::1', '10.0.2.2']) ||
                       ($_SERVER['SERVER_NAME'] ?? '') === 'localhost';

            if ($esLocal) {
                $host = 'localhost';
                $db   = 'encuestas_oxxo'; // Nombre segun el .sql incluido
                $user = 'root';
                $pass = '';
            } else {
                $host = 'mysql-fieldserviceplus.alwaysdata.net';
                $db   = 'fieldserviceplus_nps';
                $user = 'fieldserviceplus_rahuag';
                $pass = 'Admin.12';
            }

            $dsn = "mysql:host=$host;dbname=$db;charset=utf8mb4";
            self::$pdo = new PDO($dsn, $user, $pass, [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            ]);
        }
        return self::$pdo;
    }
}
