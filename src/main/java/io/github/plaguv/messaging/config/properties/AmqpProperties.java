package io.github.plaguv.messaging.config.properties;

import io.github.plaguv.contracts.common.EventScope;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fhdw.messaging")
public class AmqpProperties implements InitializingBean {

    /**
     * Exchange used by {@code AMQP} for <b>internal, service-local events</b>.
     * Example values include:
     * <ul>
     * <li>store.prod</li>
     * <li>store.dev</li>
     * <li>store.test</li>
     * </ul>
     * A reasonable default could be set by {@code ${spring.application.name}.${profile}}
     */
    private String internalExchange;

    /**
     * Exchange used by {@code AMQP} for <b>cross-domain integration events</b>.
     * Example values include:
     * <ul>
     * <li>integration.prod</li>
     * <li>integration.dev</li>
     * <li>integration.test</li>
     * </ul>
     * A reasonable default could be set by integration.${profile}}
     */
    private String externalExchange;

    public AmqpProperties() {}

    @Override
    public void afterPropertiesSet() throws Exception {
        if (internalExchange == null || internalExchange.isBlank()) {
            throw new IllegalStateException("Missing required property: fhdw.messaging.internal.exchange");
        }
        if (externalExchange == null || externalExchange.isBlank()) {
            throw new IllegalStateException("Missing required property: fhdw.messaging.external.exchange");
        }
    }

    public String getExchange(EventScope exchangeType) {
        return switch (exchangeType) {
            case INTERNAL -> internalExchange;
            case EXTERNAL -> externalExchange;
            default -> throw new IllegalStateException("Unknown event scope " + exchangeType);
        };
    }
}