<?php require __DIR__ . '/../layout_header.php'; ?>
<h1>Preguntas</h1>

<form method="GET" action="<?= BASE_URL ?>/preguntas">
  <label>Plaza
    <select name="plaza_id" onchange="this.form.submit()">
      <?php foreach ($plazas as $p): ?>
        <option value="<?= $p['id'] ?>" <?= $p['id'] == $plazaId ? 'selected' : '' ?>>
          <?= htmlspecialchars("{$p['negocio']} / {$p['region']} / {$p['nombre']}") ?>
        </option>
      <?php endforeach; ?>
    </select>
  </label>
</form>

<?php if ($cuestionario): ?>
<h2>Nueva pregunta &mdash; <?= htmlspecialchars($cuestionario['nombre']) ?></h2>
<form method="POST" action="<?= BASE_URL ?>/preguntas/crear" class="inline-form">
  <input type="hidden" name="cuestionario_id" value="<?= $cuestionario['id'] ?>">
  <input type="hidden" name="plaza_id" value="<?= $plazaId ?>">
  <label>Texto <input type="text" name="texto" required maxlength="255" style="width:300px"></label>
  <label>Orden <input type="number" name="orden" value="<?= count($preguntas) + 1 ?>" style="width:70px"></label>
  <button type="submit">Agregar</button>
</form>

<table>
  <tr><th>Orden</th><th>Pregunta</th><th>Acciones</th></tr>
  <?php foreach ($preguntas as $p): ?>
  <tr>
    <td colspan="3">
      <form method="POST" action="<?= BASE_URL ?>/preguntas/editar" class="inline-form">
        <input type="hidden" name="id" value="<?= $p['id'] ?>">
        <input type="hidden" name="plaza_id" value="<?= $plazaId ?>">
        <input type="number" name="orden" value="<?= $p['orden'] ?>" style="width:60px" <?= $p['es_fija'] ? 'disabled' : '' ?>>
        <input type="text" name="texto" value="<?= htmlspecialchars($p['texto']) ?>" style="width:400px">
        <?php if ($p['es_fija']): ?>
          <span class="rol-tag">fija &mdash; siempre penultima</span>
        <?php endif; ?>
        <button type="submit">Guardar</button>
      </form>
      <?php if (!$p['es_fija']): ?>
      <form method="POST" action="<?= BASE_URL ?>/preguntas/eliminar" class="inline-form" onsubmit="return confirm('¿Quitar esta pregunta?')">
        <input type="hidden" name="id" value="<?= $p['id'] ?>">
        <input type="hidden" name="plaza_id" value="<?= $plazaId ?>">
        <button type="submit" class="danger">Quitar</button>
      </form>
      <?php endif; ?>
    </td>
  </tr>
  <?php endforeach; ?>
  <?php if (!$preguntas): ?>
  <tr><td colspan="3">Todavia no hay preguntas para esta plaza.</td></tr>
  <?php endif; ?>
</table>
<p class="ok">El comentario opcional siempre va al final, despues de todas las preguntas -- no es parte de este listado.</p>
<?php else: ?>
<p>No hay plazas registradas.</p>
<?php endif; ?>
<?php require __DIR__ . '/../layout_footer.php'; ?>
