package co.g3a.springbootclientrop.gestionarusuario.internal.command.eliminarusuario;

public record EliminarUsuarioCommand(Long id) {  // Or String email, depending on your deletion criteria

    public interface CommandBuilderWithId {
        EliminarUsuarioCommand.Builder setId(Long id);
    }

    public static class Builder implements EliminarUsuarioCommand.CommandBuilderWithId{
        private Long id;

        private Builder(){}

        public static EliminarUsuarioCommand.CommandBuilderWithId create(){
            return new EliminarUsuarioCommand.Builder();
        }
        @Override
        public EliminarUsuarioCommand.Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public EliminarUsuarioCommand build(){
            return new EliminarUsuarioCommand(id);
        }
    }

}