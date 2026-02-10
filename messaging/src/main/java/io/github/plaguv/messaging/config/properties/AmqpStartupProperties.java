package io.github.plaguv.messaging.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "amqp")
public record AmqpStartupProperties(
        Boolean registerListenersOnStartup
) {
    public AmqpStartupProperties {
        registerListenersOnStartup = registerListenersOnStartup != null
                ? registerListenersOnStartup
                : true;
    }
}