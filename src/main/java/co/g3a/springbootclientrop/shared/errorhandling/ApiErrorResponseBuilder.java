package co.g3a.springbootclientrop.shared.errorhandling;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Configuration
public class ApiErrorResponseBuilder {
    public final Environment environment;

    public ApiErrorResponseBuilder(Environment environment) {
        this.environment = environment;
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