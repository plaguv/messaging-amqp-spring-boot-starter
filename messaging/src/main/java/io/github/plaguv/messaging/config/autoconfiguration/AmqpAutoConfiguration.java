package io.github.plaguv.messaging.config.autoconfiguration;

import io.github.plaguv.messaging.config.properties.AmqpProperties;
import io.github.plaguv.messaging.config.properties.AmqpStartupProperties;
import io.github.plaguv.messaging.utlity.AmqpEventRouter;
import io.github.plaguv.messaging.utlity.EventRouter;
import org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@AutoConfiguration(after = RabbitAutoConfiguration.class)
@EnableConfigurationProperties({AmqpProperties.class, AmqpStartupProperties.class})
public class AmqpAutoConfiguration {

    public AmqpAutoConfiguration() {}

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().build();
    }

    @Bean
    @ConditionalOnMissingBean
    public EventRouter eventRouter(AmqpProperties amqpProperties) {
        return new AmqpEventRouter(amqpProperties);
    }
}