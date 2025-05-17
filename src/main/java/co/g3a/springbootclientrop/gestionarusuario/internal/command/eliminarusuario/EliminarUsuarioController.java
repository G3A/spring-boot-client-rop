package co.g3a.springbootclientrop.gestionarusuario.internal.command.eliminarusuario;

import co.g3a.springbootclientrop.gestionarusuario.internal.command.eliminarusuario.error.EliminarUsuarioApiErrorResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletionStage;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/gestionar-usuario")
class EliminarUsuarioController {

    private final EliminarUsuarioUseCase eliminarUsuarioUseCase;
    private final EliminarUsuarioApiErrorResponseBuilder errorBuilder;

    record EliminarUsuarioRequest(
            @Min(value = 1, message = "El id debe ser válido.")
            Long id
    ) {}

    @DeleteMapping("/eliminar/{id}")
    CompletionStage<ResponseEntity<?>> eliminarUsuario(
            @PathVariable("id") Long id,
            HttpServletRequest httpRequest
    ) {
        log.info("Procesando la eliminación para el id: {}", id);

        var comando = EliminarUsuarioCommand.Builder.create()
                .setId(id)
                .build();

        return eliminarUsuarioUseCase.execute(comando)
                .thenApply(result -> result.fold(
                        // En caso de error
                        error -> errorBuilder.from(error, httpRequest),

                        // En caso de éxito
                        success -> ResponseEntity.noContent().build()
                ));
    }
}