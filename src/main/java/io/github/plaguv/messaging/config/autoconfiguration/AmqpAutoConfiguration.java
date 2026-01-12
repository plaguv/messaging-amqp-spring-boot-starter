package io.github.plaguv.messaging.config.autoconfiguration;

import io.github.plaguv.messaging.config.properties.AmqpProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.*;

@AutoConfiguration
@EnableConfigurationProperties(AmqpProperties.class)
public class AmqpAutoConfiguration {

    public AmqpAutoConfiguration() {}

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
// add if messaging isn't consistent
//        mapper.registeredModules();
//
//        mapper.registerModule(new JavaTimeModule());
//        mapper.registerModule(new Jdk8Module());
//        mapper.registerModule(new ParameterNamesModule());
//
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
//
//        mapper.enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
//        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
//
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);

        return new ObjectMapper();
    }
}