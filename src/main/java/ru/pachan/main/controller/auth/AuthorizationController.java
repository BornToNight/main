package ru.pachan.main.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pachan.main.dto.auth.AuthorizationDto;
import ru.pachan.main.dto.auth.RefreshDataDto;
import ru.pachan.main.exception.data.RequestException;
import ru.pachan.main.service.auth.authorization.AuthorizationService;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
@Tag(name = "Authorization")
class AuthorizationController {
    public final AuthorizationService service;

    @Operation(
            summary = "Генерация JWT",
            description = "Генерирует JWT, если переданы верный логин и пароль"
    )
    @PostMapping("/generate")
    public ResponseEntity<RefreshDataDto> generate(
            @RequestBody AuthorizationDto authorizationDto
    ) throws RequestException {
        return ResponseEntity.ok(service.generate(authorizationDto));
    }

    @Operation(
            summary = "Обновление JWT",
            description = "Обновляет JWT, по переданному старому JWT"
    )
    @GetMapping("/refresh")
    public ResponseEntity<RefreshDataDto> refresh(
            @RequestHeader("Authorization") String authToken
    ) throws RequestException {
        return ResponseEntity.ok(service.refresh(authToken));
    }

}