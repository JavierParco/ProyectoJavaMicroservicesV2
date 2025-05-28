-- Eliminar tablas existentes para evitar problemas de insercion
DROP TABLE IF EXISTS movimiento CASCADE;
DROP TABLE IF EXISTS cuenta CASCADE;
DROP TABLE IF EXISTS cliente CASCADE;
DROP TABLE IF EXISTS persona CASCADE;

-- Tabla Persona
CREATE TABLE persona (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    genero VARCHAR(50),
    edad INT,
    identificacion VARCHAR(20) UNIQUE NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(20)
);

-- Tabla Cliente (hereda de Persona)
CREATE TABLE cliente (
    id BIGINT PRIMARY KEY REFERENCES persona(id) ON DELETE CASCADE, -- Clave primaria y foránea referenciando a personas
    cliente_id VARCHAR(100) UNIQUE NOT NULL, -- Identificador único de cliente (lógica de negocio)
    contrasena VARCHAR(255) NOT NULL, -- En una aplicación real, esto debería estar hasheado
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

-- Enum para Tipo de Cuenta (simulado con CHECK constraint o se puede manejar en la aplicación)
-- CREATE TYPE tipo_cuenta_enum AS ENUM ('AHORRO', 'CORRIENTE');

-- Tabla Cuenta
CREATE TABLE cuenta (
    id BIGSERIAL PRIMARY KEY,
    numero_cuenta VARCHAR(50) UNIQUE NOT NULL,
    tipo_cuenta VARCHAR(20) NOT NULL CHECK (tipo_cuenta IN ('AHORRO', 'CORRIENTE')), -- 'AHORRO' o 'CORRIENTE'
    saldo_inicial NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    saldo_actual NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    cliente_id_fk BIGINT NOT NULL, -- Referencia al ID de la tabla clientes (que es el mismo que personas.id)
    CONSTRAINT fk_cliente
        FOREIGN KEY(cliente_id_fk)
        REFERENCES cliente(id)
        ON DELETE CASCADE
);

-- Enum para Tipo de Movimiento (simulado con CHECK constraint o se puede manejar en la aplicación)
-- CREATE TYPE tipo_movimiento_enum AS ENUM ('DEBITO', 'CREDITO');

-- Tabla Movimiento
CREATE TABLE movimiento (
    id BIGSERIAL PRIMARY KEY,
    fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_movimiento VARCHAR(10) NOT NULL CHECK (tipo_movimiento IN ('DEBITO', 'CREDITO')), -- 'DEBITO' o 'CREDITO'
    valor NUMERIC(19, 2) NOT NULL,
    saldo_post_movimiento NUMERIC(19, 2) NOT NULL, -- Saldo de la cuenta DESPUÉS del movimiento
    cuenta_id_fk BIGINT NOT NULL,
    CONSTRAINT fk_cuenta
        FOREIGN KEY(cuenta_id_fk)
        REFERENCES cuenta(id)
        ON DELETE CASCADE
);

-- Índices para mejorar el rendimiento de las búsquedas
CREATE INDEX idx_cliente_identificacion ON persona(identificacion);
CREATE INDEX idx_cliente_cliente_id ON cliente(cliente_id);
CREATE INDEX idx_cuenta_numero_cuenta ON cuenta(numero_cuenta);
CREATE INDEX idx_movimiento_fecha ON movimiento(fecha);

-- Comentarios adicionales:
-- - `BIGSERIAL` en PostgreSQL es un `BIGINT` autoincremental, ideal para claves primarias.
-- - `NUMERIC(19, 2)` es adecuado para valores monetarios.
-- - Las constraints `CHECK` para enums son una forma de implementarlos a nivel de BD.
-- - `ON DELETE CASCADE` se usa para que si se elimina un cliente, se eliminen sus cuentas y movimientos asociados.



-- #############################################################################
-- # SCRIPT DE INSERCIÓN DE DATOS DE MUESTRA                                   #
-- #############################################################################

-- Limpiar datos existentes de las tablas si se ejecuta múltiples veces 

-- #############################################################################
-- # PERSONAS Y CLIENTES                                                       #
-- #############################################################################

-- Persona/Cliente 1: Jose Lema
WITH persona_jose AS (
    INSERT INTO personas (nombre, genero, edad, identificacion, direccion, telefono)
    VALUES ('Jose Lema', 'Masculino', 35, '1712345678', 'Otavalo sn y principal', '098254785')
    RETURNING id
)
INSERT INTO clientes (id, cliente_id, contrasena, estado)
SELECT id, 'JL001', 'password123', TRUE FROM persona_jose; -- Contraseña en texto plano (SOLO PARA PRUEBAS)

-- Persona/Cliente 2: Marianela Montalvo
WITH persona_marianela AS (
    INSERT INTO personas (nombre, genero, edad, identificacion, direccion, telefono)
    VALUES ('Marianela Montalvo', 'Femenino', 28, '1787654321', 'Amazonas y NNUU', '097548965')
    RETURNING id
)
INSERT INTO clientes (id, cliente_id, contrasena, estado)
SELECT id, 'MM002', 'securePass', TRUE FROM persona_marianela;

-- Persona/Cliente 3: Juan Osorio
WITH persona_juan AS (
    INSERT INTO personas (nombre, genero, edad, identificacion, direccion, telefono)
    VALUES ('Juan Osorio', 'Masculino', 40, '1700000000', '13 junio y Equinoccial', '098874587')
    RETURNING id
)
INSERT INTO clientes (id, cliente_id, contrasena, estado)
SELECT id, 'JO003', 'mypwd', TRUE FROM persona_juan;


-- #############################################################################
-- # CUENTAS                                                                   #
-- #############################################################################

-- Cuentas para Jose Lema (cliente_id = 'JL001')
-- Asumimos que el ID de persona/cliente para Jose Lema es 1 si se ejecutan secuencialmente desde BD limpia.
-- Para mayor robustez, seleccionamos el ID basado en 'cliente_id'.

-- Cuenta 1.1 para Jose Lema
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_actual, estado, cliente_id_fk)
SELECT '478758', 'AHORRO', 2000.00, 2000.00, TRUE, c.id
FROM clientes c WHERE c.cliente_id = 'JL001';

