package ru.pachan.main.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MdcKeyEnum {

    REQUEST_UID("requestUid"),
    REQUEST_URL("requestUrl"),
    USER_ID("userId");

    private final String key;

}
