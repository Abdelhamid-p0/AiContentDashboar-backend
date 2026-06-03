package com.quiz.ai.infrastructure.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Profiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
@Profile({ "prod", "dev" })
public class ProdDataSourceConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ProdDataSourceConfiguration.class);

    @Bean
    public HikariDataSource dataSource(Environment environment) {
        String jdbcUrl = firstNonBlank(
                environment.getProperty("NEON_JDBC_URL"),
                environment.getProperty("SPRING_DATASOURCE_URL"),
                environment.getProperty("JDBC_DATABASE_URL"),
                environment.getProperty("DATABASE_URL"),
                buildJdbcUrlFromParts(environment));

        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            throw new IllegalStateException(
                    "Missing database connection details. Set NEON_JDBC_URL, SPRING_DATASOURCE_URL, JDBC_DATABASE_URL, DATABASE_URL, or PGHOST/PGDATABASE/PGUSER/PGPASSWORD.");
        }

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(normalizeJdbcUrl(jdbcUrl));
        dataSource.setUsername(firstNonBlank(
                environment.getProperty("SPRING_DATASOURCE_USERNAME"),
                environment.getProperty("JDBC_DATABASE_USERNAME"),
                environment.getProperty("DATABASE_USERNAME"),
                environment.getProperty("PGUSER"),
                environment.getProperty("NEON_USERNAME")));
        dataSource.setPassword(firstNonBlank(
                environment.getProperty("SPRING_DATASOURCE_PASSWORD"),
                environment.getProperty("JDBC_DATABASE_PASSWORD"),
                environment.getProperty("DATABASE_PASSWORD"),
                environment.getProperty("PGPASSWORD"),
                environment.getProperty("NEON_PASSWORD")));
        dataSource.setDriverClassName("org.postgresql.Driver");

        // Try to validate connection. If it fails and we're in dev, fall back to H2
        // in-memory DB.
        try {
            dataSource.getConnection().close();
            return dataSource;
        } catch (Exception e) {
            log.warn("Failed to connect to configured JDBC datasource: {}", e.getMessage());

            // Only fallback to H2 when running with the 'dev' profile
            if (environment.acceptsProfiles(Profiles.of("dev"))) {
                log.info("Falling back to in-memory H2 datasource for 'dev' profile.");
                HikariDataSource h2 = new HikariDataSource();
                h2.setJdbcUrl("jdbc:h2:mem:devdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
                h2.setUsername("sa");
                h2.setPassword("");
                h2.setDriverClassName("org.h2.Driver");
                return h2;
            }

            throw new IllegalStateException("Unable to connect to JDBC datasource and not in dev profile", e);
        }
    }

    private String buildJdbcUrlFromParts(Environment environment) {
        String host = firstNonBlank(
                environment.getProperty("PGHOST"),
                environment.getProperty("NEON_HOST"));
        String port = firstNonBlank(environment.getProperty("PGPORT"), "5432");
        String database = firstNonBlank(environment.getProperty("PGDATABASE"),
                environment.getProperty("DATABASE_NAME"),
                environment.getProperty("NEON_DATABASE"));

        if (isBlank(host) || isBlank(database)) {
            return null;
        }

        return "jdbc:postgresql://" + host + ":" + port + "/" + database + "?sslmode=require";
    }

    private String normalizeJdbcUrl(String url) {
        if (url.startsWith("jdbc:")) {
            return url;
        }

        if (url.startsWith("postgresql://")) {
            return "jdbc:" + url;
        }

        if (url.startsWith("postgres://")) {
            return "jdbc:postgresql://" + url.substring("postgres://".length());
        }

        return url;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}