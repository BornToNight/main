package ru.pachan.main.controller.reader;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pachan.main.dto.reader.NotificationDto;
import ru.pachan.main.exception.data.RequestException;
import ru.pachan.main.exception.data.RequestSystemException;
import ru.pachan.main.service.reader.NotificationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/main/notification")
@Tag(name = "Notification")
public class NotificationController {

    private final NotificationService service;

    @Operation(summary = "Возвращение по переданному id")
    @GetMapping("/{id}")
    public ResponseEntity<NotificationDto> getOne(
            @PathVariable long id
    ) throws RequestException, RequestSystemException {
        return ResponseEntity.ok(service.findByIdNotification(id));
    }

    @Operation(summary = "Возвращение по переданному id")
    @GetMapping("/personId/{id}")
    public ResponseEntity<NotificationDto> getOneByPersonId(
            @PathVariable long id
    ) throws RequestException, RequestSystemException {
        return ResponseEntity.ok(service.findByPersonIdNotification(id));
    }

}
