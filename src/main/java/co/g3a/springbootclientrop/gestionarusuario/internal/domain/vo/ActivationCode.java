package co.g3a.springbootclientrop.gestionarusuario.internal.domain.vo;

public record ActivationCode(String value) {
    public ActivationCode() {
        this("AC-" + System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return value;
    }
}