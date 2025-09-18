package ru.pachan.main.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pachan.main.dto.dictionary.PaginatedResponse;
import ru.pachan.main.exception.data.RequestException;
import ru.pachan.main.model.auth.User;
import ru.pachan.main.service.auth.UserService;

@CrossOrigin
@RestController
@RequestMapping("api/auth/user")
@RequiredArgsConstructor
@Tag(name = "User")
class UserController {

    private final UserService service;

    @Operation(summary = "Создание пользователя")
    @PostMapping
    public ResponseEntity<User> createOne(
            @RequestBody User user
    ) throws RequestException {
        return ResponseEntity.ok(service.createOne(user));
    }

    @Operation(summary = "Возвращение всех пользователей с фильтрацией")
    @GetMapping
    public ResponseEntity<PaginatedResponse<User>> getAll(
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @Operation(summary = "Возвращение пользователя по переданному id")
    @GetMapping("/{id}")
    public ResponseEntity<User> getOne(
            @Parameter(description = "Id пользователя")
            @PathVariable Long id,
            @RequestHeader("Authorization") String authToken
    ) throws RequestException {
        return ResponseEntity.ok(service.getOne(id, authToken));
    }


    @Operation(summary = "Обновление пользователя", description = "Обновляет данные пользователя с переданным id")
    @PostMapping("/{id}")
    public ResponseEntity<User> updateOne(
            @Parameter(description = "Id пользователя")
            @PathVariable("id") Long id,
            @RequestBody User user,
            @RequestHeader("Authorization") String authToken
    ) throws RequestException {
        return ResponseEntity.ok(service.updateOne(id, authToken, user));
    }

    @ApiResponse(responseCode = "204", content = {@Content(schema = @Schema)})
    @Operation(summary = "Удаление пользователя по переданному id")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteOne(@PathVariable Long id) throws RequestException {
        service.deleteOne(id);
        return ResponseEntity.noContent().build();
    }

}