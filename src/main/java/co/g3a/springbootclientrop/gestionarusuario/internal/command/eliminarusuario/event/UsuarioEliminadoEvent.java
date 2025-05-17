package co.g3a.springbootclientrop.gestionarusuario.internal.command.eliminarusuario.event;

public record UsuarioEliminadoEvent(Long id, String name, String email) {
}