package co.g3a.springbootclientrop.gestionarusuario.internal.command.actualizarperfilusuario.mapper;

import co.g3a.springbootclientrop.gestionarusuario.internal.command.actualizarperfilusuario.error.ActualizarPerfilUsuarioError;
import co.g3a.springbootclientrop.shared.domain.vo.email.EmailError;

public class EmailErrorToActualizarPerfilUsuarioErrorMapper {

    public static ActualizarPerfilUsuarioError map(EmailError emailError) {
        return new ActualizarPerfilUsuarioError.EmailError(emailError);
    }

}