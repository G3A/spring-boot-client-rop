package co.g3a.springbootclientrop.gestionarusuario.internal.command.registrarusuario;

public record RegistrarUsuarioCommand(String email, String name, Integer age,String password) {


    // Interfaz para el Builder que requiere el detail
    public interface CommandBuilderWithEmail {
        RegistrarUsuarioCommand.CommandBuilderWithName setEmail(String email);
    }

    // Interfaz para el Builder que requiere el nombre
    public interface CommandBuilderWithName {
        RegistrarUsuarioCommand.CommandBuilderWithAge setName(String name);
    }

    // Interfaz para el Builder que requiere el nombre
    public interface CommandBuilderWithAge {
        RegistrarUsuarioCommand.CommandBuilderWithPassword setAge(Integer age);
    }

    // Interfaz para el Builder que permite establecer la contraseña
    public interface CommandBuilderWithPassword {
        RegistrarUsuarioCommand.Builder setPassword(String password);
        RegistrarUsuarioCommand build();
    }



    // Clase Builder que implementa las interfaces
    public static class Builder implements
            RegistrarUsuarioCommand.CommandBuilderWithEmail,
            RegistrarUsuarioCommand.CommandBuilderWithName,
            RegistrarUsuarioCommand.CommandBuilderWithAge,
            RegistrarUsuarioCommand.CommandBuilderWithPassword{

        private String email;
        private String name;
        private Integer age;
        private String password;


        // Constructor privado
        private Builder() {}

        public static RegistrarUsuarioCommand.CommandBuilderWithEmail create() {
            return new RegistrarUsuarioCommand.Builder();
        }

        @Override
        public RegistrarUsuarioCommand.CommandBuilderWithName setEmail(String email) {
            this.email = email;
            return this;
        }

        @Override
        public RegistrarUsuarioCommand.CommandBuilderWithAge setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public RegistrarUsuarioCommand.CommandBuilderWithPassword setAge(Integer age) {
            this.age = age;
            return this;
        }

        @Override
        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        @Override
        public RegistrarUsuarioCommand build() {
            return new RegistrarUsuarioCommand(
                    email,
                    name,
                    age,
                    password
            );
        }
    }

    // Sobrecarga del método toString para una mejor representación
   /*
    @Override
    public String toString() {
        return "RegistrarUsuarioCommand{" +
                "detail= mail:'" + email + '\'' +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
    */
}