-- Cuenta 1.2 para Jose Lema
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_actual, estado, cliente_id_fk)
SELECT '495878', 'AHORRO', 500.00, 500.00, TRUE, c.id
FROM clientes c WHERE c.cliente_id = 'JL001';


-- Cuentas para Marianela Montalvo (cliente_id = 'MM002')
-- Asumimos ID 2 para Marianela.

-- Cuenta 2.1 para Marianela Montalvo
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_actual, estado, cliente_id_fk)
SELECT '225487', 'CORRIENTE', 100.00, 100.00, TRUE, c.id
FROM clientes c WHERE c.cliente_id = 'MM002';


-- Cuentas para Juan Osorio (cliente_id = 'JO003')
-- Asumimos ID 3 para Juan.

-- Cuenta 3.1 para Juan Osorio
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_actual, estado, cliente_id_fk)
SELECT '330011', 'AHORRO', 10000.00, 10000.00, TRUE, c.id
FROM clientes c WHERE c.cliente_id = 'JO003';


-- #############################################################################
-- # MOVIMIENTOS                                                               #
-- #############################################################################

-- Movimientos para Cuenta '478758' (Jose Lema)
-- Saldo inicial: 2000.00

-- Movimiento 1.1.1 (Crédito +575)
WITH cuenta_data AS (SELECT id, saldo_actual FROM cuentas WHERE numero_cuenta = '478758'),
     mov_val AS (SELECT 575.00 AS valor)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo_post_movimiento, cuenta_id_fk)
SELECT NOW() - INTERVAL '2 days', 'CREDITO', mv.valor, cd.saldo_actual + mv.valor, cd.id
FROM cuenta_data cd, mov_val mv;
UPDATE cuentas SET saldo_actual = saldo_actual + 575.00 WHERE numero_cuenta = '478758'; -- Saldo post: 2575.00

-- Movimiento 1.1.2 (Débito -100)
WITH cuenta_data AS (SELECT id, saldo_actual FROM cuentas WHERE numero_cuenta = '478758'),
     mov_val AS (SELECT 100.00 AS valor)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo_post_movimiento, cuenta_id_fk)
SELECT NOW() - INTERVAL '1 day', 'DEBITO', mv.valor, cd.saldo_actual - mv.valor, cd.id
FROM cuenta_data cd, mov_val mv;
UPDATE cuentas SET saldo_actual = saldo_actual - 100.00 WHERE numero_cuenta = '478758'; -- Saldo post: 2475.00


-- Movimientos para Cuenta '495878' (Jose Lema)
-- Saldo inicial: 500.00

-- Movimiento 1.2.1 (Débito -200)
WITH cuenta_data AS (SELECT id, saldo_actual FROM cuentas WHERE numero_cuenta = '495878'),
     mov_val AS (SELECT 200.00 AS valor)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo_post_movimiento, cuenta_id_fk)
SELECT NOW() - INTERVAL '3 days', 'DEBITO', mv.valor, cd.saldo_actual - mv.valor, cd.id
FROM cuenta_data cd, mov_val mv;
UPDATE cuentas SET saldo_actual = saldo_actual - 200.00 WHERE numero_cuenta = '495878'; -- Saldo post: 300.00


