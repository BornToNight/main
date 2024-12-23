package ru.pachan.main.service.reader.notification;

import ru.pachan.main.dto.reader.NotificationDto;
import ru.pachan.main.exception.data.RequestException;
import ru.pachan.main.exception.data.RequestSystemException;

public interface NotificationService {

    NotificationDto findByPersonIdNotification(long personId) throws RequestException, RequestSystemException;

    NotificationDto findByIdNotification(long notificationId) throws RequestException, RequestSystemException;

}
