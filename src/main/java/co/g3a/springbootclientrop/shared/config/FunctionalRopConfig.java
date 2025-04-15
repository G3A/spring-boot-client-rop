package co.g3a.springbootclientrop.shared.config;

import co.g3a.functionalrop.core.DeadEnd;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;

@Configuration
public class FunctionalRopConfig {

    @Bean
    public DeadEnd deadEnd(Executor executor) {
        return new DeadEnd(executor);
    }

}
