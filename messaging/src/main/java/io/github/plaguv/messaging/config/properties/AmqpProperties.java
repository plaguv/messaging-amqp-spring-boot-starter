package io.github.plaguv.messaging.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "amqp")
@Validated
public record AmqpProperties(
        // Required
        @NotBlank
        String centralExchange,
        @NotBlank
        String centralApplication,

        // Optional; will be set to default
        Boolean declareExchangeDurable,
        Boolean declareExchangeDeletable,

        Boolean declareQueueDurable,
        Boolean declareQueueExclusive,
        Boolean declareQueueDeletable
) {
    public AmqpProperties {
        declareExchangeDurable = declareExchangeDurable != null
                ? declareExchangeDurable
                : true;
        declareExchangeDeletable = declareExchangeDeletable != null
                ? declareExchangeDeletable
                : false;

        declareQueueDurable = declareQueueDurable != null
                ? declareQueueDurable
                : true;
        declareQueueExclusive = declareQueueExclusive != null
                ? declareQueueExclusive
                : false;
        declareQueueDeletable = declareQueueDeletable != null
                ? declareQueueDeletable
                : false;
    }
}