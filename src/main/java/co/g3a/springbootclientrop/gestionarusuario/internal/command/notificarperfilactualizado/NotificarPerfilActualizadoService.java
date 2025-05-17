package co.g3a.springbootclientrop.gestionarusuario.internal.command.notificarperfilactualizado;

import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class NotificarPerfilActualizadoService {

    @Observed(name = "enviar.notificacion.perfil.actualizado", contextualName = "enviar-notificacion-perfil-actualizado")
    public void enviarNotificacionPerfilActualizado(String email) {
        log.info("📧 Enviando correo de notificación sobre la actualización del perfil  usuario al destinatario: {}", email);
        // Aquí podrías integrar lógica real con un proveedor de email

        sleepSimulatedEmailLatency();
        log.info("✔️ Correo de notificación de la actualización del perfil  usuario  enviado exitosamente a: {}", email);
    }

    private void sleepSimulatedEmailLatency() {
        try {
            TimeUnit.MILLISECONDS.sleep(1000); // Simulación de latencia en envío de correo
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("⚠️ Hilo interrumpido durante la simulación de latencia en envío de correo");
        }
    }
}