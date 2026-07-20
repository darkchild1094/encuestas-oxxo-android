<?php require __DIR__ . '/../layout_header.php'; ?>
<h1>Respuestas de tus tiendas asignadas</h1>

<form method="GET" action="<?= BASE_URL ?>/respuestas" class="inline-form">
  <label>Tienda
    <select name="tienda_id">
      <option value="">Todas</option>
      <?php foreach ($tiendas as $t): ?>
        <option value="<?= $t['id'] ?>" <?= ($_GET['tienda_id'] ?? '') == $t['id'] ? 'selected' : '' ?>>
          <?= htmlspecialchars($t['nombre']) ?>
        </option>
      <?php endforeach; ?>
    </select>
  </label>
  <label>Desde <input type="date" name="desde" value="<?= htmlspecialchars($_GET['desde'] ?? '') ?>"></label>
  <label>Hasta <input type="date" name="hasta" value="<?= htmlspecialchars($_GET['hasta'] ?? '') ?>"></label>
  <button type="submit">Filtrar</button>
  <a class="boton-secundario" href="<?= BASE_URL ?>/respuestas/exportar?<?= http_build_query($_GET) ?>">Exportar a Excel</a>
</form>

<table>
  <tr><th>Fecha</th><th>Tienda</th><th>Usuario</th><th>Pregunta</th><th>Calificacion</th><th>Comentario</th></tr>
  <?php foreach ($filas as $f):
    $cal = (int) $f['calificacion'];
    $clase = $cal <= 6 ? 'cal-detractor' : ($cal <= 8 ? 'cal-pasivo' : 'cal-promotor');
    $etiqueta = $cal <= 6 ? 'Detractor' : ($cal <= 8 ? 'Pasivo' : 'Promotor');
  ?>
  <tr>
    <td><?= htmlspecialchars($f['fecha_creacion_local']) ?></td>
    <td><?= htmlspecialchars($f['tienda']) ?></td>
    <td><?= htmlspecialchars($f['usuario'] ?? '(usuario eliminado)') ?></td>
    <td><?= htmlspecialchars($f['pregunta']) ?></td>
    <td><span class="calificacion-tag <?= $clase ?>"><?= $cal ?>/10 &middot; <?= $etiqueta ?></span></td>
    <td><?= htmlspecialchars($f['comentario'] ?? '') ?></td>
  </tr>
  <?php endforeach; ?>
  <?php if (!$filas): ?>
  <tr><td colspan="6">Sin resultados con estos filtros.</td></tr>
  <?php endif; ?>
</table>
<?php require __DIR__ . '/../layout_footer.php'; ?>
