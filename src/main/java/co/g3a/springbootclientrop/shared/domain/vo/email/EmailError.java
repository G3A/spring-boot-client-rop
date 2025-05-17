package co.g3a.springbootclientrop.shared.domain.vo.email;

public sealed interface EmailError
        permits EmailError.BlancoOVacio,
        EmailError.ContenidoInvalido,
        EmailError.DominioNoPermitido,
        EmailError.FormatoMalo {
    String detail();
    String code();
    int status();

    record BlancoOVacio() implements EmailError {

        @Override
        public String detail() {
            return "El e‑mail no puede estar vacío o en blanco";
        }

        @Override
        public String code() {
            return "EMAIL_BLANK_OR_EMPTY";
        }

        @Override
        public int status() {
            return 400;
        }
    }

    record ContenidoInvalido(String rawEmail) implements EmailError {

        @Override
        public String detail() {
            return String.format("El correo no puede contener la palabra fail: %s", rawEmail);
        }

        @Override
        public String code() {
            return "EMAIL_INVALID";
        }

        @Override
        public int status() {
            return 400;
        }
    }

    record DominioNoPermitido(String rawEmail) implements EmailError {

        @Override
        public String detail() {
            return String.format("Dominio no permitido: %s",rawEmail);
        }

        @Override
        public String code() {
            return "EMAIL_NOT_ALLOWED";
        }

        @Override
        public int status() {
            return 400;
        }
    }

    record FormatoMalo(String rawEmail) implements EmailError {

        @Override
        public String detail() {
            return String.format("El formato del e‑mail es inválido: %s",rawEmail);
        }

        @Override
        public String code() {
            return "EMAIL_BAD_FORMAT";
        }

        @Override
        public int status() {
            return 400;
        }
    }
}