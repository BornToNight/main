package ru.pachan.main.service.reader.notification;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.rpc.ErrorInfo;
import com.google.rpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.pachan.grpc.NotificationServiceGrpc;
import ru.pachan.grpc.Reader;
import ru.pachan.main.dto.reader.NotificationDto;
import ru.pachan.main.exception.data.RequestException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static ru.pachan.grpc.Reader.FindByPersonIdNotificationRequest;
import static ru.pachan.grpc.Reader.FindByPersonIdNotificationResponse;
import static ru.pachan.main.util.enums.ExceptionEnum.SYSTEM_ERROR;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    @GrpcClient("reader-server")
    private NotificationServiceGrpc.NotificationServiceBlockingStub notificationServiceBlockingStub;

    public NotificationDto findByPersonIdNotification(long personId) throws RequestException {
        FindByPersonIdNotificationRequest findByPersonIdNotificationRequest =
                FindByPersonIdNotificationRequest.newBuilder()
                        .setPersonId(personId)
                        .build();

        try {
            FindByPersonIdNotificationResponse findByPersonIdNotificationResponse =
                    notificationServiceBlockingStub.findByPersonIdNotification(findByPersonIdNotificationRequest);
            Reader.Notification notification = findByPersonIdNotificationResponse.getNotification();
            return new NotificationDto(
                    notification.getNotificationId(),
                    notification.getPersonId(),
                    notification.getCount()
            );
        } catch (StatusRuntimeException e) {
            try {
                Status status = StatusProto.fromThrowable(e);
                ErrorInfo errorInfo = null;
                for (Any any : status.getDetailsList()) {
                    if (!any.is(ErrorInfo.class)) {
                        continue;
                    }
                    errorInfo = any.unpack(ErrorInfo.class);
                }
                throw new RequestException(errorInfo.getMetadataMap().get("message"), HttpStatus.valueOf(Integer.parseInt(errorInfo.getMetadataMap().get("httpStatus"))));
            } catch (NullPointerException | InvalidProtocolBufferException exception) {
                throw new RequestException(SYSTEM_ERROR.getMessage(), INTERNAL_SERVER_ERROR);
            }
        }

    }

    public NotificationDto findByIdNotification(long notificationId) throws RequestException {
        Reader.FindByIdNotificationRequest findByIdNotificationRequest =
                Reader.FindByIdNotificationRequest.newBuilder()
                        .setNotificationId(notificationId)
                        .build();

        try {
            Reader.FindByIdNotificationResponse findByIdNotificationResponse =
                    notificationServiceBlockingStub.findByIdNotification(findByIdNotificationRequest);
            Reader.Notification notification = findByIdNotificationResponse.getNotification();
            return new NotificationDto(
                    notification.getNotificationId(),
                    notification.getPersonId(),
                    notification.getCount()
            );
        } catch (StatusRuntimeException e) {
            try {
                Status status = StatusProto.fromThrowable(e);
                ErrorInfo errorInfo = null;
                for (Any any : status.getDetailsList()) {
                    if (!any.is(ErrorInfo.class)) {
                        continue;
                    }
                    errorInfo = any.unpack(ErrorInfo.class);
                }
                throw new RequestException(errorInfo.getMetadataMap().get("message"), HttpStatus.valueOf(Integer.parseInt(errorInfo.getMetadataMap().get("httpStatus"))));
            } catch (NullPointerException | InvalidProtocolBufferException exception) {
                throw new RequestException(SYSTEM_ERROR.getMessage(), INTERNAL_SERVER_ERROR);
            }
        }

    }
}
