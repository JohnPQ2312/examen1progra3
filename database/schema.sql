-- Etapa 1: modelo preliminar de préstamos de equipos.
DROP TABLE IF EXISTS prestamo;
DROP TABLE IF EXISTS equipo;
DROP TABLE IF EXISTS solicitante;

CREATE TABLE solicitante (
    identificacion VARCHAR(30) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL,
    tipo VARCHAR(15) NOT NULL,

    CONSTRAINT uq_solicitante_correo
        UNIQUE (correo),

    CONSTRAINT ck_solicitante_tipo
        CHECK (tipo IN ('ESTUDIANTE', 'FUNCIONARIO'))
) ENGINE = InnoDB;

CREATE TABLE equipo (
    codigo VARCHAR(20) PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',

    CONSTRAINT ck_equipo_estado
        CHECK (estado IN ('DISPONIBLE', 'PRESTADO', 'EN_REPARACION'))
) ENGINE = InnoDB;

CREATE TABLE prestamo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    solicitante_id VARCHAR(30) NOT NULL,
    equipo_codigo VARCHAR(20) NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion_esperada DATE NOT NULL,
    fecha_devolucion_real DATE NULL,
    estado VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',

    CONSTRAINT fk_prestamo_solicitante
        FOREIGN KEY (solicitante_id)
        REFERENCES solicitante (identificacion),

    CONSTRAINT fk_prestamo_equipo
        FOREIGN KEY (equipo_codigo)
        REFERENCES equipo (codigo),

    CONSTRAINT ck_prestamo_estado
        CHECK (estado IN ('ACTIVO', 'DEVUELTO')),

    CONSTRAINT ck_prestamo_fecha_esperada
        CHECK (fecha_devolucion_esperada > fecha_prestamo),

    CONSTRAINT ck_prestamo_fecha_real
        CHECK (fecha_devolucion_real IS NULL
               OR fecha_devolucion_real >= fecha_prestamo),

    CONSTRAINT ck_prestamo_devolucion
        CHECK (
            (estado = 'ACTIVO' AND fecha_devolucion_real IS NULL)
            OR
            (estado = 'DEVUELTO' AND fecha_devolucion_real IS NOT NULL)
        )
) ENGINE = InnoDB;

-- Datos de prueba
INSERT INTO solicitante (identificacion, nombre, correo, tipo) VALUES
('1-1111-1111', 'Ana Mora Solís',       'ana.mora@est.una.ac.cr',     'ESTUDIANTE'),
('2-2222-2222', 'Carlos Vega Rojas',    'carlos.vega@est.una.ac.cr',  'ESTUDIANTE'),
('3-3333-3333', 'Daniela Ruiz Castro',  'daniela.ruiz@est.una.ac.cr', 'ESTUDIANTE'),
('4-4444-4444', 'Mario Jiménez Alfaro', 'mario.jimenez@una.ac.cr',    'FUNCIONARIO'),
('5-5555-5555', 'Laura Chaves Brenes',  'laura.chaves@una.ac.cr',     'FUNCIONARIO');

INSERT INTO equipo (codigo, descripcion, tipo, estado) VALUES
('EQ-01', 'Laptop Dell Latitude 5440',  'Laptop',    'DISPONIBLE'),
('EQ-02', 'Laptop Lenovo ThinkPad E14', 'Laptop',    'DISPONIBLE'),
('EQ-03', 'Proyector Epson PowerLite',  'Proyector', 'DISPONIBLE'),
('EQ-04', 'Tableta Samsung Galaxy Tab', 'Tableta',   'EN_REPARACION'),
('EQ-05', 'Cámara Canon EOS M50',       'Cámara',    'PRESTADO'),
('EQ-06', 'Laptop HP ProBook 450',      'Laptop',    'PRESTADO'),
('EQ-07', 'Parlante JBL Charge 5',      'Audio',     'DISPONIBLE'),
('EQ-08', 'Proyector BenQ MS560',       'Proyector', 'DISPONIBLE');

-- Ana (estudiante) ya tiene 2 préstamos activos: sirve para probar el límite (RN-02).
-- Mario tiene un préstamo ya devuelto: sirve para el historial.
INSERT INTO prestamo (solicitante_id, equipo_codigo, fecha_prestamo,
                      fecha_devolucion_esperada, fecha_devolucion_real, estado) VALUES
('1-1111-1111', 'EQ-05', '2026-10-05', '2026-10-20', NULL,         'ACTIVO'),
('1-1111-1111', 'EQ-06', '2026-10-06', '2026-10-21', NULL,         'ACTIVO'),
('4-4444-4444', 'EQ-01', '2026-09-20', '2026-09-27', '2026-09-26', 'DEVUELTO');
