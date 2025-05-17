package co.g3a.springbootclientrop.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;

@Configuration
public class ApplicationEventsConfiguration {

	// https://docs.spring.io/spring-framework/reference/integration/observability.html#observability.application-events
	@Bean(name = "applicationEventMulticaster")
	public SimpleApplicationEventMulticaster simpleApplicationEventMulticaster() {
		SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		// decorate task execution with a decorator that supports context propagation
		taskExecutor.setTaskDecorator(new ContextPropagatingTaskDecorator());
		eventMulticaster.setTaskExecutor(taskExecutor);
		return eventMulticaster;
	}

}