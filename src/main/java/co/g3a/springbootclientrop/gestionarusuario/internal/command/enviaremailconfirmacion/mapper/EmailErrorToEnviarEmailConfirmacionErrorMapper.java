package co.g3a.springbootclientrop.gestionarusuario.internal.command.enviaremailconfirmacion.mapper;

import co.g3a.springbootclientrop.gestionarusuario.internal.command.enviaremailconfirmacion.error.EnviarEmailConfirmacionError;
import co.g3a.springbootclientrop.shared.domain.vo.email.EmailError;

public class EmailErrorToEnviarEmailConfirmacionErrorMapper {

    public static EnviarEmailConfirmacionError map(EmailError emailError) {
        return new EnviarEmailConfirmacionError.EmailError(emailError);
    }

}