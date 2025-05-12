package ru.pachan.main.dto.main;

public record PersonDto(
        long id,
        String firstName,
//        String surname,
        String organizationName
) {
}
