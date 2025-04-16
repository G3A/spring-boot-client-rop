package co.g3a.springbootclientrop.shared.errorhandling;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
public class ApiErrorResponseBuilder {
    public final Environment environment;
    private final Tracer tracer;

    public ApiErrorResponseBuilder(Environment environment
                                   ,Tracer tracer) {
        this.environment = environment;
        this.tracer = tracer;
    }

    public ApiErrorResponse build(
            String level,
            String message,
            int status,
            HttpServletRequest request,
            List<ApiErrorResponse.ErrorDetail> errors
    ) {
        var service = StringUtils.capitalize(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1));
        String traceId = Optional.ofNullable(tracer.currentSpan()) // Envuelve el span en Optional (maneja null)
                .map(Span::context)             // Si el span existe, obtiene el contexto (devuelve Optional<TraceContext>)
                .map(TraceContext::traceId)       // Si el contexto existe, obtiene el traceId (devuelve Optional<String>)
                .orElse(null);                  // Obtiene el valor del String o null si alguna etapa anterior resultó vacía

        String spanId = Optional.ofNullable(tracer.currentSpan())
                .map(Span::context)
                .map(TraceContext::spanId)
                .orElse(null);
        String userId=recuperarUserId(request); //remplazar luego con una implementación real.
        String sessionId=recuperarSessionId(request); //remplazar luego con una implementación real.
        return new ApiErrorResponse(
                Instant.now(),
                level,
                message,
                traceId,
                spanId,
                userId,
                sessionId,
                Arrays.stream(environment.getActiveProfiles()).findFirst().orElse(Constantes.DEFAULT_ENVIRONMENT),
                service.isEmpty() ? Constantes.SERVICE_NOT_DEFINED : service,
                status,
                request.getRequestURI(),
                errors
        );
    }

    private String recuperarUserId(HttpServletRequest request) {
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

    private String recuperarSessionId(HttpServletRequest request) {
        // Lo mismo, si tienes sesión u otras fuentes
        return "abc123";
    }
}