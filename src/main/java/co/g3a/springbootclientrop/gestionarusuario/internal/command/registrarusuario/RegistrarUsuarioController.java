package co.g3a.springbootclientrop.gestionarusuario.internal.command.registrarusuario;

import co.g3a.springbootclientrop.gestionarusuario.internal.command.registrarusuario.error.RegistrarUsuarioApiErrorResponseBuilder;
import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.concurrent.CompletionStage;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/gestionar-usuario")
class RegistrarUsuarioController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final RegistrarUsuarioApiErrorResponseBuilder errorBuilder;

    record RegistrarUsuarioRequest(
            @NotEmpty(message = "El correo no puede estar vacío.")
            @Email(message = "El correo debe ser válido.")
            String email,

            @Size(min = 3, message = "El nombre debe contener al menos 3 caracteres.")
            String name,

            @Min(value = 18, message = "Debe ser mayor de edad.")
            int age,

            @NotEmpty(message = "La contraseña no puede estar vacía.")
            @Size(min = 8, message = "La contraseña es demasiado corta. Al menos debe tener 8 caracteres.")
            String password
    ) {}

    @PostMapping("/registro")
    @Observed(name = "registrar.usuario.request", contextualName = "registrar-usuario-request")
    CompletionStage<ResponseEntity<?>> registrarUsuario(
        @RequestBody @Valid RegistrarUsuarioRequest request,
        HttpServletRequest httpRequest
    ) {
        log.info("Procesando registro para {}", request.name);

        var comando = RegistrarUsuarioCommand.Builder.create()
            .setEmail(request.email)
            .setName(request.name)
            .setAge(request.age)
            .setPassword(request.password)
            .build();

        return registrarUsuarioUseCase.execute(comando)
            .thenApply(result -> result.fold(
                    // En caso de error
                    error -> errorBuilder.from(error, httpRequest),

                    // En caso de éxito
                    id -> ResponseEntity.created(URI.create("/api/gestionar-usuario/" + id)).body(id)
            ));
    }
}