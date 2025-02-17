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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Schema(description = "Рефреш токен")
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Column(nullable = false)
    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_user_id", referencedColumnName = "user_id")
    @JsonIgnore
    @ToString.Exclude
    private User user;

    @Id
    @SequenceGenerator(
            name = "refresh_tokens_seq",
            sequenceName = "refresh_tokens_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_tokens_seq")
    @Column(name = "refresh_token_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

}
