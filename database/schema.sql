-- Etapa 1: modelo preliminar de préstamos de equipos.
CREATE DATABASE IF NOT EXISTS examen_progra3
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_spanish_ci;

USE examen_progra3;

CREATE TABLE IF NOT EXISTS solicitante (
    identificacion VARCHAR(30) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL,
    tipo VARCHAR(15) NOT NULL,

    CONSTRAINT uq_solicitante_correo
        UNIQUE (correo),

    CONSTRAINT ck_solicitante_tipo
        CHECK (tipo IN ('ESTUDIANTE', 'FUNCIONARIO'))
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS equipo (
    codigo VARCHAR(20) PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',

    CONSTRAINT ck_equipo_estado
        CHECK (
            estado IN (
                'DISPONIBLE',
                'PRESTADO',
                'EN_REPARACION'
            )
        )
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS prestamo (
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
        CHECK (
            fecha_devolucion_real IS NULL
            OR fecha_devolucion_real >= fecha_prestamo
        ),

    CONSTRAINT ck_prestamo_devolucion
        CHECK (
            (estado = 'ACTIVO' AND fecha_devolucion_real IS NULL)
            OR
            (estado = 'DEVUELTO' AND fecha_devolucion_real IS NOT NULL)
        )
) ENGINE = InnoDB;
