package ru.pachan.main.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuthorityEnum {

    VERIFIED_TOKEN("VerifiedToken"),
    ACTUATOR_ADMIN("ActuatorAdmin");

    private final String authority;
}
