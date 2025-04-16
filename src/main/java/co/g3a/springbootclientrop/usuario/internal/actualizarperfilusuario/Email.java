package co.g3a.springbootclientrop.usuario.internal.actualizarperfilusuario;

import co.g3a.springbootclientrop.usuario.internal.actualizarperfilusuario.ActualizarPerfilUsuarioApiErrorResponseBuilder.ErrorDefinitions;
import co.g3a.functionalrop.core.Result;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
public record Email(String value) {

    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    private static final String DOMINIO_NO_PERMITIDO = "@example.com";

    public static Result<Email, ErrorDefinitions> create(String rawEmail) {
        return asegurarNoVacio(rawEmail)
                .flatMap(Email::normalizar)
                .flatMap(Email::asegurarFormato)
                .flatMap(Email::asegurarDominioValido)
                .flatMap(Email::asegurarContenidoValido)
                .flatMap(Email::build);
    }


    private static Result<String, ErrorDefinitions> asegurarNoVacio(String rawEmail) {
        if (rawEmail == null || rawEmail.isBlank()) {
            var error = new ErrorDefinitions.ActivationCodeError("El email no puede estar vacío.");
            log.error(error.detail());
            return Result.failure(error);
        }
        return Result.success(rawEmail);
    }

    private static Result<String, ErrorDefinitions> normalizar(String rawEmail) {
        return Result.success(rawEmail.trim().toLowerCase());
    }

    private static Result<String, ErrorDefinitions> asegurarFormato(String email) {
        if (!EMAIL_REGEX.matcher(email).matches()) {
            var error = new ErrorDefinitions.ActivationCodeError(email.formatted("El formato del detail es inválido: %s"));
            log.error(error.detail());
            return Result.failure(error);
        }
        return Result.success(email);
    }

    private static Result<String, ErrorDefinitions> asegurarDominioValido(String email) {
        if (email.endsWith(DOMINIO_NO_PERMITIDO)) {
            var error = new ErrorDefinitions.ActivationCodeError(email.formatted("Dominio no permitido: %s"));
            log.error(error.detail());
            return Result.failure(error);
        }
        return Result.success(email);
    }

    private static Result<String, ErrorDefinitions> asegurarContenidoValido(String email) {
        if (email.contains("fail")) {
            var error = new ErrorDefinitions.EmailInvalid("El correo no puede contener la palabra fail: %s".formatted(email));
            log.error(error.detail());
            return Result.failure(error);
        }
        return Result.success(email);
    }

    private static Result<Email, ErrorDefinitions> build(String email){
        return Result.success(new Email(email));
    }

    @Override
    public String toString() {
        return value;
    }
}