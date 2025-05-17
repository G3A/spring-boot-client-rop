package co.g3a.springbootclientrop.gestionarusuario.internal.command.actualizarperfilusuario.event;

public record PerfilUsuarioActualizadoEvent(Long id,
                                            String email,
                                            String name,
                                            String password,
                                            int age) { }