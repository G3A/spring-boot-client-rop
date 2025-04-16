package co.g3a.springbootclientrop.shared.config;

import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OtlGrpcSpanExporterConfig {

        @Bean
        public OtlpGrpcSpanExporter otlpHttpSpanExporter(@Value("${tracing.url}") String url) {
                return OtlpGrpcSpanExporter.builder().setEndpoint(url).build();
        }
}