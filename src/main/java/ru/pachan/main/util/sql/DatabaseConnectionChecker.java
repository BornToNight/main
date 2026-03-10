package ru.pachan.main.util.sql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
@Component
public class DatabaseConnectionChecker {

    private final DataSource healthCheckDataSource;

    public DatabaseConnectionChecker(
            @Qualifier("healthCheckDataSource") DataSource healthCheckDataSource
    ) {
        this.healthCheckDataSource = healthCheckDataSource;
    }

    public boolean isDatabaseConnected() {
        try {
            try (var connection = healthCheckDataSource.getConnection()) {
                return connection.isValid(1);
            }
        } catch (Exception e) {
            log.debug("Database connection check failed", e);
            return false;
        }
    }

}