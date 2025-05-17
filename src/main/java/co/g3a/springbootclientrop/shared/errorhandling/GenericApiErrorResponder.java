package co.g3a.springbootclientrop.shared.errorhandling;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.function.Function;

public class GenericApiErrorResponder<T extends AppErrorDefinition> {

    private final ApiErrorResponseBuilder apiErrorResponseBuilder;

    public GenericApiErrorResponder(ApiErrorResponseBuilder apiErrorResponseBuilder) {
        this.apiErrorResponseBuilder = apiErrorResponseBuilder;
    }

    @SuppressWarnings("unchecked")
    public ResponseEntity<Object> buildResponse(
            T error,
            HttpServletRequest request,
            Function<T, Integer> mapToStatus,
            Function<T, String> mapToCode,
            Function<T, String> mapToMessage
    ) {
        int status = mapToStatus.apply(error);
        List<ApiErrorResponse.ErrorDetail> details;

        if (error instanceof MultipleErrorDefinition multiple) {
            details = multiple.errors().stream()
                    .map(e -> new ApiErrorResponse.ErrorDetail(
                            mapToCode.apply((T) e),
                            mapToMessage.apply((T) e)))
                    .toList();
        } else {
            details = List.of(new ApiErrorResponse.ErrorDetail(mapToCode.apply(error), mapToMessage.apply(error)));
        }

        ApiErrorResponse response = apiErrorResponseBuilder.build(
                Constantes.LEVEL_ERROR,
                mapToMessage.apply(error),
                status,
                request,
                details
        );

        return ResponseEntity.status(status).body(response);
    }

    // Clase reutilizable para errores múltiples
    public static final class MultipleErrors<T extends AppErrorDefinition> implements MultipleErrorDefinition {
        private final List<T> errors;

        public MultipleErrors(List<T> errors) {
            this.errors = errors;
        }


        @Override
        public String detail() {
            return "";
        }

        @Override
        public String code() {
            return "";
        }

        @Override
        public int status() {
            return 0;
        }

        @Override
        public void logErrorMessage() {

        }

        @Override
        public List<T> errors() {
            return errors;
        }


    }
}