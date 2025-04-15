package co.g3a.springbootclientrop.usuario.internal.actualizaremailynombre;

import co.g3a.functionalrop.core.DeadEnd;
import co.g3a.functionalrop.core.Result;
import co.g3a.functionalrop.utils.BiFunc;
import co.g3a.springbootclientrop.usuario.internal.actualizaremailynombre.ActualizarEmailYNombreApiErrorResponseBuilder.ErrorDefinitions;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.function.Function;

@Service
@Slf4j
class ActualizarEmailYNombreService {
    private final DeadEnd deadEnd;

    ActualizarEmailYNombreService(DeadEnd deadEnd) {
        this.deadEnd = deadEnd;
    }

    @Observed(name = "canonicalize.detail", contextualName = "canonicalize-detail")
    public ActualizarEmailYNombreCommand canonicalizeEmail(ActualizarEmailYNombreCommand comando) {
        return ActualizarEmailYNombreCommand.Builder.create()
                .setEmail(comando.email().trim().toLowerCase())
                .setName(comando.name())
                .setPassword(comando.password())
                .setAge(comando.age())
                .build();
    }

    @Observed(name = "update.db", contextualName = "update-db")
    public CompletionStage<Result<ActualizarEmailYNombreCommand, ErrorDefinitions>> updateDb(ActualizarEmailYNombreCommand cmd) {
        return executeAsyncWithEmail(
                cmd,
                this::updateDbWithEmail,
                throwable -> new ErrorDefinitions.DbError("❌ Error al actualizar la base de datos: " + throwable.getMessage())
        );
    }

    private Result<ActualizarEmailYNombreCommand, ErrorDefinitions> updateDbWithEmail(ActualizarEmailYNombreCommand cmd, Email email) {
        log.info("🚀 Iniciando proceso de actualización de usuario en base de datos. Email: {}", email);
        sleepSimulatedDbLatency();

        log.info("✔️ Actualización de usuario completada exitosamente. Email: {}", email);
        return Result.success(cmd);
    }

    @Observed(name = "send.detail", contextualName = "send-detail")
    public CompletionStage<Result<ActualizarEmailYNombreCommand, ErrorDefinitions>> sendEmail(ActualizarEmailYNombreCommand cmd) {
        return executeAsyncWithEmail(
                cmd,
                this::sendEmailWithEmail,
                throwable -> new ErrorDefinitions.EmailSendError("❌ Error al enviar el correo electrónico: " + throwable.getMessage())
        );
    }

    private Result<ActualizarEmailYNombreCommand, ErrorDefinitions> sendEmailWithEmail(ActualizarEmailYNombreCommand cmd, Email email) {
        log.info("📧 Enviando correo de confirmación al destinatario: {}", email);
        sleepSimulatedEmailLatency();
        log.info("✔️ Correo de confirmación enviado exitosamente a: {}", email);
        return Result.success(cmd);
    }

    @Observed(name = "generate.activation.code", contextualName = "generate-activation-code")
    public CompletionStage<Result<String, ErrorDefinitions>> generateActivationCode(ActualizarEmailYNombreCommand cmd) {
        return deadEnd.runSafeResultTransform(
                cmd,
                input -> Email.create(input.email())
                        .flatMap(this::generateCodeFromEmail),
                throwable -> new ErrorDefinitions.ActivationCodeError("❌ Error al generar el código de activación: " + throwable.getMessage())
        );
    }

    @Observed(name = "generate.code.from.detail", contextualName = "generate-code-from-detail")
    public Result<String, ErrorDefinitions> generateCodeFromEmail(Email email) {
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

    private CompletionStage<Result<ActualizarEmailYNombreCommand, ErrorDefinitions>> executeAsyncWithEmail(
            ActualizarEmailYNombreCommand cmd,
            BiFunc<ActualizarEmailYNombreCommand, Email, Result<ActualizarEmailYNombreCommand, ErrorDefinitions>> operation,
            Function<Throwable, ErrorDefinitions> onError
    ) {
        return deadEnd.runSafeResultTransform(
                cmd,
                command -> {
                    Result<Email, ErrorDefinitions> emailResult = Email.create(command.email());
                    Result<ActualizarEmailYNombreCommand, ErrorDefinitions> result;
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