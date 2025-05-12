package ru.pachan.main.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PermissionLevelEnum {
    PERMISSION_READ((short) 1),
    PERMISSION_WRITE((short) 2),
    PERMISSION_UPDATE((short) 3),
    PERMISSION_DELETE((short) 4);

    private final Short permissionLevel;
}
