package ru.pachan.main.dto.reader;

public record NotificationDto(
        long notification_id,
        long person_id,
        long count
) {
}
