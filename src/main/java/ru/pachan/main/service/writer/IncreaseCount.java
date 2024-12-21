package ru.pachan.main.service.writer;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.pachan.main.dto.writer.WriterDto;

@RequiredArgsConstructor
@Service
public class IncreaseCount {

    private final KafkaTemplate<String, WriterDto> kafkaTemplate;
    private final String topicName;

    @Scheduled(
            fixedDelayString = "${task.schedule.increase-count.delay}",
            initialDelayString = "${task.schedule.increase-count.initialDelay}"
    )
    @SchedulerLock(
            name = "TaskScheduler_increaseCount",
            lockAtLeastFor = "9s", // EXPLAIN_V минимальное время блокировки
            lockAtMostFor = "20s" // EXPLAIN_V максимальное время блокировки (должно быть больше времени выполнения работы)
    )
    public void increaseCount() {
        kafkaTemplate.send(topicName, new WriterDto(1L, 1));
    }

}