-- Movimientos para Cuenta '225487' (Marianela Montalvo)
-- Saldo inicial: 100.00

-- Movimiento 2.1.1 (Crédito +1000)
WITH cuenta_data AS (SELECT id, saldo_actual FROM cuentas WHERE numero_cuenta = '225487'),
     mov_val AS (SELECT 1000.00 AS valor)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo_post_movimiento, cuenta_id_fk)
SELECT NOW() - INTERVAL '5 hours', 'CREDITO', mv.valor, cd.saldo_actual + mv.valor, cd.id
FROM cuenta_data cd, mov_val mv;
UPDATE cuentas SET saldo_actual = saldo_actual + 1000.00 WHERE numero_cuenta = '225487'; -- Saldo post: 1100.00

-- Movimiento 2.1.2 (Débito -50)
WITH cuenta_data AS (SELECT id, saldo_actual FROM cuentas WHERE numero_cuenta = '225487'),
     mov_val AS (SELECT 50.00 AS valor)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo_post_movimiento, cuenta_id_fk)
SELECT NOW() - INTERVAL '1 hour', 'DEBITO', mv.valor, cd.saldo_actual - mv.valor, cd.id
FROM cuenta_data cd, mov_val mv;
UPDATE cuentas SET saldo_actual = saldo_actual - 50.00 WHERE numero_cuenta = '225487'; -- Saldo post: 1050.00

-- Mensaje de finalización
SELECT 'Script de inserción de datos de muestra ejecutado.' AS resultado;

--Consultas
--Consultar todas las personas
SELECT * FROM personas;
SELECT * FROM clientes;
SELECT * FROM movimientos;


-- Consultar todos los clientes (mostrando datos heredados de persona)
SELECT
    p.id AS persona_id,
    p.nombre,
    p.genero,
    p.edad,
    p.identificacion,
    p.direccion,
    p.telefono,
    c.cliente_id AS cliente_business_id,
    c.estado AS cliente_estado
    -- No mostrar c.contrasena
FROM
    personas p
JOIN
    clientes c ON p.id = c.id;

-- Consultar un cliente específico por su cliente_id de negocio
SELECT
    p.nombre, p.identificacion, c.cliente_id, c.estado
FROM
    personas p
JOIN
    clientes c ON p.id = c.id
WHERE c.cliente_id = 'JL001';

-- Consultar todas las cuentas
SELECT * FROM cuentas;

-- Consultar las cuentas de un cliente específico (Jose Lema - JL001)
SELECT
    cu.numero_cuenta,
    cu.tipo_cuenta,
    cu.saldo_inicial,
    cu.saldo_actual,
    cu.estado AS cuenta_estado,
    p.nombre AS nombre_cliente
FROM
    cuentas cu
JOIN
    clientes cl ON cu.cliente_id_fk = cl.id
JOIN
    personas p ON cl.id = p.id
WHERE
    cl.cliente_id = 'JL001';

-- Consultar todos los movimientos
SELECT * FROM movimientos ORDER BY fecha DESC;

-- Consultar los movimientos de una cuenta específica ('478758')
SELECT
    m.id AS movimiento_id,
    m.fecha,
    m.tipo_movimiento,
    m.valor,
    m.saldo_post_movimiento,
    c.numero_cuenta
FROM
    movimientos m
JOIN
    cuentas c ON m.cuenta_id_fk = c.id
WHERE
    c.numero_cuenta = '478758'
ORDER BY m.fecha DESC;

-- Reporte: Movimientos de un cliente (Marianela Montalvo - MM002) en un rango de fechas
-- (Simulando el query que haría el servicio, ajusta las fechas según los datos insertados)
SELECT
    m.fecha,
    p.nombre AS nombre_cliente,
    c.numero_cuenta,
    c.tipo_cuenta,
    (m.saldo_post_movimiento - CASE WHEN m.tipo_movimiento = 'CREDITO' THEN m.valor ELSE -m.valor END) AS saldo_anterior_al_movimiento, -- Cálculo del saldo inicial para este movimiento
    c.estado AS estado_cuenta,
    m.tipo_movimiento,
    m.valor AS valor_movimiento,
    m.saldo_post_movimiento AS saldo_disponible_cuenta
FROM
    movimientos m
JOIN
    cuentas c ON m.cuenta_id_fk = c.id
JOIN
    clientes cl ON c.cliente_id_fk = cl.id
JOIN
    personas p ON cl.id = p.id
WHERE
    cl.cliente_id = 'MM002' AND
    m.fecha BETWEEN (NOW() - INTERVAL '7 days') AND NOW() -- Ajusta este rango de fechas
ORDER BY
    m.fecha ASC;

-- Verificar saldo actual de las cuentas
SELECT numero_cuenta, saldo_inicial, saldo_actual FROM cuentas;


