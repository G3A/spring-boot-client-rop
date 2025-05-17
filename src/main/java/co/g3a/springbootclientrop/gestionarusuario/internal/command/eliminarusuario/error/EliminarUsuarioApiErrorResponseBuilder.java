package co.g3a.springbootclientrop.gestionarusuario.internal.command.eliminarusuario.error;

import co.g3a.springbootclientrop.shared.errorhandling.ApiErrorResponseBuilder;
import co.g3a.springbootclientrop.shared.errorhandling.GenericApiErrorResponder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class EliminarUsuarioApiErrorResponseBuilder {

    private final GenericApiErrorResponder<EliminarUsuarioErrors> responder;

    public EliminarUsuarioApiErrorResponseBuilder(ApiErrorResponseBuilder apiErrorResponseBuilder) {
        this.responder = new GenericApiErrorResponder<>(apiErrorResponseBuilder);
    }

    public ResponseEntity<Object> from(EliminarUsuarioErrors error, HttpServletRequest request) {
        return responder.buildResponse(
                error,
                request,
                this::mapToStatus,
                this::mapToCode,
                this::toMessage
        );
    }

    private int mapToStatus(EliminarUsuarioErrors error) {
        return switch (error) {
            case EliminarUsuarioErrors.DbError cause -> cause.status();
            case EliminarUsuarioErrors.UsuarioNoEncontrado cause -> cause.status();
        };
    }

    private String mapToCode(EliminarUsuarioErrors error) {
        return switch (error) {
            case EliminarUsuarioErrors.DbError cause -> cause.code();
            case EliminarUsuarioErrors.UsuarioNoEncontrado cause -> cause.code();
        };
    }

    private String toMessage(EliminarUsuarioErrors error) {
        return switch (error) {
            case EliminarUsuarioErrors.DbError cause -> cause.detail();
            case EliminarUsuarioErrors.UsuarioNoEncontrado cause -> cause.detail();
        };
    }
}