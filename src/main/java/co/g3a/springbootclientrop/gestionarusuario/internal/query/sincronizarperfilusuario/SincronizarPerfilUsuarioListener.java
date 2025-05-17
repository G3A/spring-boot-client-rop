package co.g3a.springbootclientrop.gestionarusuario.internal.query.sincronizarperfilusuario;

import co.g3a.springbootclientrop.gestionarusuario.internal.command.actualizarperfilusuario.event.PerfilUsuarioActualizadoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SincronizarPerfilUsuarioListener {
    private final SincronizarPerfilUsuarioService service;

    @EventListener
    public void on(PerfilUsuarioActualizadoEvent event) {
        service.actualizarModeloDeLectura(event.email(),event.email(), event.password(), event.age());
    }
}
