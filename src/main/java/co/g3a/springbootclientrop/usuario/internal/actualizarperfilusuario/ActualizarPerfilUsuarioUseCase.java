package co.g3a.springbootclientrop.usuario.internal.actualizarperfilusuario;

import co.g3a.springbootclientrop.usuario.internal.actualizarperfilusuario.ActualizarPerfilUsuarioApiErrorResponseBuilder.ErrorDefinitions;
import co.g3a.functionalrop.core.Result;
import co.g3a.functionalrop.core.ResultPipeline;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
class ActualizarPerfilUsuarioUseCase {

   final ActualizarPerfilUsuarioService actualizarPerfilUsuarioService;

    ActualizarPerfilUsuarioUseCase(ActualizarPerfilUsuarioService actualizarPerfilUsuarioService) {
        this.actualizarPerfilUsuarioService = actualizarPerfilUsuarioService;
    }

    @Async
    @Observed(name = "run", contextualName = "run")
    public CompletableFuture<Result<String, ErrorDefinitions>> execute(ActualizarPerfilUsuarioCommand comando) {
        return ResultPipeline
                .<ActualizarPerfilUsuarioCommand, ErrorDefinitions>use(comando)
                .map(actualizarPerfilUsuarioService::normalizarEmail)
                .flatMapAsync(actualizarPerfilUsuarioService::actualizarPerfil)
                .flatMapAsync(actualizarPerfilUsuarioService::enviarNotificacion)
                .flatMapAsync(actualizarPerfilUsuarioService::generarCodigoActivacion)
                .build()
                .toCompletableFuture(); // Necesario ya que @Async espera CompletableFuture
    }
}