package co.g3a.springbootclientrop.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@Configuration
public class AuditorAwareConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        // Aquí puedes usar Spring Security para extraer el usuario autenticado
        return () -> Optional.of("sistema"); // valor por defecto
    }
}
