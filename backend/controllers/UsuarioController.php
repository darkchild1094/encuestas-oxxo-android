<?php

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../src/Auth.php';

// Solo webmaster entra aqui (gestiona_usuarios).
class UsuarioController
{
    public function index(): void
    {
        Auth::requierePermiso('gestiona_usuarios');
        $pdo = Database::conexion();

        $usuarios = $pdo->query('
            SELECT u.id, u.correo, u.nombre_completo, u.foto_perfil, u.plaza_id,
                   u.debe_cambiar_password, u.fecha_registro, r.nombre AS rol,
                   p.nombre AS plaza
            FROM usuario u
            JOIN rol r ON r.id = u.rol_id
            LEFT JOIN plaza p ON p.id = u.plaza_id
            ORDER BY u.correo
        ')->fetchAll();

        $roles = $pdo->query('SELECT id, nombre FROM rol ORDER BY nombre')->fetchAll();
        $plazas = $pdo->query('
            SELECT pl.id, pl.nombre, r.nombre AS region, n.nombre AS negocio
            FROM plaza pl
            JOIN region r ON r.id = pl.region_id
            JOIN negocio n ON n.id = r.negocio_id
            ORDER BY n.nombre, r.nombre, pl.nombre
        ')->fetchAll();

        require __DIR__ . '/../views/usuarios/lista.php';
    }

    public function crear(): void
    {
        Auth::requierePermiso('gestiona_usuarios');
        $correo = trim($_POST['correo'] ?? '');
        $nombreCompleto = trim($_POST['nombre_completo'] ?? '');
        $rolId = (int) ($_POST['rol_id'] ?? 0);
        $plazaId = (int) ($_POST['plaza_id'] ?? 0) ?: null;

        $rutaFoto = $this->guardarFotoSiViene();

        // Password temporal: se muestra UNA sola vez en el mensaje de
        // confirmacion. No se guarda en texto plano en ningun lado.
        $temporal = bin2hex(random_bytes(4));

        $pdo = Database::conexion();
        $stmt = $pdo->prepare('
            INSERT INTO usuario (rol_id, plaza_id, correo, nombre_completo, password_hash, foto_perfil, debe_cambiar_password)
            VALUES (:rol_id, :plaza_id, :correo, :nombre, :hash, :foto, 1)
        ');
        $stmt->execute([
            'rol_id' => $rolId,
            'plaza_id' => $plazaId,
            'correo' => $correo,
            'nombre' => $nombreCompleto,
            'hash' => password_hash($temporal, PASSWORD_DEFAULT),
            'foto' => $rutaFoto,
        ]);

        $_SESSION['mensaje'] = "Usuario creado. Password temporal: $temporal (copiala ahora, no se vuelve a mostrar).";
        header('Location: ' . BASE_URL . '/usuarios');
        exit;
    }

    // Editar nombre y/o foto de un usuario que ya existe. El correo y
    // el rol tienen su propio flujo (cambiarRol ya existe); esto es
    // solo para los datos de perfil.
    public function editarDatos(): void
    {
        Auth::requierePermiso('gestiona_usuarios');
        $id = (int) ($_POST['id'] ?? 0);
        $nombreCompleto = trim($_POST['nombre_completo'] ?? '');

        $rutaFoto = $this->guardarFotoSiViene();

        $pdo = Database::conexion();
        if ($rutaFoto) {
            $stmt = $pdo->prepare('UPDATE usuario SET nombre_completo = :nombre, foto_perfil = :foto WHERE id = :id');
            $stmt->execute(['nombre' => $nombreCompleto, 'foto' => $rutaFoto, 'id' => $id]);
        } else {
            $stmt = $pdo->prepare('UPDATE usuario SET nombre_completo = :nombre WHERE id = :id');
            $stmt->execute(['nombre' => $nombreCompleto, 'id' => $id]);
        }

        header('Location: ' . BASE_URL . '/usuarios');
        exit;
    }

    // Valida y guarda la foto si vino en el request. Regresa la ruta
    // relativa a guardar en BD, o null si no se subio nada (para no
    // pisar la foto anterior en un editarDatos()).
    private function guardarFotoSiViene(): ?string
    {
        if (empty($_FILES['foto_perfil']) || $_FILES['foto_perfil']['error'] === UPLOAD_ERR_NO_FILE) {
            return null;
        }
        if ($_FILES['foto_perfil']['error'] !== UPLOAD_ERR_OK) {
            return null;
        }

        $permitidos = ['image/jpeg' => 'jpg', 'image/png' => 'png', 'image/webp' => 'webp'];
        $tipo = mime_content_type($_FILES['foto_perfil']['tmp_name']);
        if (!isset($permitidos[$tipo])) {
            return null; // tipo no soportado, se ignora silenciosamente
        }

        $nombreArchivo = bin2hex(random_bytes(16)) . '.' . $permitidos[$tipo];
        $destino = __DIR__ . '/../public/uploads/perfiles/' . $nombreArchivo;
        move_uploaded_file($_FILES['foto_perfil']['tmp_name'], $destino);

        return 'uploads/perfiles/' . $nombreArchivo;
    }

    public function cambiarRol(): void
    {
        Auth::requierePermiso('gestiona_usuarios');
        $id = (int) ($_POST['id'] ?? 0);
        $rolId = (int) ($_POST['rol_id'] ?? 0);

        $pdo = Database::conexion();
        $stmt = $pdo->prepare('UPDATE usuario SET rol_id = :rol_id WHERE id = :id');
        $stmt->execute(['rol_id' => $rolId, 'id' => $id]);

        header('Location: ' . BASE_URL . '/usuarios');
        exit;
    }

    public function cambiarPlaza(): void
    {
        Auth::requierePermiso('gestiona_usuarios');
        $id = (int) ($_POST['id'] ?? 0);
        $plazaId = (int) ($_POST['plaza_id'] ?? 0) ?: null;

        $pdo = Database::conexion();
        $stmt = $pdo->prepare('UPDATE usuario SET plaza_id = :plaza_id WHERE id = :id');
        $stmt->execute(['plaza_id' => $plazaId, 'id' => $id]);

        header('Location: ' . BASE_URL . '/usuarios');
        exit;
    }

    public function restablecerPassword(): void
    {
        Auth::requierePermiso('gestiona_usuarios');
        $id = (int) ($_POST['id'] ?? 0);
        $temporal = bin2hex(random_bytes(4));

        $pdo = Database::conexion();
        $stmt = $pdo->prepare('
            UPDATE usuario
            SET password_hash = :hash, debe_cambiar_password = 1
            WHERE id = :id
        ');
        $stmt->execute([
            'hash' => password_hash($temporal, PASSWORD_DEFAULT),
            'id' => $id,
        ]);

        $_SESSION['mensaje'] = "Password temporal generada: $temporal (copiala ahora, no se vuelve a mostrar).";
        header('Location: ' . BASE_URL . '/usuarios');
        exit;
    }

    public function eliminar(): void
    {
        Auth::requierePermiso('gestiona_usuarios');
        $id = (int) ($_POST['id'] ?? 0);

        // encuesta.usuario_id es ON DELETE SET NULL, asi que borrar un
        // usuario no rompe el historial de encuestas ya contestadas.
        $pdo = Database::conexion();
        $stmt = $pdo->prepare('DELETE FROM usuario WHERE id = :id');
        $stmt->execute(['id' => $id]);

        header('Location: ' . BASE_URL . '/usuarios');
        exit;
    }
}
