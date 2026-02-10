package io.github.plaguv.messaging.startup;

import io.github.plaguv.messaging.config.properties.AmqpStartupProperties;
import io.github.plaguv.messaging.listener.EventListenerDiscoverer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AmqpStartup {

    private static final Logger log = LoggerFactory.getLogger(AmqpStartup.class);
    private final AmqpStartupProperties properties;
    private final EventListenerDiscoverer discoverer;

    public AmqpStartup(AmqpStartupProperties properties, EventListenerDiscoverer discoverer) {
        this.properties = properties;
        this.discoverer = discoverer;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerListeners() {
        if (!properties.registerListenersOnStartup()) {
            log.atDebug().log("Skipping startup step 'registerListeners'.");
            return;
        }
        log.atDebug().log("Starting startup step 'registerListeners'.");

        discoverer.getListeners().forEach((m, c) -> {
            System.out.println(m);
            System.out.println(c);
        });

        log.atDebug().log("Finished startup step 'registerListeners'.");
    }
}