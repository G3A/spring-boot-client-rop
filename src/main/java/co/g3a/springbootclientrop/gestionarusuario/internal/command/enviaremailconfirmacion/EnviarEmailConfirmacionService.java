package co.g3a.springbootclientrop.gestionarusuario.internal.command.enviaremailconfirmacion;

import co.g3a.functionalrop.core.DeadEnd;
import co.g3a.functionalrop.core.Result;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.enviaremailconfirmacion.error.EnviarEmailConfirmacionError;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.enviaremailconfirmacion.mapper.EmailErrorToEnviarEmailConfirmacionErrorMapper;
import co.g3a.springbootclientrop.gestionarusuario.internal.domain.vo.ActivationCode;
import co.g3a.springbootclientrop.shared.domain.vo.email.Email;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.actualizarperfilusuario.event.CodigoDeActivacionGeneradoEvent;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

@Slf4j
@RequiredArgsConstructor
@Service
public class EnviarEmailConfirmacionService {
    private final DeadEnd deadEnd;


    @Observed(name = "generar.codigo.activacion", contextualName = "generar-codigo-activacion")
    public Result<CodigoDeActivacionGeneradoEvent, EnviarEmailConfirmacionError> generarCodigoActivacion(String email) {
       Result<CodigoDeActivacionGeneradoEvent, EnviarEmailConfirmacionError> result;
        try {
            result = Email.create(email)
                    .mapFailure(EmailErrorToEnviarEmailConfirmacionErrorMapper::map)
                    .flatMap(this::generarCodigoActivacionFromEmail);
        } catch (Exception e) {
            result=Result.failure(new EnviarEmailConfirmacionError.ActivationCodeFailure("❌ Error al generar el código de activación: " + e.getMessage()));
        }
        return result;
    }

    private Result<CodigoDeActivacionGeneradoEvent, EnviarEmailConfirmacionError> generarCodigoActivacionFromEmail(Email email) {
        log.info("🔑 Generando código de activación para el detail: {}", email.value());
        ActivationCode codigo = new ActivationCode();
/*
        if(true) {
            throw new RuntimeException("Alguna excepción en tiempo de ejecución");
        }

 */

        log.info("✔️ Código de activación generado exitosamente. Código: '{}', Email: '{}'", codigo.value(), email.value());
        return Result.success(new CodigoDeActivacionGeneradoEvent(email.value(), codigo.value()));
    }

    @Observed(name = "enviar.email.confirmacion", contextualName = "enviar-email-confirmacion")
    public Result<Void, EnviarEmailConfirmacionError> enviarEmailConfirmacion(String email, String activationCode) {
        log.info("📧 Enviando código de activación '{}' a email '{}'", activationCode, email);
        // Aquí podrías integrar lógica real con un proveedor de email
        Result<Void, EnviarEmailConfirmacionError> result;

        try{
            result = Result.success((Void) null);
        } catch (Exception e) {
            result= Result.failure(new EnviarEmailConfirmacionError.SendEmailError("❌ Error al enviar el correo: " + e.getMessage()));
        }
        return result;
    }


}
