package co.g3a.springbootclientrop.gestionarusuario.internal.command.actualizarperfilusuario;

/**
 * Ejemplo de uso con campos opcionales y requeridos
 * <p>
 * <pre>{@code
 * ActualizarNombreEmailUsuarioCommand comando2 =
 *         ActualizarNombreEmailUsuarioCommand.Builder.create()
 *                 .setEmail("dora@example.com") // Campo requerido
 *                 .setName("Dora") // Campo requerido
 *                 .setPassword("otraContra") // Campo requerido
 *                 .setAge(28) // Campo requerido
 *                 .setAlias("La exploradora") // Campo opcional
 *                 .setCiudad("Soledad") // Campo opcional
 *                 .build(); // Te construye el registro inmutable.
 * }</pre>
 *
 * @param email correo electrónico del usuario
 * @param name nombre del usuario
 * @param password contraseña del usuario
 * @param age edad del usuario
 * @param alias alias opcional del usuario
 * @param ciudad ciudad opcional del usuario
 */
public record ActualizarPerfilUsuarioCommand(
        Long id,
        String email,
        String name,
        String password,
        int age,
        String alias,
        String ciudad
) {

    // Valor por defecto para campos opcionales si no se proporcionan
    public static final String ALIAS_POR_DEFECTO = null;
    public static final String CIUDAD_POR_DEFECTO = null;

    // Interfaz para el Builder que requiere el detail
    public interface CommandBuilderWithId {
        CommandBuilderWithEmail setId(Long id);
    }

    // Interfaz para el Builder que requiere el detail
    public interface CommandBuilderWithEmail {
        CommandBuilderWithName setEmail(String email);
    }

    // Interfaz para el Builder que requiere el nombre
    public interface CommandBuilderWithName {
        CommandBuilderWithPassword setName(String name);
    }

    // Interfaz para el Builder que permite establecer la contraseña
    public interface CommandBuilderWithPassword {
        CommandBuilderWithAge setPassword(String password);
    }

    // Interfaz para el Builder que permite establecer la edad
    public interface CommandBuilderWithAge {
        CommandBuilderWithCamposOpcionales setAge(int age);
    }

    // Interfaz para el Builder que permite establecer campos opcionales (alias, ciudad)
    public interface CommandBuilderWithCamposOpcionales {
        Builder setAlias(String alias);
        Builder setCiudad(String ciudad);
        ActualizarPerfilUsuarioCommand build();
    }

    // Clase Builder que implementa las interfaces
    public static class Builder implements
            CommandBuilderWithId,
            CommandBuilderWithEmail,
            CommandBuilderWithName,
            CommandBuilderWithPassword,
            CommandBuilderWithAge,
            CommandBuilderWithCamposOpcionales {
        private Long id;
        private String email;
        private String name;
        private String password;
        private int age;
        private String alias = ALIAS_POR_DEFECTO;
        private String ciudad = CIUDAD_POR_DEFECTO;


        // Constructor privado
        private Builder() {}

        public static CommandBuilderWithId create() {
            return new Builder();
        }

        @Override
        public CommandBuilderWithEmail setId(Long id) {
            this.id = id;
            return this;
        }

        @Override
        public CommandBuilderWithName setEmail(String email) {
            this.email = email;
            return this;
        }

        @Override
        public CommandBuilderWithPassword setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public CommandBuilderWithAge setPassword(String password) {
            this.password = password;
            return this;
        }

        @Override
        public CommandBuilderWithCamposOpcionales setAge(int age) {
            this.age = age;
            return this;
        }

        @Override
        public Builder setAlias(String alias) {
            this.alias = alias;
            return this;
        }

        @Override
        public Builder setCiudad(String ciudad) {
            this.ciudad = ciudad;
            return this;
        }

        @Override
        public ActualizarPerfilUsuarioCommand build() {
            return new ActualizarPerfilUsuarioCommand(
                    id,
                    email,
                    name,
                    password,
                    age,
                    alias,
                    ciudad
            );
        }

    }

    // Sobrecarga del método toString para una mejor representación
    @Override
    public String toString() {
        return "ActualizarPerfilUsuarioCommand{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", alias='" + alias + '\'' +
                ", ciudad='" + ciudad + '\'' +
                '}';
    }
}