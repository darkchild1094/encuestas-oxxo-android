-- Tabla adicional para autenticacion de la app movil (login por
-- token, no por sesion PHP). Correr esto sobre encuestas_oxxo
-- despues del script principal.
USE encuestas_oxxo;

CREATE TABLE token_acceso (
  token             CHAR(64)     NOT NULL PRIMARY KEY,
  usuario_id        INT UNSIGNED NOT NULL,
  fecha_creacion    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_expiracion  DATETIME     NOT NULL,
  CONSTRAINT fk_token_usuario FOREIGN KEY (usuario_id)
    REFERENCES usuario(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_token_usuario ON token_acceso (usuario_id);
