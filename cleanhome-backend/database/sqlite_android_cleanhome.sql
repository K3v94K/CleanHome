-- Base local SQLite para la aplicacion Android de CleanHome.
-- Este script no reemplaza la base MySQL del backend.
-- Su objetivo es guardar datos en el dispositivo y apoyar sincronizacion offline.

PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS sync_metadata (
    sync_key TEXT PRIMARY KEY,
    last_sync_at TEXT,
    updated_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS usuario_sesion (
    id_usuario INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL,
    correo TEXT NOT NULL,
    telefono TEXT,
    direccion TEXT,
    id_rol INTEGER,
    nombre_rol TEXT,
    token TEXT,
    fecha_creacion TEXT,
    updated_at TEXT
);

CREATE TABLE IF NOT EXISTS servicios (
    id_servicio INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL,
    descripcion TEXT,
    duracion_estimada TEXT,
    precio REAL NOT NULL,
    activo INTEGER NOT NULL DEFAULT 1,
    fecha_creacion TEXT,
    updated_at TEXT,
    local_updated_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS personal_limpieza (
    id_personal INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL,
    telefono TEXT,
    disponible INTEGER NOT NULL DEFAULT 1,
    fecha_creacion TEXT,
    updated_at TEXT,
    local_updated_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS solicitudes (
    id_solicitud INTEGER PRIMARY KEY,
    client_temp_id TEXT UNIQUE,
    id_usuario INTEGER NOT NULL,
    id_servicio INTEGER NOT NULL,
    id_personal INTEGER,
    fecha_servicio TEXT NOT NULL,
    hora_servicio TEXT NOT NULL,
    direccion_atencion TEXT NOT NULL,
    estado TEXT NOT NULL DEFAULT 'Pendiente',
    cliente TEXT,
    servicio TEXT,
    servicio_descripcion TEXT,
    personal_nombre TEXT,
    fecha_creacion TEXT,
    updated_at TEXT,
    local_created_at TEXT NOT NULL DEFAULT (datetime('now')),
    local_updated_at TEXT NOT NULL DEFAULT (datetime('now')),
    sync_status TEXT NOT NULL DEFAULT 'synced',
    sync_error TEXT,
    FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio)
);

CREATE TABLE IF NOT EXISTS solicitudes_pendientes_sync (
    id_local INTEGER PRIMARY KEY AUTOINCREMENT,
    client_temp_id TEXT NOT NULL UNIQUE,
    id_servicio INTEGER NOT NULL,
    fecha_servicio TEXT NOT NULL,
    hora_servicio TEXT NOT NULL,
    direccion_atencion TEXT NOT NULL,
    accion TEXT NOT NULL DEFAULT 'create',
    intentos INTEGER NOT NULL DEFAULT 0,
    ultimo_error TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE INDEX IF NOT EXISTS idx_servicios_updated_at
    ON servicios(updated_at);

CREATE INDEX IF NOT EXISTS idx_solicitudes_usuario_updated_at
    ON solicitudes(id_usuario, updated_at);

CREATE INDEX IF NOT EXISTS idx_solicitudes_sync_status
    ON solicitudes(sync_status);

CREATE INDEX IF NOT EXISTS idx_solicitudes_pendientes_status
    ON solicitudes_pendientes_sync(accion, updated_at);

INSERT OR IGNORE INTO sync_metadata (sync_key, last_sync_at)
VALUES
    ('servicios', NULL),
    ('solicitudes', NULL),
    ('personal_limpieza', NULL);
