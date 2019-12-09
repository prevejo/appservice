package br.ucb.prevejo.core;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

/**
 * Listener para eventos da aplicação.
 */
@Component
@SuppressWarnings("rawtypes")
public class AplicationListener implements ApplicationListener {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
		if ((event instanceof ContextStoppedEvent) || (event instanceof ContextClosedEvent)) {
			ContextProvider.getBeansOfType(AppShutdownListener.class).values().forEach(AppShutdownListener::onShutdown);
		}
    }
    
}
