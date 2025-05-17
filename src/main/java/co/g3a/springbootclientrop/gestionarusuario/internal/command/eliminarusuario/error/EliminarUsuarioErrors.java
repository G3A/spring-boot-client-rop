package co.g3a.springbootclientrop.gestionarusuario.internal.command.eliminarusuario.error;

import co.g3a.springbootclientrop.shared.errorhandling.AppErrorDefinition;
import lombok.extern.slf4j.Slf4j;

public sealed interface EliminarUsuarioErrors extends AppErrorDefinition
        permits EliminarUsuarioErrors.DbError, EliminarUsuarioErrors.UsuarioNoEncontrado {

    @Slf4j
    record DbError(String detail, String errorMessage) implements EliminarUsuarioErrors {
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
    record UsuarioNoEncontrado(String errorMessage) implements EliminarUsuarioErrors {
        @Override
        public String detail() {
            return errorMessage;
        }

        @Override
        public String code() {
            return "USUARIO_NO_ENCONTRADO";
        }

        @Override
        public int status() {
            return 404;
        }

        @Override
        public void logErrorMessage() {
            log.error(errorMessage);
        }
    }
}