package ru.pachan.main.service.reader.notification;

import ru.pachan.main.dto.reader.NotificationDto;
import ru.pachan.main.exception.data.RequestException;

public interface NotificationService {

    NotificationDto findByPersonIdNotification(long personId) throws RequestException;

    NotificationDto findByIdNotification(long notificationId) throws RequestException;

}
