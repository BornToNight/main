package ru.pachan.main.dto.auth;

public record RefreshDataDto(
        String refresh,
        String token,
        long roleId,
        long userId
) {
}
