package co.g3a.springbootclientrop.gestionarusuario.internal.command;

import co.g3a.functionalrop.core.Result;

public class EntendiendoErroresComponiblesMain {

    // === Modelos de dominio
    public record Order(String orderId, String itemId, String sourceData) {}
    public record OrderEntity(String orderId, String itemId) {}

    // === Validación
    public sealed interface ValidationError permits ValidationError.RequiredFieldMissing {
        record RequiredFieldMissing(String field) implements ValidationError {}
    }

    // === Error de base datos
    public sealed interface DbError permits DbError.NotFound {
        record NotFound(String entity, String id) implements DbError {}
    }

    // === Error de API externa
    public sealed interface ExternalApiError permits ExternalApiError.NetworkError {
        record NetworkError(String message) implements ExternalApiError {}
    }

    // === Error general del dominio
    public sealed interface OrderError permits
            OrderError.Validation,
            OrderError.Database,
            OrderError.ExternalService,
            OrderError.InvalidOrderId,
            OrderError.StockUnavailable {

        record Validation(ValidationError cause) implements OrderError {}
        record Database(DbError cause) implements OrderError {}
        record ExternalService(ExternalApiError cause) implements OrderError {}

        record InvalidOrderId(String id) implements OrderError {}
        record StockUnavailable(String itemId) implements OrderError {}
    }

    // === Lógica de negocio

    Result<Void, ValidationError> validateInput(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            return Result.failure(new ValidationError.RequiredFieldMissing("orderId"));
        }
        return Result.success(null);
    }

    Result<OrderEntity, DbError> fetchOrderFromDb(String orderId) {
        if ("404".equals(orderId)) {
            return Result.failure(new DbError.NotFound("Order", orderId));
        }
        return Result.success(new OrderEntity(orderId, "item-123"));
    }

    Result<String, ExternalApiError> callExternalService(String orderId) {
        //return Result.success("external-response-for-" + orderId);
         return Result.failure(new ExternalApiError.NetworkError("Timeout"));
    }

    private Order toDomain(OrderEntity entity, String apiResponse) {
        return new Order(entity.orderId(), entity.itemId(), apiResponse);
    }

    public Result<Order, OrderError> getOrder(String orderId) {
        // Validación
        Result<Void, OrderError> step1 = validateInput(orderId)
                .mapFailure(OrderError.Validation::new);

        // Base de datos
        Result<OrderEntity, OrderError> step2 = step1.flatMap(ignored ->
                fetchOrderFromDb(orderId)
                        .mapFailure(OrderError.Database::new)
        );

        // API externa + toDomain

        return step2.flatMap(entity -> {
            Result<String, OrderError> apiResult = callExternalService(orderId)
                    .mapFailure(OrderError.ExternalService::new);

            return apiResult.map(apiResponse -> toDomain(entity, apiResponse));
        });
    }

    public static void main(String[] args) {
        EntendiendoErroresComponiblesMain service = new EntendiendoErroresComponiblesMain();
        Result<Order, OrderError> result = service.getOrder("100");

        result.fold(
                error -> {
                    switch (error) {
                        case OrderError.ExternalService external ->
                                System.out.println("logExternal(" + external.cause() + ")");
                        case OrderError.Database db ->
                                System.out.println("alertDatabaseIssue(" + db.cause() + ")");
                        case OrderError.Validation validation ->
                                System.out.println("showValidationError(" + validation.cause() + ")");
                        case OrderError.InvalidOrderId invalid ->
                                System.out.println("showMessage(\"ID inválido\")");
                        case OrderError.StockUnavailable stock ->
                                System.out.println("notifyStockUnavailable(" + stock.itemId() + ")");
                    }
                    return null;
                },
                order -> {
                    System.out.println("Orden procesada: " + order.orderId());
                    return null;
                }
        );
    }
}