package co.g3a.springbootclientrop.gestionarusuario.internal.command.registrarusuario.mapper;

import co.g3a.springbootclientrop.gestionarusuario.internal.command.registrarusuario.error.RegistrarUsuarioErrors;
import co.g3a.springbootclientrop.shared.domain.vo.email.EmailError;

public class EmailErrorToRegistrarUsuarioErrorsMapper {

    public static RegistrarUsuarioErrors map(EmailError emailError) {
        return new RegistrarUsuarioErrors.EmailError(emailError);
    }

}