package co.g3a.springbootclientrop.gestionarusuario.internal.command.enviaremailconfirmacion.error;

import co.g3a.springbootclientrop.shared.errorhandling.ApiErrorResponseBuilder;
import co.g3a.springbootclientrop.shared.errorhandling.GenericApiErrorResponder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class EnviarEmailConfirmacionApiErrorResponseBuilder {

    private final GenericApiErrorResponder<EnviarEmailConfirmacionError> responder;

    public EnviarEmailConfirmacionApiErrorResponseBuilder(ApiErrorResponseBuilder apiErrorResponseBuilder) {
        this.responder = new GenericApiErrorResponder<>(apiErrorResponseBuilder);
    }

    public ResponseEntity<Object> from(EnviarEmailConfirmacionError error, HttpServletRequest request) {
        return responder.buildResponse(
                error,
                request,
                this::mapToStatus,
                this::mapToCode,
                this::toMessage
        );
    }

    private int mapToStatus(EnviarEmailConfirmacionError error) {
        return switch (error) {
            case EnviarEmailConfirmacionError.EmailError cause -> cause.status();
            case EnviarEmailConfirmacionError.ActivationCodeFailure cause -> cause.status();
            case EnviarEmailConfirmacionError.MultipleError cause -> cause.status();
            case EnviarEmailConfirmacionError.SendEmailError cause-> cause.status();
        };
    }

    private String mapToCode(EnviarEmailConfirmacionError error) {
        return switch (error) {
            case EnviarEmailConfirmacionError.EmailError cause -> cause.code();
            case EnviarEmailConfirmacionError.ActivationCodeFailure cause -> cause.code();
            case EnviarEmailConfirmacionError.MultipleError cause -> cause.code();
            case EnviarEmailConfirmacionError.SendEmailError cause -> cause.code();
        };
    }

    private String toMessage(EnviarEmailConfirmacionError error) {
        return switch (error) {
            case EnviarEmailConfirmacionError.EmailError cause -> cause.detail();
            case EnviarEmailConfirmacionError.ActivationCodeFailure cause -> cause.detail();
            case EnviarEmailConfirmacionError.MultipleError cause ->cause.detail();
            case EnviarEmailConfirmacionError.SendEmailError cause ->cause.detail();
        };
    }
}