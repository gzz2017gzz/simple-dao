package com.simple.common.base.dialect;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DialectAutoConfiguration {

    @Value("${simple-dao.dialect:}")
    private String configDialect;

    @Bean
    @ConditionalOnMissingBean(DbDialect.class)
    public DbDialect dbDialect(DataSource dataSource) {
        if (configDialect != null && !configDialect.isBlank()) {
            return DbDialect.of(null, configDialect);
        }
        try (Connection conn = dataSource.getConnection()) {
            return DbDialect.of(conn, null);
        } catch (Exception ignored) {
            return DbDialect.of(null, null);
        }
    }
}