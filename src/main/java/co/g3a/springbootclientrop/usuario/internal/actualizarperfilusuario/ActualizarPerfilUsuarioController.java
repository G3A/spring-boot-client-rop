package co.g3a.springbootclientrop.usuario.internal.actualizarperfilusuario;

import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletionStage;


@RestController
@RequestMapping("/usuario")
@Slf4j
class ActualizarPerfilUsuarioController {
    private final ActualizarPerfilUsuarioUseCase actualizarPerfilUsuarioUseCase;
    private final ActualizarPerfilUsuarioApiErrorResponseBuilder errorBuilder;

    ActualizarPerfilUsuarioController(ActualizarPerfilUsuarioUseCase actualizarPerfilUsuarioUseCase,
                                      ActualizarPerfilUsuarioApiErrorResponseBuilder errorBuilder) {
        this.actualizarPerfilUsuarioUseCase = actualizarPerfilUsuarioUseCase;
        this.errorBuilder = errorBuilder;
    }

    public record ActualizarPerfilUsuarioRequest(
            @NotEmpty(message = "El correo no puede estar vacío.")
            @Email(message = "El correo debe ser válido.")
            String email,

            @Size(min = 3, message = "El nombre debe contener al menos 3 caracteres.")
            String name,

            @NotEmpty(message = "La contraseña no puede estar vacía.")
            @Size(min = 8, message = "La contraseña es demasiado corta. Al menos debe tener 8 caracteres.")
            String password,

            @Min(value = 18, message = "Debe ser mayor de edad.")
            int age
    ) {
    }


    @PutMapping
    @Observed(name = "actualizar.perfil.usuario.request", contextualName = "actualizar-perfil-usuario-request")
    CompletionStage<ResponseEntity<?>> actualizarPerfilUsuario(
            @RequestBody @Valid ActualizarPerfilUsuarioController.ActualizarPerfilUsuarioRequest request,
            HttpServletRequest httpRequest
    ) {

        log.info("Se inicia la actualización de los datos del usuario {}", request.name);

        var comando = ActualizarPerfilUsuarioCommand.Builder.create()
                .setEmail(request.email)
                .setName(request.name)
                .setPassword(request.password)
                .setAge(request.age)
                .build();


        return actualizarPerfilUsuarioUseCase.execute(comando)
                .thenApply(result -> result.fold(
                        error -> errorBuilder.from(error, httpRequest),
                        ResponseEntity::ok
                ));
    }



}