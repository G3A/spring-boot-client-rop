package co.g3a.springbootclientrop.gestionarusuario.internal.command.registrarusuario;


import co.g3a.functionalrop.core.Result;
import co.g3a.functionalrop.core.SyncResultPipeline;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.registrarusuario.error.RegistrarUsuarioErrors;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.registrarusuario.event.UsuarioRegistradoEvent;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {

    private final ApplicationEventPublisher eventPublisher;
    private final RegistrarUsuarioService registrarUsuarioService;


    @Async
    @Observed(name = "registrar.usuario.usecase", contextualName = "registrar-usuario-usecase")
    public CompletableFuture<Result<Long, RegistrarUsuarioErrors>> execute(RegistrarUsuarioCommand comando){
        return CompletableFuture.completedFuture(
                    SyncResultPipeline
                        .<RegistrarUsuarioCommand, RegistrarUsuarioErrors>use(comando)
                        .flatMap(registrarUsuarioService::registrarUsuario)
                        .peek(eventPublisher::publishEvent)
                        .map(UsuarioRegistradoEvent::id)
                        .build()
                );
    }
}