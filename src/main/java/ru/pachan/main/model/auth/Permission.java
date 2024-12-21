package ru.pachan.main.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.pachan.main.model.auth.rolePermissionPermissionLevel.RolePermissionPermissionLevel;

import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Schema(description = "Права пользователя")
@Table(name = "permissions")
public class Permission {

    @Column(nullable = false, unique = true)
    private String uname;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "permission", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    @ToString.Exclude
    private List<RolePermissionPermissionLevel> rolePermissionPermissionLevels;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

}
