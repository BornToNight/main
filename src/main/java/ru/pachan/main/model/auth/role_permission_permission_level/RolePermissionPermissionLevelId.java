package ru.pachan.main.model.auth.role_permission_permission_level;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RolePermissionPermissionLevelId implements Serializable {

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "permission_id")
    private Long permissionId;

    @Column(name = "permission_level_id")
    private Long permissionLevelId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RolePermissionPermissionLevelId rolePermissionPermissionLevelId = (RolePermissionPermissionLevelId) o;
        return Objects.equals(this.roleId, rolePermissionPermissionLevelId.roleId) &&
               Objects.equals(this.permissionId, rolePermissionPermissionLevelId.permissionId) &&
               Objects.equals(this.permissionLevelId, rolePermissionPermissionLevelId.permissionLevelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, permissionId, permissionLevelId);
    }

}
