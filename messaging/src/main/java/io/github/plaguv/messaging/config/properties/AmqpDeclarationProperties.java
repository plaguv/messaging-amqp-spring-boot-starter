package io.github.plaguv.messaging.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "amqp.declaration")
public record AmqpDeclarationProperties(
        Boolean declareExchangeDurable,
        Boolean declareExchangeDeletable,

        Boolean declareQueueDurable,
        Boolean declareQueueExclusive,
        Boolean declareQueueDeletable
) {
    public AmqpDeclarationProperties {
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