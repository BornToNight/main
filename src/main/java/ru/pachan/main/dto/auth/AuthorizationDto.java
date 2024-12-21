package ru.pachan.main.dto.auth;

public record AuthorizationDto(
        String login,
        String password
) {
}
