package co.g3a.springbootclientrop.shared.errorhandling;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;




@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    public static final String OCURRIO_UN_ERROR_MESSAGE = "\uD83D\uDED1  Ocurrió un error \uD83D\uDC49\uD83C\uDFFB {}. {}";
    private final ApiErrorResponseBuilder apiErrorResponseBuilder;

    public GlobalExceptionHandler(ApiErrorResponseBuilder apiErrorResponseBuilder) {
        this.apiErrorResponseBuilder = apiErrorResponseBuilder;
    }

    //Check validations if you add validation rules on DTO class
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).toList();

        final String mensaje="La solicitud contiene datos no válidos.";
        final String codigoError="CAMPO_INVALIDO";

        ApiErrorResponse response = apiErrorResponseBuilder.build(
                Constantes.LEVEL_ERROR,
                mensaje, // o un mensaje más genérico
                HttpStatus.BAD_REQUEST.value(),
                request,
                List.of(new ApiErrorResponse.ErrorDetail(codigoError, errorMessages.toString())),
                recuperarUserId(), // desde contexto
                recuperarSessionId() // desde contexto/cookie
        );

        log.error(OCURRIO_UN_ERROR_MESSAGE, HttpStatus.BAD_REQUEST, response);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    //Check validations if you add validation rules on entity class
    @ExceptionHandler(value= ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request){
        List<String> errorMessages = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errorMessages.add(violation.getMessage());
        }

        final String mensaje="Error de validación de datos.";
        final String codigoError="RESTRICCION_VIOLADA";

        ApiErrorResponse response = apiErrorResponseBuilder.build(
                Constantes.LEVEL_ERROR,
                mensaje,
                HttpStatus.BAD_REQUEST.value(),
                request,
                List.of(new ApiErrorResponse.ErrorDetail(codigoError, errorMessages.toString())),
                recuperarUserId(), // desde contexto
                recuperarSessionId() // desde contexto/cookie
        );

        log.error(OCURRIO_UN_ERROR_MESSAGE, HttpStatus.BAD_REQUEST, response);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    //When given wrong request param
    @ExceptionHandler(value= MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request){
        final String mensaje="Tipo de argumento incorrecto.";
        final String codigoError="TIPO_ARGUMENTO_INCORRECTO";


        ApiErrorResponse response = apiErrorResponseBuilder.build(
                Constantes.LEVEL_ERROR,
                mensaje,
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                request,
                List.of(new ApiErrorResponse.ErrorDetail(codigoError, "Método no permitido")),
                recuperarUserId(), // desde contexto
                recuperarSessionId() // desde contexto/cookie
        );

        log.error(OCURRIO_UN_ERROR_MESSAGE, HttpStatus.METHOD_NOT_ALLOWED, "%s. %s".formatted(response, ex.getMessage()));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    //When given invalid request method
    @ExceptionHandler(value= HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request){
        final String mensaje="Método HTTP no soportado.";
        final String codigoError="METODO_NO_SOPORTADO";
        final String metodoSolicitado = ex.getMethod();
        final String metodosSoportados = String.join(", ", Objects.requireNonNull(ex.getSupportedMethods())); //Más seguro que getSupportedHttpMethods()
        final String details="El método HTTP '" + metodoSolicitado + "' no es soportado para este recurso. Métodos soportados: " + metodosSoportados + ".";

        ApiErrorResponse response = apiErrorResponseBuilder.build(
                Constantes.LEVEL_ERROR,
                mensaje,
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                request,
                List.of(new ApiErrorResponse.ErrorDetail(codigoError, details)),
                recuperarUserId(), // desde contexto
                recuperarSessionId() // desde contexto/cookie
        );

        log.error(OCURRIO_UN_ERROR_MESSAGE, HttpStatus.METHOD_NOT_ALLOWED, "%s. %s".formatted(response, ex.getMessage()));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        final String mensaje="Argumento inválido.";
        final String codigoError="ARGUMENTO_INVALIDO";
        final String details="Se proporcionó un argumento no válido.";

        ApiErrorResponse response = apiErrorResponseBuilder.build(
                Constantes.LEVEL_ERROR,
                mensaje,
                HttpStatus.BAD_REQUEST.value(),
                request,
                List.of(new ApiErrorResponse.ErrorDetail(codigoError, details)),
                recuperarUserId(), // desde contexto
                recuperarSessionId() // desde contexto/cookie
        );

        log.error(OCURRIO_UN_ERROR_MESSAGE, HttpStatus.BAD_REQUEST, "%s. %s".formatted(response, ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    //When given invalid values to request body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String mensaje;
        String codigoError;
        String details;
        final HttpStatus status = HttpStatus.BAD_REQUEST;


        if (ex.getCause() instanceof JsonParseException) {
            mensaje = "La solicitud JSON es inválida.";
            codigoError = "JSON_MALFORMADO";
            details = "El cuerpo de la solicitud contiene JSON con errores de sintaxis.";
        } else if (ex.getCause() instanceof JsonMappingException) {
            mensaje = "Tipos de datos JSON incorrectos.";
            codigoError = "JSON_TIPO_INCORRECTO";
            details = "Los tipos de datos en el JSON de la solicitud no coinciden con los esperados.";
        } else if (ex.getMessage().toLowerCase().contains("required request body is missing") ||
                ex.getMessage().toLowerCase().contains("request body is missing or empty")){ //se verifica el mensaje
            mensaje = "El cuerpo de la solicitud está vacío o ausente.";
            codigoError = "CUERPO_SOLICITUD_FALTANTE";
            details = "Se requiere un cuerpo de solicitud, pero no se proporcionó.";
        }
        // ... más else if para otras causas ...
        else {
            mensaje = "El cuerpo de la solicitud no se pudo leer o interpretar.";
            codigoError = "CUERPO_SOLICITUD_INVALIDO";
            details = "El formato del cuerpo de la solicitud es incorrecto o no se puede procesar.";
        }


        ApiErrorResponse response = apiErrorResponseBuilder.build(
                Constantes.LEVEL_ERROR,
                mensaje,
                HttpStatus.BAD_REQUEST.value(),
                request,
                List.of(new ApiErrorResponse.ErrorDetail(codigoError, details)),
                recuperarUserId(), // desde contexto
                recuperarSessionId() // desde contexto/cookie
        );

        log.error(OCURRIO_UN_ERROR_MESSAGE, HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        final String mensaje="Recurso no encontrado.";
        final String codigoError="RECURSO_NO_ENCONTRADO";
        final String details="El recurso solicitado no existe.";

        ApiErrorResponse response = apiErrorResponseBuilder.build(
                Constantes.LEVEL_ERROR,
                mensaje,
                HttpStatus.NOT_FOUND.value(),
                request,
                List.of(new ApiErrorResponse.ErrorDetail(codigoError, details)),
                recuperarUserId(), // desde contexto
                recuperarSessionId() // desde contexto/cookie
        );

        log.error(OCURRIO_UN_ERROR_MESSAGE, HttpStatus.NOT_FOUND, "%s. %s".formatted(response, ex.getMessage()));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    //Invalid media types
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedMediaTypeException(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        final String contentType = (ex.getContentType() != null)? ex.getContentType().toString(): "desconocido";
        final String tiposSoportados = ex.getSupportedMediaTypes().stream()
                .map(MediaType::toString)
                .collect(Collectors.joining(", "));
        final String mensaje="Tipo de medio no soportado.";
        final String codigoError="TIPO_MEDIO_NO_SOPORTADO";
        final String details="El tipo de medio '" + contentType + "' no es soportado. Tipos de medios soportados: " + tiposSoportados + ".";

        ApiErrorResponse response = apiErrorResponseBuilder.build(
                Constantes.LEVEL_ERROR,
                mensaje,
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                request,
                List.of(new ApiErrorResponse.ErrorDetail(codigoError, details)),
                recuperarUserId(), // desde contexto
                recuperarSessionId() // desde contexto/cookie
        );

        log.error(OCURRIO_UN_ERROR_MESSAGE, HttpStatus.UNSUPPORTED_MEDIA_TYPE, response);

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        final String mensaje="Ocurrió un error inesperado en el sistema.";
        final String codigoError="INTERNAL_SERVER_ERROR";

        ApiErrorResponse response = apiErrorResponseBuilder.build(
                Constantes.LEVEL_ERROR,
                mensaje, // o un mensaje más genérico
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request,
                List.of(new ApiErrorResponse.ErrorDetail(codigoError, "Error 500 (internal server error).")),
                recuperarUserId(), // desde contexto
                recuperarSessionId() // desde contexto/cookie
        );

        Throwable rootCause=getRootCause(ex);
        log.error("\uD83D\uDED1 Ocurrió un error inesperado. {}, causa raíz: {}", HttpStatus.INTERNAL_SERVER_ERROR, rootCause, rootCause);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private Throwable getRootCause(Exception e) {
        Throwable rootCause = e;
        while (e.getCause() != null && e.getCause() != rootCause) {
            rootCause = e.getCause();
        }
        return rootCause;
    }

    private String recuperarUserId() {
        // Aquí deberías usar SecurityContext, token, etc.
        //Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //        if (principal instanceof UserDetails) {
        //            String username = ((UserDetails) principal).getUsername();
        //            MDC.put("username", username);
        //        } else {
        //            String username = principal.toString();
        //            MDC.put("username", username);
        //        }

        return "USR-32212";
    }

    private String recuperarSessionId() {
        // Lo mismo, si tienes sesión u otras fuentes
        return "abc123";
    }

}