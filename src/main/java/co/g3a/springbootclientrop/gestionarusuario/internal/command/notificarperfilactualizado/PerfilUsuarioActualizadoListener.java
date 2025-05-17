package co.g3a.springbootclientrop.gestionarusuario.internal.command.notificarperfilactualizado;

import co.g3a.springbootclientrop.gestionarusuario.internal.command.actualizarperfilusuario.event.PerfilUsuarioActualizadoEvent;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PerfilUsuarioActualizadoListener {

    private final NotificarPerfilActualizadoService service;

    //@ApplicationModuleListener
    @EventListener
    @Observed(name = "perfil.usuario.arctualizado.event", contextualName = "perfil-usuario-arctualizado-event")
    public void on(PerfilUsuarioActualizadoEvent event) {
        service.enviarNotificacionPerfilActualizado(event.email());
    }
}