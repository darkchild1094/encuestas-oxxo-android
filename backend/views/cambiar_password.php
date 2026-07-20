<?php require __DIR__ . '/layout_header.php'; ?>
<h1>Cambiar password</h1>
<p>Es tu primer acceso o te restablecieron la password. Pon una nueva antes de seguir.</p>
<?php if (!empty($_SESSION['error_password'])): ?>
  <p class="error"><?= htmlspecialchars($_SESSION['error_password']) ?></p>
  <?php unset($_SESSION['error_password']); ?>
<?php endif; ?>
<form method="POST" action="<?= BASE_URL ?>/cambiar-password">
  <label>Nueva password <input type="password" name="password" minlength="8" required></label>
  <label>Confirmar <input type="password" name="password_confirmar" minlength="8" required></label>
  <button type="submit">Guardar</button>
</form>
<?php require __DIR__ . '/layout_footer.php'; ?>
