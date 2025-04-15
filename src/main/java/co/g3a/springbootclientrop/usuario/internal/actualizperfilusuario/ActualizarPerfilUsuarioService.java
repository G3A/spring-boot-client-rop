package co.g3a.springbootclientrop.usuario.internal.actualizperfilusuario;

import co.g3a.functionalrop.core.DeadEnd;
import co.g3a.functionalrop.core.Result;
import co.g3a.functionalrop.utils.BiFunc;
import co.g3a.springbootclientrop.usuario.internal.actualizperfilusuario.ActualizarPerfilUsuarioApiErrorResponseBuilder.ErrorDefinitions;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.function.Function;

@Service
@Slf4j
class ActualizarPerfilUsuarioService {
    private final DeadEnd deadEnd;

    ActualizarPerfilUsuarioService(DeadEnd deadEnd) {
        this.deadEnd = deadEnd;
    }

    @Observed(name = "normalizar.email", contextualName = "normalizar-email")
    public ActualizarPerfilUsuarioCommand normalizarEmail(ActualizarPerfilUsuarioCommand comando) {
        return ActualizarPerfilUsuarioCommand.Builder.create()
                .setEmail(comando.email().trim().toLowerCase())
                .setName(comando.name())
                .setPassword(comando.password())
                .setAge(comando.age())
                .build();
    }

    @Observed(name = "actualizar.perfil", contextualName = "actualizar-perfil")
    public CompletionStage<Result<ActualizarPerfilUsuarioCommand, ErrorDefinitions>> actualizarPerfil(ActualizarPerfilUsuarioCommand cmd) {
        return executeAsyncWithEmail(
                cmd,
                this::actualizarPerfilWithEmail,
                throwable -> new ErrorDefinitions.DbError("❌ Error al actualizar la base de datos: " + throwable.getMessage())
        );
    }

    private Result<ActualizarPerfilUsuarioCommand, ErrorDefinitions> actualizarPerfilWithEmail(ActualizarPerfilUsuarioCommand cmd, Email email) {
        log.info("🚀 Iniciando proceso de actualización de usuario en base de datos. Email: {}", email);
        sleepSimulatedDbLatency();

        log.info("✔️ Actualización de usuario completada exitosamente. Email: {}", email);
        return Result.success(cmd);
    }

    @Observed(name = "enviar.notificacion", contextualName = "enviar-notificacion")
    public CompletionStage<Result<ActualizarPerfilUsuarioCommand, ErrorDefinitions>> enviarNotificacion(ActualizarPerfilUsuarioCommand cmd) {
        return executeAsyncWithEmail(
                cmd,
                this::enviarNotificacionWithEmail,
                throwable -> new ErrorDefinitions.EmailSendError("❌ Error al enviar el correo electrónico: " + throwable.getMessage())
        );
    }

    private Result<ActualizarPerfilUsuarioCommand, ErrorDefinitions> enviarNotificacionWithEmail(ActualizarPerfilUsuarioCommand cmd, Email email) {
        log.info("📧 Enviando correo de confirmación al destinatario: {}", email);
        sleepSimulatedEmailLatency();
        log.info("✔️ Correo de confirmación enviado exitosamente a: {}", email);
        return Result.success(cmd);
    }

    @Observed(name = "generar.codigo.activacion", contextualName = "generar-codigo-activacion")
    public CompletionStage<Result<String, ErrorDefinitions>> generarCodigoActivacion(ActualizarPerfilUsuarioCommand cmd) {
        return deadEnd.runSafeResultTransform(
                cmd,
                input -> Email.create(input.email())
                        .flatMap(this::generarCodigoActivacionFromEmail),
                throwable -> new ErrorDefinitions.ActivationCodeError("❌ Error al generar el código de activación: " + throwable.getMessage())
        );
    }


    private Result<String, ErrorDefinitions> generarCodigoActivacionFromEmail(Email email) {
        log.info("🔑 Generando código de activación para el detail: {}", email.value());
        ActivationCode code = new ActivationCode();
        /*
        if(true) {
            throw new RuntimeException("Alguna excepción en tiempo de ejecución");
        }
        */
        log.info("✔️ Código de activación generado exitosamente. Código: '{}', Email: '{}'", code.value(), email.value());
        return Result.success(code.value());
    }

    private CompletionStage<Result<ActualizarPerfilUsuarioCommand, ErrorDefinitions>> executeAsyncWithEmail(
            ActualizarPerfilUsuarioCommand cmd,
            BiFunc<ActualizarPerfilUsuarioCommand, Email, Result<ActualizarPerfilUsuarioCommand, ErrorDefinitions>> operation,
            Function<Throwable, ErrorDefinitions> onError
    ) {
        return deadEnd.runSafeResultTransform(
                cmd,
                command -> {
                    Result<Email, ErrorDefinitions> emailResult = Email.create(command.email());
                    Result<ActualizarPerfilUsuarioCommand, ErrorDefinitions> result;
                    if (emailResult.isSuccess()) {
                        result = operation.apply(command, emailResult.getValue());
                    } else {
                        result = Result.failure(emailResult.getError());
                    }
                    return result;
                },
                onError
        );
    }



    private void sleepSimulatedDbLatency() {
        try {
            TimeUnit.MILLISECONDS.sleep(1000); // Simulación de latencia de base de datos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("⚠️ Hilo interrumpido durante la simulación de latencia en base de datos");
        }
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