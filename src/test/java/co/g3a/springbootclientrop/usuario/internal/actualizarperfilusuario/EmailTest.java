package co.g3a.springbootclientrop.usuario.internal.actualizarperfilusuario;

import co.g3a.functionalrop.core.Result;
import co.g3a.springbootclientrop.usuario.internal.actualizarperfilusuario.ActualizarPerfilUsuarioApiErrorResponseBuilder.ErrorDefinitions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EmailTest {

    @Test
    void debeCrearSiNoHayErroresEmailValidoExitosamente() {
        // Arrange
        String rawEmail = "Test.User@domain.com";

        // Act
        Result<Email, ErrorDefinitions> result = Email.create(rawEmail);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().value()).isEqualTo("test.user@domain.com");
    }

    @Test
    void debeFallarSiEmailEsNull() {
        // Act
        Result<Email, ErrorDefinitions> result = Email.create(null);

        // Assert
        assertThat(!result.isSuccess()).isTrue();
        assertThat(result.getError()).isInstanceOf(ErrorDefinitions.ActivationCodeError.class);
    }

    @Test
    void debeFallarSiEmailEsVacio() {
        // Act
        Result<Email, ErrorDefinitions> result = Email.create("   ");

        // Assert
        assertThat(!result.isSuccess()).isTrue();
    }

    @Test
    void debeFallarSiEmailTieneFormatoInvalido() {
        // Act
        Result<Email, ErrorDefinitions> result = Email.create("invalid-detail");

        // Assert
        assertThat(!result.isSuccess()).isTrue();
        assertThat(result.getError()).isInstanceOf(ErrorDefinitions.ActivationCodeError.class);
    }

    @Test
    void debeFallarSiEmailTieneDominioNoPermitido() {
        // Arrange
        String email = "user@example.com";

        // Act
        Result<Email, ErrorDefinitions> result = Email.create(email);

        // Assert
        assertThat(!result.isSuccess()).isTrue();
        assertThat(result.getError()).isInstanceOf(ErrorDefinitions.ActivationCodeError.class);
    }

    @Test
    void debeFallarSiEmailContienePalabraFail() {
        // Arrange
        String email = "userfail@domain.com";

        // Act
        Result<Email, ErrorDefinitions> result = Email.create(email);

        // Assert
        assertThat(!result.isSuccess()).isTrue();
        assertThat(result.getError()).isInstanceOf(ErrorDefinitions.EmailInvalid.class);
    }
}