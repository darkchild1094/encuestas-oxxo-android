<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Login - Encuestas OXXO</title>
  <link rel="stylesheet" href="<?= BASE_URL ?>/css/style.css">
</head>
<body>
<main class="login-box">
  <h1>Encuestas OXXO</h1>
  <?php if (!empty($_SESSION['error_login'])): ?>
    <p class="error"><?= htmlspecialchars($_SESSION['error_login']) ?></p>
    <?php unset($_SESSION['error_login']); ?>
  <?php endif; ?>
  <form method="POST" action="<?= BASE_URL ?>/login">
    <label>Correo <input type="email" name="correo" required autofocus></label>
    <label>Password <input type="password" name="password" required></label>
    <button type="submit">Entrar</button>
  </form>
</main>
</body>
</html>
