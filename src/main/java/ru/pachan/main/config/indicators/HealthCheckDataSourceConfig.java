package ru.pachan.main.config.indicators;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class HealthCheckDataSourceConfig {

    @Bean
    @Qualifier("healthCheckDataSource")
    public DataSource healthCheckDataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password
    ) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setConnectionTimeout(3000);
        config.setValidationTimeout(2000);
        config.setMaximumPoolSize(1);
        config.setMinimumIdle(0);
        config.setIdleTimeout(10000);
        config.setPoolName("HealthCheckPool");

        log.debug("Creating HealthCheckDataSource with pool name: {}", config.getPoolName());

        return new HikariDataSource(config);
    }

}