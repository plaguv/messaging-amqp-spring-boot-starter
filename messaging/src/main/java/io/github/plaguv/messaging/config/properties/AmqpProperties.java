package io.github.plaguv.messaging.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "amqp")
@Validated
public record AmqpProperties(
        @NotBlank
        String centralExchange,
        @NotBlank
        String centralApplication
) {}