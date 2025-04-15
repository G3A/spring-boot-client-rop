


# Tracing with Spring Boot, OpenTelemetry, and Jaeger

Cada «hop/salto»  de un servicio al siguiente se denomina «span/tramo».
Todos los «spans» que intervienen en la respuesta a una solicitud al usuario final forman juntos un «trace/traza».

Cada span y trace tiene un identificador único. El primer span de una traza suele reutilizar el ID de traza como ID del span. Se espera que cada servicio pase el ID de rastreo al siguiente servicio que llame para que el siguiente servicio pueda utilizar el mismo ID de rastreo como ID de correlación en sus registros. Esta propagación del ID de rastreo se realiza normalmente a través de una cabecera HTTP.

