package io.github.plaguv.messaging.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "amqp")
public record AmqpProperties(
    String centralExchange,
    String centralApplication
) {
    public AmqpProperties {
        if (centralExchange == null || centralExchange.isBlank()) {
            throw new IllegalArgumentException("Property 'amqp.central_exchange' cannot be null or blank");
        }
        if (centralApplication == null || centralApplication.isBlank()) {
            throw new IllegalArgumentException("Property 'amqp.application_name' cannot be null or blank");
        }
    }
}