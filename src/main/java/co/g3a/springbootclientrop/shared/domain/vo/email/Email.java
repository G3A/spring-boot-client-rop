package co.g3a.springbootclientrop.shared.domain.vo.email;

import co.g3a.functionalrop.core.Result;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
public record Email(String value) {

    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    private static final String DOMINIO_NO_PERMITIDO = "@example.com";

    public static Result<Email, EmailError> create(String rawEmail) {
        return asegurarNoVacio(rawEmail)
                .flatMap(Email::normalizar)
                .flatMap(Email::asegurarFormatoValido)
                .flatMap(Email::asegurarDominioValido)
                .flatMap(Email::asegurarContenidoValido)
                .flatMap(Email::build);
    }

    private static Result<String, EmailError> normalizar(String rawEmail) {
        return Result.success(rawEmail.trim().toLowerCase());
    }


    private static Result<String, EmailError> asegurarNoVacio(String rawEmail) {
        if (rawEmail == null || rawEmail.isBlank()) {
            var error = new EmailError.BlancoOVacio();
            log.error(error.detail());
            return Result.failure(error);
        }
        return Result.success(rawEmail);
    }



    private static Result<String, EmailError> asegurarFormatoValido(String email) {
        if (!EMAIL_REGEX.matcher(email).matches()) {
            var error = new EmailError.FormatoMalo(email);
            log.error(error.detail());
            return Result.failure(error);
        }
        return Result.success(email);
    }

    private static Result<String, EmailError> asegurarDominioValido(String email) {
        if (email.endsWith(DOMINIO_NO_PERMITIDO)) {
            var error = new EmailError.DominioNoPermitido(email);
            log.error(error.detail());
            return Result.failure(error);
        }
        return Result.success(email);
    }

    private static Result<String, EmailError> asegurarContenidoValido(String email) {
        if (email.contains("fail")) {
            var error = new EmailError.ContenidoInvalido(email);
            log.error(error.detail());
            return Result.failure(error);
        }
        return Result.success(email);
    }

    private static Result<Email, EmailError> build(String email){
        return Result.success(new Email(email));
    }

    @Override
    public String toString() {
        return value;
    }
}