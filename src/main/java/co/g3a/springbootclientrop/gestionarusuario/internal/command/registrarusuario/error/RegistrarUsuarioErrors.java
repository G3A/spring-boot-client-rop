package co.g3a.springbootclientrop.gestionarusuario.internal.command.registrarusuario.error;

import co.g3a.springbootclientrop.shared.errorhandling.AppErrorDefinition;
import co.g3a.springbootclientrop.shared.errorhandling.MultipleErrorDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;


public sealed interface RegistrarUsuarioErrors extends AppErrorDefinition
        permits RegistrarUsuarioErrors.ConflictoUsuarioEmailRepetidoError, RegistrarUsuarioErrors.DbError, RegistrarUsuarioErrors.EmailError, RegistrarUsuarioErrors.MultipleErrors {

    @Slf4j
    record DbError(String detail, String errorMessage) implements RegistrarUsuarioErrors {
        @Override
        public String detail() {
            return detail;
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
            log.error(errorMessage);
        }
    }

    @Slf4j
    record ConflictoUsuarioEmailRepetidoError(String email, String errorMessage) implements RegistrarUsuarioErrors {
        @Override
        public String detail() {
            return String.format("❌ El usuario ya existe registrado en la base de datos con el mismo email: %s",email);
        }

        @Override
        public String code() {
            return "CONFLICTO_USUARIO_EMAIL_REPETIDO_ERROR";
        }

        @Override
        public int status() {
            return 409;
        }

        @Override
        public void logErrorMessage() {
            log.error(this.detail(),errorMessage);
        }
    }

    record EmailError(co.g3a.springbootclientrop.shared.domain.vo.email.EmailError cause) implements RegistrarUsuarioErrors {
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

    record MultipleErrors(List<RegistrarUsuarioErrors> errors)
            implements RegistrarUsuarioErrors, MultipleErrorDefinition {

        @Override
        public String detail() {
            return  "Se produjeron múltiples errores:\n" +
                    errors.stream().map(RegistrarUsuarioErrors::detail).collect(Collectors.joining("\n"));
        }

        @Override
        public String code() {
            return "MULTIPLE_ERRORS";
        }

        @Override
        public int status() {
            return 500;
        }

        @Override
        public void logErrorMessage() {

        }

        @Override
        public List<RegistrarUsuarioErrors> errors() {
            return errors;
        }

    }
}