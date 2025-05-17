package co.g3a.springbootclientrop.gestionarusuario.internal.command.actualizarperfilusuario.error;

import co.g3a.springbootclientrop.shared.errorhandling.AppErrorDefinition;
import co.g3a.springbootclientrop.shared.errorhandling.MultipleErrorDefinition;

import java.util.List;
import java.util.stream.Collectors;

public sealed interface ActualizarPerfilUsuarioError extends AppErrorDefinition
        permits ActualizarPerfilUsuarioError.ActivationCodeFailure, ActualizarPerfilUsuarioError.DbError, ActualizarPerfilUsuarioError.EmailError, ActualizarPerfilUsuarioError.MultipleError {

    String detail();
    String code();
    int status();

    record ActivationCodeFailure(String activationCode) implements ActualizarPerfilUsuarioError {
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

    record DbError(String detalle) implements ActualizarPerfilUsuarioError {
        @Override
        public String detail() {
            return detalle;
        }

        @Override
        public String code() {
            return "DB_ERROR";
        }

        @Override
        public int status() {
            return 500;
        }

        @Override
        public void logErrorMessage() {

        }
    }

    record EmailError(co.g3a.springbootclientrop.shared.domain.vo.email.EmailError cause) implements ActualizarPerfilUsuarioError {
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

    record MultipleError(List<ActualizarPerfilUsuarioError> errors)
            implements ActualizarPerfilUsuarioError, MultipleErrorDefinition {

        @Override
        public String detail() {
            return "Se produjeron múltiples errores:\n" +
                    errors.stream().map(ActualizarPerfilUsuarioError::detail).collect(Collectors.joining("\n"));
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

        @Override
        public List<ActualizarPerfilUsuarioError> errors() {
            return errors;
        }
    }
}