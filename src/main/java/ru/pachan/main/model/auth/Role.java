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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.pachan.main.model.auth.role_permission_permission_level.RolePermissionPermissionLevel;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Schema(description = "Роли пользователя")
@Table(name = "roles")
public class Role {

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private Set<User> users;

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    @ToString.Exclude
    private List<RolePermissionPermissionLevel> rolePermissionPermissionLevels;

    @Id
    @SequenceGenerator(
            name = "roles_seq",
            sequenceName = "roles_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_seq")
    @Column(name = "role_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

}
