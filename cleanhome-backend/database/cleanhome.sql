-- Crea la base de datos principal del backend.
CREATE DATABASE IF NOT EXISTS CleanHomeDB;
USE CleanHomeDB;

-- Tabla de roles del sistema (Admin / Cliente).
CREATE TABLE IF NOT EXISTS roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(30) NOT NULL UNIQUE,
    descripcion VARCHAR(100)
) ENGINE=InnoDB;

-- Usuarios registrados: clientes y administradores.
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    direccion VARCHAR(200),
    password_hash VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
) ENGINE=InnoDB;

-- Catalogo de servicios ofertados por la empresa.
CREATE TABLE IF NOT EXISTS servicios (
    id_servicio INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    duracion_estimada VARCHAR(50),
    precio DECIMAL(10,2) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Personal de limpieza disponible para asignaciones.
CREATE TABLE IF NOT EXISTS personal_limpieza (
    id_personal INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    disponible BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Solicitudes de limpieza creadas por clientes.
CREATE TABLE IF NOT EXISTS solicitudes (
    id_solicitud INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_servicio INT NOT NULL,
    id_personal INT NULL,
    fecha_servicio DATE NOT NULL,
    hora_servicio TIME NOT NULL,
    direccion_atencion VARCHAR(200) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'Pendiente',
    client_temp_id VARCHAR(80) NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio),
    FOREIGN KEY (id_personal) REFERENCES personal_limpieza(id_personal),
    UNIQUE KEY uq_solicitudes_usuario_client_temp (id_usuario, client_temp_id)
) ENGINE=InnoDB;

-- Semillas de roles base.
INSERT INTO roles (nombre_rol, descripcion) VALUES
('Admin', 'Administrador del sistema'),
('Cliente', 'Usuario que solicita servicios')
ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

-- Semillas de servicios iniciales.
INSERT INTO servicios (nombre, descripcion, duracion_estimada, precio, activo)
SELECT 'Limpieza General', 'Servicio básico de limpieza para hogar u oficina', '2 a 4 horas', 40.00, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM servicios WHERE nombre = 'Limpieza General'
);

INSERT INTO servicios (nombre, descripcion, duracion_estimada, precio, activo)
SELECT 'Limpieza Profunda', 'Servicio completo de limpieza detallada', '4 a 8 horas', 65.00, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM servicios WHERE nombre = 'Limpieza Profunda'
);

INSERT INTO servicios (nombre, descripcion, duracion_estimada, precio, activo)
SELECT 'Limpieza de Ventanas', 'Limpieza especializada de ventanas y cristales', '1 a 3 horas', 35.00, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM servicios WHERE nombre = 'Limpieza de Ventanas'
);

-- Semillas de personal de limpieza.
INSERT INTO personal_limpieza (nombre, telefono, disponible)
SELECT 'Ana Silva', '7000-0001', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM personal_limpieza WHERE telefono = '7000-0001'
);

INSERT INTO personal_limpieza (nombre, telefono, disponible)
SELECT 'Carlos Ruiz', '7000-0002', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM personal_limpieza WHERE telefono = '7000-0002'
);

INSERT INTO personal_limpieza (nombre, telefono, disponible)
SELECT 'Fernanda López', '7000-0003', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM personal_limpieza WHERE telefono = '7000-0003'
);

-- Usuario administrador por defecto (password: Admin123!).
INSERT INTO usuarios (nombre, correo, telefono, direccion, password_hash, id_rol)
SELECT
    'Administrador CleanHome',
    'admin@cleanhome.com',
    '7000-0101',
    'San Salvador',
    '$2b$10$gHxBFeX.bbwLIwLxNXEfn.2CfnMoYZSiJvJyy7N4FhjPsRJWfWa06',
    (SELECT id_rol FROM roles WHERE nombre_rol = 'Admin')
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE correo = 'admin@cleanhome.com'
);
