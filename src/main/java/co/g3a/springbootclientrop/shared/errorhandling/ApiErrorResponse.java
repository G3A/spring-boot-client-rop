package co.g3a.springbootclientrop.shared.errorhandling;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
    Instant timestamp,
    String level,
    String message,
    String traceId,
    String spanId,
    String userId,
    String sessionId,
    String environment,
    String service,
    int status,
    String path,
    List<ErrorDetail> errors
) {
    public record ErrorDetail(String code, String details) {}
}