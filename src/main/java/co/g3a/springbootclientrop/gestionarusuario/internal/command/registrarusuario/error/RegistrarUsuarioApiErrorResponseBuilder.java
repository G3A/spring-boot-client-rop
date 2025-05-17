package co.g3a.springbootclientrop.gestionarusuario.internal.command.registrarusuario.error;

import co.g3a.springbootclientrop.shared.errorhandling.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RegistrarUsuarioApiErrorResponseBuilder {

    private final GenericApiErrorResponder<RegistrarUsuarioErrors> responder;

    public RegistrarUsuarioApiErrorResponseBuilder(ApiErrorResponseBuilder apiErrorResponseBuilder) {
        this.responder = new GenericApiErrorResponder<>(apiErrorResponseBuilder);
    }

    public ResponseEntity<Object> from(RegistrarUsuarioErrors error, HttpServletRequest request) {
        return responder.buildResponse(
                error,
                request,
                this::mapToStatus,
                this::mapToCode,
                this::toMessage
        );
    }

    private int mapToStatus(RegistrarUsuarioErrors error) {
        return switch (error) {
            case RegistrarUsuarioErrors.DbError cause -> cause.status();
            case RegistrarUsuarioErrors.MultipleErrors cause -> cause.status();
            case RegistrarUsuarioErrors.ConflictoUsuarioEmailRepetidoError cause -> cause.status();
            case RegistrarUsuarioErrors.EmailError cause -> cause.status();
        };
    }

    private String mapToCode(RegistrarUsuarioErrors error) {
        return switch (error) {
            case RegistrarUsuarioErrors.DbError cause -> cause.code();
            case RegistrarUsuarioErrors.MultipleErrors cause -> cause.code();
            case RegistrarUsuarioErrors.ConflictoUsuarioEmailRepetidoError cause -> cause.code();
            case RegistrarUsuarioErrors.EmailError cause -> cause.code();
        };
    }

    private String toMessage(RegistrarUsuarioErrors error) {
        return switch (error) {
            case RegistrarUsuarioErrors.DbError cause -> cause.detail();
            case RegistrarUsuarioErrors.MultipleErrors cause -> cause.detail();
            case RegistrarUsuarioErrors.ConflictoUsuarioEmailRepetidoError cause -> cause.detail();
            case RegistrarUsuarioErrors.EmailError cause -> cause.detail();
        };
    }
}