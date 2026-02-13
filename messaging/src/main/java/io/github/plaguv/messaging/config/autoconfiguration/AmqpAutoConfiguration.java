package io.github.plaguv.messaging.config.autoconfiguration;

import io.github.plaguv.messaging.config.properties.AmqpDeclarationProperties;
import io.github.plaguv.messaging.config.properties.AmqpProperties;
import io.github.plaguv.messaging.config.properties.AmqpStartupProperties;
import io.github.plaguv.messaging.utlity.AmqpEventRouter;
import io.github.plaguv.messaging.utlity.EventRouter;
import io.github.plaguv.messaging.utlity.converter.EventPayloadByteConverter;
import io.github.plaguv.messaging.utlity.converter.EventPayloadArgumentResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration;
import org.springframework.boot.amqp.autoconfigure.RabbitTemplateCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.util.List;

@AutoConfiguration(after = RabbitAutoConfiguration.class)
@EnableConfigurationProperties({
        AmqpProperties.class,
        AmqpDeclarationProperties.class,
        AmqpStartupProperties.class
})
public class AmqpAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AmqpAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("io.github.plaguv")
                .build();

        return JsonMapper.builder()
                .polymorphicTypeValidator(ptv)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(EventRouter.class)
    public EventRouter eventRouter(AmqpProperties amqpProperties) {
        return new AmqpEventRouter(amqpProperties);
    }

    @Bean
    public RabbitTemplateCustomizer amqpEventTemplateCustomizer() {
        return rabbitTemplate -> {
            rabbitTemplate.setMandatory(true);
            rabbitTemplate.setReturnsCallback(returned ->
                    log.atError().log(
                            "Unroutable message. exchange={}, routingKey={}",
                            returned.getExchange(),
                            returned.getRoutingKey()
                    )
            );
        };
    }

    @Bean
    public SimpleRabbitListenerContainerFactory amqpEventListenerContainerFactory(ConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        factory.setMessageConverter(new EventPayloadByteConverter(objectMapper));
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setChannelTransacted(false);
        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
                .maxRetries(5)
                .backOffOptions(1000, 5.0, 100_000)
                .build());

        return factory;
    }

    @Bean
    public MessageHandlerMethodFactory amqpMessageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();

        factory.setCustomArgumentResolvers(List.of(
                new EventPayloadArgumentResolver()
        ));
        factory.afterPropertiesSet();

        return factory;
    }
}