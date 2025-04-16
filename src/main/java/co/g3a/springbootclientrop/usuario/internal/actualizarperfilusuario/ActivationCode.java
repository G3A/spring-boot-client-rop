package co.g3a.springbootclientrop.usuario.internal.actualizarperfilusuario;

public record ActivationCode(String value) {
    public ActivationCode() {
        this("AC-" + System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return value;
    }
}