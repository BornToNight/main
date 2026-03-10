package ru.pachan.main.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuthorityEnum {

    VERIFIED_TOKEN("VerifiedToken");

    private final String authority;
}
