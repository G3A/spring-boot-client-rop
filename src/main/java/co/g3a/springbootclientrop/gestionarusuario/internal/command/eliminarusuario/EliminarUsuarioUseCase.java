package co.g3a.springbootclientrop.gestionarusuario.internal.command.eliminarusuario;

import co.g3a.functionalrop.core.Result;
import co.g3a.functionalrop.core.SyncResultPipeline;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.eliminarusuario.error.EliminarUsuarioErrors;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.eliminarusuario.event.UsuarioEliminadoEvent;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class EliminarUsuarioUseCase {

    private final ApplicationEventPublisher eventPublisher;
    private final EliminarUsuarioService eliminarUsuarioService;

    @Async
    @Observed(name = "eliminar.usuario.usecase", contextualName = "eliminar-usuario-usecase")
    public CompletableFuture<Result<Void, EliminarUsuarioErrors>> execute(EliminarUsuarioCommand comando){
        return CompletableFuture.completedFuture(
                SyncResultPipeline
                        .<EliminarUsuarioCommand, EliminarUsuarioErrors>use(comando)
                        .flatMap(eliminarUsuarioService::eliminarUsuario)
                        .peek(eventPublisher::publishEvent)
                        .map(x -> (Void) null)
                        .build()
        );
    }
}