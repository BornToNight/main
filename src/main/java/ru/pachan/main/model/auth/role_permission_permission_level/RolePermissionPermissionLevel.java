package ru.pachan.main.model.auth.role_permission_permission_level;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import ru.pachan.main.model.auth.Permission;
import ru.pachan.main.model.auth.PermissionLevel;
import ru.pachan.main.model.auth.Role;

@Entity
@Table(name = "roles_permissions_permission_levels")
public class RolePermissionPermissionLevel {

    @EmbeddedId
    private RolePermissionPermissionLevelId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id")
    private Permission permission;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("permissionLevelId")
    @JoinColumn(name = "permission_level_id")
    private PermissionLevel permissionLevel;

}