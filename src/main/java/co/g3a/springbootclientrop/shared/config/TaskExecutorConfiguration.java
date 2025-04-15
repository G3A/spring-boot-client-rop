package co.g3a.springbootclientrop.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;

@Configuration(proxyBeanMethods = false)
public class TaskExecutorConfiguration {

        // https://docs.spring.io/spring-framework/reference/integration/observability.html#observability.application-events
        @Bean(name = "propagatingContextExecutor")
        public TaskExecutor propagatingContextExecutor() {
                SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
                // decorate task execution with a decorator that supports context propagation
                taskExecutor.setTaskDecorator(new ContextPropagatingTaskDecorator());
                return taskExecutor;
        }

}
