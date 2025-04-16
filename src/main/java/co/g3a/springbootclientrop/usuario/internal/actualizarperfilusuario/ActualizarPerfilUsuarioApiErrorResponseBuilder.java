package co.g3a.springbootclientrop.usuario.internal.actualizarperfilusuario;

import co.g3a.springbootclientrop.shared.errorhandling.ApiErrorResponse;
import co.g3a.springbootclientrop.shared.errorhandling.Constantes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ActualizarPerfilUsuarioApiErrorResponseBuilder {


    private final co.g3a.springbootclientrop.shared.errorhandling.ApiErrorResponseBuilder apiErrorResponseBuilder;

    public ActualizarPerfilUsuarioApiErrorResponseBuilder(co.g3a.springbootclientrop.shared.errorhandling.ApiErrorResponseBuilder apiErrorResponseBuilder) {
        this.apiErrorResponseBuilder = apiErrorResponseBuilder;
    }

    public ResponseEntity<Object> from(ErrorDefinitions error, HttpServletRequest request) {
        int status = mapToStatus(error);
        List<ApiErrorResponse.ErrorDetail> errorsDetails = switch (error) {
            case ErrorDefinitions.MultipleErrors multi -> multi.errors().stream()
                    .map(this::toErrorDetail)
                    .toList();
            default -> List.of(toErrorDetail(error));
        };

        ApiErrorResponse response = apiErrorResponseBuilder.build(
                Constantes.LEVEL_ERROR,
                Constantes.GENERIC_ERROR_MESSAGE,
                status,
                request,
                errorsDetails
        );

        return ResponseEntity.status(status).body(response);
    }

    private int mapToStatus(ErrorDefinitions error) {
        return switch (error) {
            case ErrorDefinitions.NameBlank e -> 400;
            case ErrorDefinitions.EmailBlank e -> 400;
            case ErrorDefinitions.EmailInvalid e -> 400;
            case ErrorDefinitions.PasswordBlank e -> 400;
            case ErrorDefinitions.PasswordTooShort e -> 400;
            case ErrorDefinitions.NameTooShort e -> 400;
            case ErrorDefinitions.UnderAge e -> 400;
            case ErrorDefinitions.MultipleErrors e -> 400;

            case ErrorDefinitions.DbError e -> 500;
            case ErrorDefinitions.EmailSendError e -> 500;
            case ErrorDefinitions.ActivationCodeError e -> 400;
        };
    }

    private ApiErrorResponse.ErrorDetail toErrorDetail(ErrorDefinitions error) {
        String code = mapAppErrorToCode(error);
        String message = toReturnMessage(error);
        return new ApiErrorResponse.ErrorDetail(code, message);
    }


    private String mapAppErrorToCode(ErrorDefinitions error) {
        return switch (error) {
            case ErrorDefinitions.NameBlank __         -> "NAME_BLANK";
            case ErrorDefinitions.EmailBlank __        -> "EMAIL_BLANK";
            case ErrorDefinitions.EmailInvalid __      -> "EMAIL_INVALID";
            case ErrorDefinitions.PasswordBlank __     -> "PASSWORD_BLANK";
            case ErrorDefinitions.PasswordTooShort __  -> "PASSWORD_TOO_SHORT";
            case ErrorDefinitions.NameTooShort __      -> "NAME_TOO_SHORT";
            case ErrorDefinitions.UnderAge __          -> "UNDERAGE";
            case ErrorDefinitions.DbError __           -> "DB_ERROR";
            case ErrorDefinitions.EmailSendError __    -> "EMAIL_SEND_ERROR";
            case ErrorDefinitions.ActivationCodeError __ -> "ACTIVATION_CODE_ERROR";
            case ErrorDefinitions.MultipleErrors __    -> "MULTIPLE_ERRORS";
        };
    }

    public static String toReturnMessage(ErrorDefinitions error) {
        return switch (error) {
            case ErrorDefinitions.NameBlank e -> "El nombre no debe estar en blanco.";
            case ErrorDefinitions.EmailBlank e -> "El detail no debe estar en blanco.";
            case ErrorDefinitions.EmailInvalid e -> "El detail '%s' es inválido.".formatted(e.detail());
            case ErrorDefinitions.PasswordBlank e -> "La contraseña no debe estar vacía.";
            case ErrorDefinitions.PasswordTooShort e -> "La contraseña es demasiado corta.";
            case ErrorDefinitions.NameTooShort e -> "El nombre es demasiado corto.";
            case ErrorDefinitions.UnderAge e -> "Debe ser mayor de edad. Edad: " + e.age();
            case ErrorDefinitions.DbError e -> "Error de base de datos: " + e.detail();
            case ErrorDefinitions.EmailSendError e -> "Error al enviar detail: " + e.detail();
            case ErrorDefinitions.ActivationCodeError e -> "Error generando código: " + e.detail();
            case ErrorDefinitions.MultipleErrors e ->
                    "Se encontraron varios errores:\n" + e.errors().stream()
                            .map(ActualizarPerfilUsuarioApiErrorResponseBuilder::toReturnMessage)
                            .map(msg -> " - " + msg)
                            .collect(Collectors.joining("\n"));
        };
    }

    public sealed interface ErrorDefinitions permits
            ErrorDefinitions.NameBlank,
            ErrorDefinitions.EmailBlank,
            ErrorDefinitions.EmailInvalid,
            ErrorDefinitions.PasswordBlank,
            ErrorDefinitions.PasswordTooShort,
            ErrorDefinitions.NameTooShort,
            ErrorDefinitions.UnderAge,
            ErrorDefinitions.DbError,
            ErrorDefinitions.EmailSendError,
            ErrorDefinitions.ActivationCodeError,
            ErrorDefinitions.MultipleErrors
    {

        record NameBlank() implements ErrorDefinitions {}
        record EmailBlank() implements ErrorDefinitions {}
        record EmailInvalid(String detail) implements ErrorDefinitions {}
        record PasswordBlank() implements ErrorDefinitions {}
        record PasswordTooShort() implements ErrorDefinitions {}
        record NameTooShort() implements ErrorDefinitions {}
        record UnderAge(int age) implements ErrorDefinitions {}
        record DbError(String detail) implements ErrorDefinitions {}
        record EmailSendError(String detail) implements ErrorDefinitions {}
        record ActivationCodeError(String detail) implements ErrorDefinitions {}
        record MultipleErrors(List<ErrorDefinitions> errors) implements ErrorDefinitions {}

    }
}