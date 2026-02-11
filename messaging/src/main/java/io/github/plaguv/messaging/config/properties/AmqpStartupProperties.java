package io.github.plaguv.messaging.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "amqp.skip")
public record AmqpStartupProperties(
        Boolean registerListeners
) {
    public AmqpStartupProperties {
        registerListeners = registerListeners != null
                ? registerListeners
                : false;
    }
}