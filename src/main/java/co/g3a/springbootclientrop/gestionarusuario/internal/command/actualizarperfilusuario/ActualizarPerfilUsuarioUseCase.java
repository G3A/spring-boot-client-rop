package co.g3a.springbootclientrop.gestionarusuario.internal.command.actualizarperfilusuario;

import co.g3a.functionalrop.core.Result;
import co.g3a.functionalrop.core.SyncResultPipeline;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.actualizarperfilusuario.error.ActualizarPerfilUsuarioError;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

/**
 * Caso de uso encargado de orquestar la actualización del perfil de un usuario.
 *
 * Este caso de uso sigue el patrón de diseño "Read outside, write inside", lo que significa:
 * - Las operaciones de lectura se ejecutan fuera del contexto transaccional para evitar bloqueos innecesarios,
 *   mejorar la performance y reducir el riesgo de "idle in transaction".
 * - Las operaciones de escritura se ejecutan dentro de una transacción, lo que permite garantizar atomicidad
 *   y rollback en caso de error.
 *
 * Características:
 * - Asíncrono: se ejecuta en un hilo separado usando @Async
 * - Observado: se traza el comportamiento del caso de uso con Micrometer usando @Observed
 * - Inmutable: usa SyncResultPipeline para componer el flujo de forma funcional y declarativa
 *
 * Flujo:
 * 1. Normaliza los datos del comando
 * 2. Realiza la lectura del perfil del usuario (sin transacción)
 * 3. Actualiza el perfil del usuario (con transacción)
 * 4. Publica un evento de dominio
 */
@Component
@RequiredArgsConstructor
class ActualizarPerfilUsuarioUseCase {

    final ActualizarPerfilUsuarioService actualizarPerfilUsuarioService;
    final ApplicationEventPublisher eventPublisher;

    @Async
    @Observed(name = "actualizar.perfil.usuario.usecase", contextualName = "actualizar-perfil-usuario-usecase")
    public CompletableFuture<Result<Void, ActualizarPerfilUsuarioError>> execute(ActualizarPerfilUsuarioCommand comando) {
        return CompletableFuture.completedFuture(
                SyncResultPipeline
                        .<ActualizarPerfilUsuarioCommand, ActualizarPerfilUsuarioError>use(comando)
                        .map(actualizarPerfilUsuarioService::normalizarEmail)
                        .flatMap(actualizarPerfilUsuarioService::buscarPerfil) // 🔍 Lectura fuera de transacción
                        .flatMap(usuario -> actualizarPerfilUsuarioService.actualizarPerfil(comando, usuario)) // 💾 Escritura dentro de transacción
                        .peek(eventPublisher::publishEvent)
                        .map(x -> (Void) null)
                        .build()
        );
    }
}