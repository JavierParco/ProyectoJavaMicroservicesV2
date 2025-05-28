package com.tcs.microservice_api.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Cliente createValidCliente(Long id, String identificacion, String clienteId) {
        return Cliente.builder()
                .id(id)
                .nombre("Nombre Valido")
                .genero("Masculino")
                .edad(30)
                .identificacion(identificacion) // "IDValido123"
                .direccion("Direccion Valida 123")
                .telefono("0987654321")
                .clienteId(clienteId) // "CLIValido001"
                .contrasena("PassVal123")
                .estado(true)
                .cuentas(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Prueba de instanciación y valores básicos con Builder")
    void testClienteInstantiationAndGetters() {
        List<Cuenta> cuentas = new ArrayList<>();
        Cuenta c1 = Cuenta.builder().numeroCuenta("123").build();
        cuentas.add(c1);

        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Maria Jose")
                .genero("Femenino")
                .edad(28)
                .identificacion("IDMJ1995")
                .direccion("Calle Sol 456")
                .telefono("0991234567")
                .clienteId("MJ001")
                .contrasena("mypassword")
                .estado(true)
                .cuentas(cuentas)
                .build();

        assertNotNull(cliente);
        assertEquals(1L, cliente.getId());
        assertEquals("Maria Jose", cliente.getNombre());
        assertEquals("Femenino", cliente.getGenero());
        assertEquals(28, cliente.getEdad());
        assertEquals("IDMJ1995", cliente.getIdentificacion());
        assertEquals("Calle Sol 456", cliente.getDireccion());
        assertEquals("0991234567", cliente.getTelefono());
        assertEquals("MJ001", cliente.getClienteId());
        assertEquals("mypassword", cliente.getContrasena());
        assertTrue(cliente.getEstado());
        assertNotNull(cliente.getCuentas());
        assertEquals(1, cliente.getCuentas().size());
        assertEquals("123", cliente.getCuentas().get(0).getNumeroCuenta());
    }

    @Test
    @DisplayName("Prueba de setters individuales")
    void testClienteSetters() {
        Cliente cliente = Cliente.builder().build(); // Inicia con builder vacío

        cliente.setId(2L);
        cliente.setNombre("Carlos Ruiz");
        cliente.setGenero("Masculino");
        cliente.setEdad(40);
        cliente.setIdentificacion("IDCR1983");
        cliente.setDireccion("Av. Luna 789");
        cliente.setTelefono("0988887777");
        cliente.setClienteId("CR002");
        cliente.setContrasena("anotherPass");
        cliente.setEstado(false);
        List<Cuenta> cuentasNuevas = List.of(Cuenta.builder().numeroCuenta("456").build());
        cliente.setCuentas(cuentasNuevas);

        assertEquals(2L, cliente.getId());
        assertEquals("Carlos Ruiz", cliente.getNombre());
        assertEquals("Masculino", cliente.getGenero());
        assertEquals(40, cliente.getEdad());
        assertEquals("IDCR1983", cliente.getIdentificacion());
        assertEquals("Av. Luna 789", cliente.getDireccion());
        assertEquals("0988887777", cliente.getTelefono());
        assertEquals("CR002", cliente.getClienteId());
        assertEquals("anotherPass", cliente.getContrasena());
        assertFalse(cliente.getEstado());
        assertEquals(cuentasNuevas, cliente.getCuentas());
    }

    @Nested
    @DisplayName("Pruebas del Contrato Equals y HashCode")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Reflexividad: un objeto debe ser igual a sí mismo")
        void testEquals_Reflexive() {
            Cliente cliente1 = createValidCliente(1L, "ID001", "CLI001");
            assertEquals(cliente1, cliente1);
        }

        @Test
        @DisplayName("Simetría: si x.equals(y) es true, entonces y.equals(x) debe ser true")
        void testEquals_Symmetric() {
            Cliente cliente1 = createValidCliente(1L, "ID001", "CLI001");
            Cliente cliente2 = createValidCliente(1L, "ID001", "CLI001");
            assertTrue(cliente1.equals(cliente2) && cliente2.equals(cliente1));
            assertEquals(cliente1.hashCode(), cliente2.hashCode());
        }

        @Test
        @DisplayName("Transitividad: si x.equals(y) y y.equals(z), entonces x.equals(z)")
        void testEquals_Transitive() {
            Cliente cliente1 = createValidCliente(1L, "ID001", "CLI001");
            Cliente cliente2 = createValidCliente(1L, "ID001", "CLI001");
            Cliente cliente3 = createValidCliente(1L, "ID001", "CLI001");
            assertTrue(cliente1.equals(cliente2));
            assertTrue(cliente2.equals(cliente3));
            assertTrue(cliente1.equals(cliente3));
            assertEquals(cliente1.hashCode(), cliente3.hashCode());
        }

        @Test
        @DisplayName("Consistencia: múltiples invocaciones de x.equals(y) retornan lo mismo")
        void testEquals_Consistent() {
            Cliente cliente1 = createValidCliente(1L, "ID001", "CLI001");
            Cliente cliente2 = createValidCliente(1L, "ID001", "CLI001");
            assertEquals(cliente1, cliente2);
            assertEquals(cliente1, cliente2);
        }

        @Test
        @DisplayName("Igualdad con nulo: x.equals(null) debe retornar false")
        void testEquals_Null() {
            Cliente cliente1 = createValidCliente(1L, "ID001", "CLI001");
            assertNotEquals(null, cliente1);
        }

        @Test
        @DisplayName("Desigualdad con diferente tipo de objeto")
        void testEquals_DifferentType() {
            Cliente cliente1 = createValidCliente(1L, "ID001", "CLI001");
            Object otroObjeto = new Object();
            assertNotEquals(cliente1, otroObjeto);
        }

        @Test
        @DisplayName("Desigualdad cuando el ID es diferente")
        void testNotEquals_DifferentId() {
            Cliente cliente1 = createValidCliente(1L, "ID001", "CLI001");
            Cliente cliente2 = createValidCliente(2L, "ID001", "CLI001"); // Diferente ID
            assertNotEquals(cliente1, cliente2);
        }

        @Test
        @DisplayName("Desigualdad cuando un campo de Persona es diferente")
        void testNotEquals_DifferentPersonaField() {
            Cliente cliente1 = createValidCliente(1L, "ID001", "CLI001");
            Cliente cliente2 = createValidCliente(1L, "ID002", "CLI001"); // Diferente identificación
            assertNotEquals(cliente1, cliente2);
        }

        @Test
        @DisplayName("Desigualdad cuando un campo de Cliente es diferente")
        void testNotEquals_DifferentClienteField() {
            Cliente cliente1 = createValidCliente(1L, "ID001", "CLI001");
            Cliente cliente2 = createValidCliente(1L, "ID001", "CLI002"); // Diferente clienteId
            assertNotEquals(cliente1, cliente2);
        }
        @Test
        @DisplayName("Objetos iguales deben tener el mismo hashCode")
        void testHashCode_EqualObjects() {
            Cliente cliente1 = createValidCliente(1L, "ID001", "CLI001");
            Cliente cliente2 = createValidCliente(1L, "ID001", "CLI001");
            assertEquals(cliente1.hashCode(), cliente2.hashCode());
        }
    }

    @Test
    @DisplayName("Prueba del método toString()")
    void testToString() {
        Cliente cliente = createValidCliente(1L, "IDTestToString", "CLITestToString");
        String clienteString = cliente.toString();

        assertNotNull(clienteString);
        assertTrue(clienteString.contains("id=1"));
        assertTrue(clienteString.contains("nombre=Nombre Valido"));
        assertTrue(clienteString.contains("identificacion=IDTestToString"));
        assertTrue(clienteString.contains("clienteId=CLITestToString"));
        // Como toString incluye los campos de Persona (callSuper = true),
        // y los de Cliente, es suficiente con verificar algunos de ellos.
    }

    @Nested
    @DisplayName("Pruebas de Validación de Bean (JSR 303)")
    class BeanValidationTests {

        @Test
        @DisplayName("Cliente válido no debe tener violaciones de constraints")
        void testValidCliente_NoViolations() {
            Cliente cliente = createValidCliente(1L, "IDValido123", "CLIValido001");
            Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
            assertTrue(violations.isEmpty(), "Un cliente válido no debería tener violaciones");
        }

        @Test
        @DisplayName("Violación cuando el nombre es nulo")
        void testValidation_NullNombre() {
            Cliente cliente = createValidCliente(1L, "IDValido123", "CLIValido001");
            cliente.setNombre(null); // Campo de Persona
            Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
            assertFalse(violations.isEmpty());
            ConstraintViolation<Cliente> violation = violations.iterator().next();
            assertEquals("El nombre no puede estar vacío", violation.getMessage());
            assertEquals("nombre", violation.getPropertyPath().toString());
        }

        @Test
        @DisplayName("Violación cuando la identificación es nula")
        void testValidation_NullIdentificacion() {
            Cliente cliente = createValidCliente(1L, "IDValido123", "CLIValido001");
            cliente.setIdentificacion(null); // Campo de Persona
            Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
            assertFalse(violations.isEmpty());
            ConstraintViolation<Cliente> violation = violations.iterator().next();
            assertEquals("La identificación no puede estar vacía", violation.getMessage());
            assertEquals("identificacion", violation.getPropertyPath().toString());
        }

        @Test
        @DisplayName("Violación cuando la identificación excede el tamaño máximo")
        void testValidation_IdentificacionTooLong() {
            Cliente cliente = createValidCliente(1L, "IDValido123", "CLIValido001");
            cliente.setIdentificacion("123456789012345678901"); // 21 caracteres, max es 20
            Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
            assertFalse(violations.isEmpty());
            ConstraintViolation<Cliente> violation = violations.iterator().next();
            assertEquals("La identificación no puede exceder los 20 caracteres", violation.getMessage());
        }

        @Test
        @DisplayName("Violación cuando clienteId es nulo")
        void testValidation_NullClienteId() {
            Cliente cliente = createValidCliente(1L, "IDValido123", "CLIValido001");
            cliente.setClienteId(null); // Campo de Cliente
            Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
            assertFalse(violations.isEmpty());
            ConstraintViolation<Cliente> violation = violations.iterator().next();
            assertEquals("El clienteId no puede estar vacío", violation.getMessage());
            assertEquals("clienteId", violation.getPropertyPath().toString());
        }

        @Test
        @DisplayName("Violación cuando la contraseña es nula")
        void testValidation_NullContrasena() {
            Cliente cliente = createValidCliente(1L, "IDValido123", "CLIValido001");
            cliente.setContrasena(null); // Campo de Cliente
            Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
            assertFalse(violations.isEmpty());
            ConstraintViolation<Cliente> violation = violations.iterator().next();
            assertEquals("La contraseña no puede estar vacía", violation.getMessage());
            assertEquals("contrasena", violation.getPropertyPath().toString());
        }

        @Test
        @DisplayName("Violación cuando la contraseña es muy corta")
        void testValidation_ContrasenaTooShort() {
            Cliente cliente = createValidCliente(1L, "IDValido123", "CLIValido001");
            cliente.setContrasena("123"); // Min es 4
            Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
            assertFalse(violations.isEmpty());
            ConstraintViolation<Cliente> violation = violations.iterator().next();
            assertEquals("La contraseña debe tener al menos 4 caracteres", violation.getMessage());
        }

        @Test
        @DisplayName("Violación cuando el estado es nulo")
        void testValidation_NullEstado() {
            Cliente cliente = createValidCliente(1L, "IDValido123", "CLIValido001");
            cliente.setEstado(null);
            Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
            assertFalse(violations.isEmpty());
            ConstraintViolation<Cliente> violation = violations.iterator().next();
            assertEquals("El estado no puede ser nulo", violation.getMessage());
            assertEquals("estado", violation.getPropertyPath().toString());
        }
    }
}
