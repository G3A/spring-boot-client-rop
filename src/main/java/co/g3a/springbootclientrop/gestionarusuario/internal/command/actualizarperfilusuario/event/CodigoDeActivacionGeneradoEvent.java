package co.g3a.springbootclientrop.gestionarusuario.internal.command.actualizarperfilusuario.event;

public record CodigoDeActivacionGeneradoEvent(String email, String activationCode) { }