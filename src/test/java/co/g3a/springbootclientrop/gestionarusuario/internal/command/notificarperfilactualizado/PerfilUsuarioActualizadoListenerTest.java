package co.g3a.springbootclientrop.gestionarusuario.internal.command.notificarperfilactualizado;

import co.g3a.springbootclientrop.gestionarusuario.internal.command.actualizarperfilusuario.event.PerfilUsuarioActualizadoEvent;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class PerfilUsuarioActualizadoListenerTest {

    @Test
    void should_delegate_to_service() {
        NotificarPerfilActualizadoService service = mock(NotificarPerfilActualizadoService.class);
        PerfilUsuarioActualizadoListener listener = new PerfilUsuarioActualizadoListener(service);

        PerfilUsuarioActualizadoEvent event = new PerfilUsuarioActualizadoEvent(1l,
                "test@example.com",
                "Nombre1",
                "asdf",
                19);


        listener.on(event);

        verify(service).enviarNotificacionPerfilActualizado("test@example.com");
    }
}