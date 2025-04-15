package co.g3a.springbootclientrop.usuario.internal.actualizaremailynombre;

import co.g3a.springbootclientrop.usuario.internal.actualizaremailynombre.ActualizarEmailYNombreApiErrorResponseBuilder.ErrorDefinitions;
import co.g3a.functionalrop.core.Result;
import co.g3a.functionalrop.core.ResultPipeline;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
class ActualizarEmailYNombreUseCase {

   final ActualizarEmailYNombreService actualizarEmailYNombreService;

    ActualizarEmailYNombreUseCase(ActualizarEmailYNombreService actualizarEmailYNombreService) {
        this.actualizarEmailYNombreService = actualizarEmailYNombreService;
    }

    @Async
    @Observed(name = "run", contextualName = "run")
    public CompletableFuture<Result<String, ErrorDefinitions>> run(ActualizarEmailYNombreCommand comando) {
        return ResultPipeline
                .<ActualizarEmailYNombreCommand, ErrorDefinitions>use(comando)
                .map(actualizarEmailYNombreService::canonicalizeEmail)
                .flatMapAsync(actualizarEmailYNombreService::updateDb)
                .flatMapAsync(actualizarEmailYNombreService::sendEmail)
                .flatMapAsync(actualizarEmailYNombreService::generateActivationCode)
                //.map(r -> "Success")
                .build()
                .toCompletableFuture(); // Necesario ya que @Async espera CompletableFuture
    }
}