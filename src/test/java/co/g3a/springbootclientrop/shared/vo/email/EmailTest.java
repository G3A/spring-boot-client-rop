package co.g3a.springbootclientrop.shared.vo.email;

import co.g3a.functionalrop.core.Result;
import co.g3a.springbootclientrop.shared.domain.vo.email.Email;
import co.g3a.springbootclientrop.shared.domain.vo.email.EmailError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    @DisplayName("Debe mapear BlancoOVacio a EmailBlank con detalle")
    void blancoOVacioDebeMapearAEmailBlank() {
        Result<Email, EmailError> result = Email.create("  "); // email en blanco

        assertFalse(result.isSuccess());

        assertEquals("El e‑mail no puede estar vacío o en blanco", result.getError().detail());
    }

    @Test
    @DisplayName("Debe mapear FormatoMalo a EmailInvalid con detalle")
    void formatoMaloDebeMapearAEmailInvalid() {
        Result<Email, EmailError> result = Email.create("correo-invalid");

        assertFalse(result.isSuccess());


        assertEquals("El formato del e‑mail es inválido: correo-invalid", result.getError().detail());
    }

    @Test
    @DisplayName("Debe mapear DominioNoPermitido a EmailInvalid con detalle")
    void dominioNoPermitidoDebeMapearAEmailInvalid() {
        Result<Email, EmailError> result = Email.create("usuario@example.com");

        assertFalse(result.isSuccess());


        assertEquals("Dominio no permitido: usuario@example.com", result.getError().detail());
    }

    @Test
    @DisplayName("Debe mapear ContenidoInvalido a EmailInvalid con detalle")
    void contenidoInvalidoDebeMapearAEmailInvalid() {
        Result<Email, EmailError> result = Email.create("fail@dominio.com");

        assertFalse(result.isSuccess());

        assertEquals("El correo no puede contener la palabra fail: fail@dominio.com", result.getError().detail());
    }

    @Test
    @DisplayName("Debe crear correctamente un Email válido")
    void emailValidoDebeCrearCorrectamente() {
        Result<Email, EmailError> result = Email.create("usuario@dominio.com");

        assertTrue(result.isSuccess());
        assertEquals("usuario@dominio.com", result.getValue().value());
    }
}