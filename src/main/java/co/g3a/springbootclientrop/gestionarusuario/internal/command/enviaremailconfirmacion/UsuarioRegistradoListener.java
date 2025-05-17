package co.g3a.springbootclientrop.gestionarusuario.internal.command.enviaremailconfirmacion;

import co.g3a.functionalrop.core.Result;

import co.g3a.functionalrop.core.SyncResultPipeline;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.enviaremailconfirmacion.error.EnviarEmailConfirmacionError;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.registrarusuario.event.UsuarioRegistradoEvent;
import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.api.trace.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioRegistradoListener {
    private final EnviarEmailConfirmacionService enviarEmailConfirmacionService;
    private final Tracer tracer;

    @EventListener
    @Observed(name = "usuario.registrado.event", contextualName = "usuario-registrado-event")
    public void on(UsuarioRegistradoEvent event) {
           Result<Void, EnviarEmailConfirmacionError> result = SyncResultPipeline
                    .<String, EnviarEmailConfirmacionError>use(event.email())
                    .flatMap(enviarEmailConfirmacionService::generarCodigoActivacion)
                    .flatMap(codigoDeActivacionGeneradoEvent -> enviarEmailConfirmacionService.enviarEmailConfirmacion(event.email(), codigoDeActivacionGeneradoEvent.activationCode()))
                   .build();
    }
}
