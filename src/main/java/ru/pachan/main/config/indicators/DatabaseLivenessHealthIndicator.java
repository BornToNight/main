package ru.pachan.main.config.indicators;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.pachan.main.util.sql.DatabaseConnectionChecker;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RequiredArgsConstructor
@Component
public class DatabaseLivenessHealthIndicator implements HealthIndicator {

    private final DatabaseConnectionChecker connectionChecker;

    private final ReentrantLock lock = new ReentrantLock();

    @Value("${application.health-indicators.database-liveness-threshold}")
    private Duration livenessThreshold;

    private Instant problemStartTime = null;

    @Override
    public Health health() {
        lock.lock();
        try {
            Instant now = Instant.now();

            if (connectionChecker.isDatabaseConnected()) {
                return handleConnectionExists(now);
            } else if (isProblemBegin()) {
                return handleFirstFailure(now);
            } else {
                return handleOngoingFailure(now);
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean isProblemBegin() {
        return problemStartTime == null;
    }

    private Health handleConnectionExists(Instant now) {
        if (isConnectionRestored()) {
            handleConnectionRestored(now);
        }
        return Health.up()
                .withDetail("database", "connected")
                .build();
    }

    private boolean isConnectionRestored() {
        return problemStartTime != null;
    }

    private void handleConnectionRestored(Instant now) {
        log.info("Database connection restored after {} minutes", Duration.between(problemStartTime, now).toMinutes());
        problemStartTime = null;
    }

    private Health handleFirstFailure(Instant now) {
        problemStartTime = now;
        log.warn("Database connection lost, starting timer for liveness");

        return Health.up()
                .withDetail("database", "disconnected")
                .withDetail("problem_started", now.toString())
                .withDetail("problem_duration_minutes", 0)
                .withDetail("remaining_tolerance_minutes", livenessThreshold.toMinutes())
                .withDetail("liveness_threshold_minutes", livenessThreshold.toMinutes())
                .withDetail("status", "tolerating")
                .build();
    }

    private Health handleOngoingFailure(Instant now) {
        long minutesDown = Duration.between(problemStartTime, now).toMinutes();

        if (isTolerable(minutesDown)) {
            return Health.up()
                    .withDetail("database", "disconnected")
                    .withDetail("problem_started", problemStartTime.toString())
                    .withDetail("problem_duration_minutes", minutesDown)
                    .withDetail("remaining_tolerance_minutes", livenessThreshold.toMinutes() - minutesDown)
                    .withDetail("liveness_threshold_minutes", livenessThreshold.toMinutes())
                    .withDetail("status", "tolerating")
                    .build();
        } else {
            log.error("Database unavailable for {} minutes, marking liveness as DOWN", minutesDown);
            return Health.down()
                    .withDetail("database", "disconnected")
                    .withDetail("problem_started", problemStartTime.toString())
                    .withDetail("problem_duration_minutes", minutesDown)
                    .withDetail("liveness_threshold_minutes", livenessThreshold.toMinutes())
                    .withDetail("action", "Container will be restarted")
                    .build();
        }
    }

    private boolean isTolerable(long minutesDown) {
        return minutesDown < livenessThreshold.toMinutes();
    }

}