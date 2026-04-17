package com.simple.common.base;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleDaoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(UserIdProvider.class)
    public UserIdProvider userIdProvider() {
        return new SimpleUserIdProvider();
    }
}