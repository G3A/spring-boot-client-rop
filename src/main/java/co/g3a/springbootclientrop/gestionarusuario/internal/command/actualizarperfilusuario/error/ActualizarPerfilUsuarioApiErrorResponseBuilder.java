package co.g3a.springbootclientrop.gestionarusuario.internal.command.actualizarperfilusuario.error;

import co.g3a.springbootclientrop.shared.errorhandling.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ActualizarPerfilUsuarioApiErrorResponseBuilder {

    private final GenericApiErrorResponder<ActualizarPerfilUsuarioError> responder;

    public ActualizarPerfilUsuarioApiErrorResponseBuilder(ApiErrorResponseBuilder apiErrorResponseBuilder) {
        this.responder = new GenericApiErrorResponder<>(apiErrorResponseBuilder);
    }

    public ResponseEntity<Object> from(ActualizarPerfilUsuarioError error, HttpServletRequest request) {
        return responder.buildResponse(
                error,
                request,
                this::mapToStatus,
                this::mapToCode,
                this::toMessage
        );
    }

    private int mapToStatus(ActualizarPerfilUsuarioError error) {
        return switch (error) {
            case ActualizarPerfilUsuarioError.EmailError cause -> cause.status();
            case ActualizarPerfilUsuarioError.ActivationCodeFailure cause -> cause.status();
            case ActualizarPerfilUsuarioError.MultipleError cause -> cause.status();
            case ActualizarPerfilUsuarioError.DbError cause -> cause.status();

        };
    }

    private String mapToCode(ActualizarPerfilUsuarioError error) {
        return switch (error) {
            case ActualizarPerfilUsuarioError.EmailError cause -> cause.code();
            case ActualizarPerfilUsuarioError.ActivationCodeFailure cause -> cause.code();
            case ActualizarPerfilUsuarioError.MultipleError cause -> cause.code();
            case ActualizarPerfilUsuarioError.DbError cause -> cause.code();
        };
    }

    private String toMessage(ActualizarPerfilUsuarioError error) {
        return switch (error) {
            case ActualizarPerfilUsuarioError.EmailError cause -> cause.detail();
            case ActualizarPerfilUsuarioError.ActivationCodeFailure cause -> cause.detail();
            case ActualizarPerfilUsuarioError.MultipleError cause ->cause.detail();
            case ActualizarPerfilUsuarioError.DbError cause -> cause.detail();
        };
    }
}