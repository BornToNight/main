package ru.pachan.main.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pachan.main.model.auth.PermissionLevel;

@Repository
public interface PermissionLevelRepository extends JpaRepository<PermissionLevel, Long> {

    @Query(
            """
            SELECT pl.permissionLevel
                FROM PermissionLevel pl
                    JOIN pl.rolePermissionPermissionLevels.permission p
                    JOIN pl.rolePermissionPermissionLevels.role r
                WHERE
                    r.id = :roleId
                    AND p.uname = :permissionUname
            """
    )
    Short findPermissionLevelByRoleIdAndPermissionUname(
            @Param("roleId") Long roleId,
            @Param("permissionUname") String permissionUname
    );

}
