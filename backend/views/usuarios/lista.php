<?php require __DIR__ . '/../layout_header.php'; ?>
<h1>Usuarios</h1>

<?php if (!empty($_SESSION['mensaje'])): ?>
  <p class="ok"><?= htmlspecialchars($_SESSION['mensaje']) ?></p>
  <?php unset($_SESSION['mensaje']); ?>
<?php endif; ?>

<h2>Nuevo usuario</h2>
<form method="POST" action="<?= BASE_URL ?>/usuarios/crear" class="inline-form" enctype="multipart/form-data">
  <label>Nombre completo <input type="text" name="nombre_completo" required></label>
  <label>Correo <input type="email" name="correo" required></label>
  <label>Rol
    <select name="rol_id" required>
      <?php foreach ($roles as $r): ?>
        <option value="<?= $r['id'] ?>"><?= htmlspecialchars($r['nombre']) ?></option>
      <?php endforeach; ?>
    </select>
  </label>
  <label>Plaza
    <select name="plaza_id">
      <option value="">Sin asignar</option>
      <?php foreach ($plazas as $p): ?>
        <option value="<?= $p['id'] ?>"><?= htmlspecialchars("{$p['negocio']} / {$p['region']} / {$p['nombre']}") ?></option>
      <?php endforeach; ?>
    </select>
  </label>
  <label>Foto de perfil <input type="file" name="foto_perfil" accept="image/png,image/jpeg,image/webp"></label>
  <button type="submit">Crear (genera password temporal)</button>
</form>

<h2>Listado</h2>
<table>
  <tr><th>Foto</th><th>Nombre</th><th>Correo</th><th>Rol</th><th>Plaza</th><th>Debe cambiar pass</th><th>Registro</th><th>Acciones</th></tr>
  <?php foreach ($usuarios as $u): ?>
  <tr>
    <td>
      <?php if (!empty($u['foto_perfil'])): ?>
        <img src="<?= BASE_URL ?>/<?= htmlspecialchars($u['foto_perfil']) ?>" alt="" width="40" height="40" style="border-radius:50%;object-fit:cover">
      <?php else: ?>
        &mdash;
      <?php endif; ?>
    </td>
    <td>
      <form method="POST" action="<?= BASE_URL ?>/usuarios/editar-datos" enctype="multipart/form-data" class="inline-form">
        <input type="hidden" name="id" value="<?= $u['id'] ?>">
        <input type="text" name="nombre_completo" value="<?= htmlspecialchars($u['nombre_completo'] ?? '') ?>" style="width:160px">
        <input type="file" name="foto_perfil" accept="image/png,image/jpeg,image/webp" style="width:120px">
        <button type="submit">Guardar</button>
      </form>
    </td>
    <td><?= htmlspecialchars($u['correo']) ?></td>
    <td>
      <form method="POST" action="<?= BASE_URL ?>/usuarios/cambiar-rol">
        <input type="hidden" name="id" value="<?= $u['id'] ?>">
        <select name="rol_id" onchange="this.form.submit()">
          <?php foreach ($roles as $r): ?>
            <option value="<?= $r['id'] ?>" <?= $r['nombre'] === $u['rol'] ? 'selected' : '' ?>>
              <?= htmlspecialchars($r['nombre']) ?>
            </option>
          <?php endforeach; ?>
        </select>
      </form>
    </td>
    <td>
      <form method="POST" action="<?= BASE_URL ?>/usuarios/cambiar-plaza">
        <input type="hidden" name="id" value="<?= $u['id'] ?>">
        <select name="plaza_id" onchange="this.form.submit()">
          <option value="">Sin asignar</option>
          <?php foreach ($plazas as $p): ?>
            <option value="<?= $p['id'] ?>" <?= $p['id'] == $u['plaza_id'] ? 'selected' : '' ?>>
              <?= htmlspecialchars($p['nombre']) ?>
            </option>
          <?php endforeach; ?>
        </select>
      </form>
    </td>
    <td><?= $u['debe_cambiar_password'] ? 'Si' : 'No' ?></td>
    <td><?= htmlspecialchars($u['fecha_registro']) ?></td>
    <td class="acciones">
      <form method="POST" action="<?= BASE_URL ?>/usuarios/restablecer-password">
        <input type="hidden" name="id" value="<?= $u['id'] ?>">
        <button type="submit">Restablecer password</button>
      </form>
      <form method="POST" action="<?= BASE_URL ?>/usuarios/eliminar" onsubmit="return confirm('¿Borrar este usuario?')">
        <input type="hidden" name="id" value="<?= $u['id'] ?>">
        <button type="submit" class="danger">Eliminar</button>
      </form>
    </td>
  </tr>
  <?php endforeach; ?>
</table>
<?php require __DIR__ . '/../layout_footer.php'; ?>
