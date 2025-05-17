package co.g3a.springbootclientrop.gestionarusuario.internal.command.registrarusuario.event;

public record UsuarioRegistradoEvent(Long id,String name, String email, String password) {
}
