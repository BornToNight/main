package ru.pachan.main.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "9s")
public class SchedulerConfiguration {

// EXPLAIN_V Для постгри
//    @Bean
//    public LockProvider lockProvider(final DataSource dataSource) {
//        return new JdbcTemplateLockProvider(dataSource);
//    }

    @Bean
    public LockProvider lockProvider(LettuceConnectionFactory lettuceConnectionFactory) {
        return new RedisLockProvider(lettuceConnectionFactory);
    }

}
