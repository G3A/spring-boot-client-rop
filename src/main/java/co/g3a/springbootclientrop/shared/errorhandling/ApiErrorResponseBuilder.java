package co.g3a.springbootclientrop.shared.errorhandling;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
            List<ApiErrorResponse.ErrorDetail> errors,
            String userId,
            String sessionId
    ) {
        var service = StringUtils.capitalize(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1));
        return new ApiErrorResponse(
                Instant.now(),
                level,
                message,
                Objects.requireNonNull(tracer.currentTraceContext().context()).traceId(),
                userId,
                sessionId,
                Arrays.stream(environment.getActiveProfiles()).findFirst().orElse(Constantes.DEFAULT_ENVIRONMENT),
                service.isEmpty() ? Constantes.SERVICE_NOT_DEFINED : service,
                status,
                request.getRequestURI(),
                errors
        );
    }

    public ApiErrorResponse build(
            String level,
            String message,
            int status,
            HttpServletRequest request,
            List<ApiErrorResponse.ErrorDetail> errors
    ) {
        var service = StringUtils.capitalize(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1));
        return new ApiErrorResponse(
                Instant.now(),
                level,
                message,
                Objects.requireNonNull(tracer.currentTraceContext().context()).traceId(),
                "userId",
                "sessionId",
                Arrays.stream(environment.getActiveProfiles()).findFirst().orElse(Constantes.DEFAULT_ENVIRONMENT),
                service.isEmpty() ? Constantes.SERVICE_NOT_DEFINED : service,
                status,
                request.getRequestURI(),
                errors
        );
    }
}