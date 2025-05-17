package co.g3a.springbootclientrop.gestionarusuario.internal.command.enviaremailconfirmacion.error;

import co.g3a.springbootclientrop.shared.errorhandling.AppErrorDefinition;
import co.g3a.springbootclientrop.shared.errorhandling.MultipleErrorDefinition;

import java.util.List;
import java.util.stream.Collectors;

public sealed interface EnviarEmailConfirmacionError extends AppErrorDefinition
        permits EnviarEmailConfirmacionError.ActivationCodeFailure, EnviarEmailConfirmacionError.EmailError, EnviarEmailConfirmacionError.MultipleError, EnviarEmailConfirmacionError.SendEmailError {

    String detail();
    String code();
    int status();

    record ActivationCodeFailure(String activationCode) implements EnviarEmailConfirmacionError {
        @Override
        public String detail() {
            return activationCode.formatted("Error generando código: %s");
        }

        @Override
        public String code() {
            return "ACTIVATION_CODE_ERROR";
        }

        @Override
        public int status() {
            return 400;
        }

        @Override
        public void logErrorMessage() {

        }
    }

     record EmailError(co.g3a.springbootclientrop.shared.domain.vo.email.EmailError cause) implements EnviarEmailConfirmacionError {
        @Override
        public String detail() {
            return cause.detail();
        }

        @Override
        public String code() {
            return cause.code();
        }

        @Override
        public int status(){
            return cause.status();
        }

         @Override
         public void logErrorMessage() {

         }
    }

    record SendEmailError(String detalle) implements EnviarEmailConfirmacionError {
        @Override
        public String detail() {
            return detalle;
        }

        @Override
        public String code() {
            return "SEND_EMAIL_ERROR";
        }

        @Override
        public int status() {
            return 400;
        }

        @Override
        public void logErrorMessage() {

        }
    }

    record MultipleError(List<EnviarEmailConfirmacionError> errors)
            implements EnviarEmailConfirmacionError, MultipleErrorDefinition {

        @Override
        public String detail() {
            return "Se produjeron múltiples errores:\n" +
                    errors.stream().map(EnviarEmailConfirmacionError::detail).collect(Collectors.joining("\n"));
        }

        @Override
        public String code() {
            return "MULTIPLE_ERRORS";
        }

        @Override
        public int status() {
            return 400;
        }

        @Override
        public void logErrorMessage() {

        }
    }
}