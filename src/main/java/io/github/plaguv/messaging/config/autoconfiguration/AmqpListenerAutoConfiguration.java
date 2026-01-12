package io.github.plaguv.messaging.config.autoconfiguration;

import io.github.plaguv.messaging.config.properties.AmqpProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration(after = AmqpAutoConfiguration.class)
@EnableConfigurationProperties(AmqpProperties.class)
public class AmqpListenerAutoConfiguration {

    public AmqpListenerAutoConfiguration() {}

}