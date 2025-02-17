package ru.pachan.main.model.main;

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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Schema(description = "Сотрудник")
@Table(name = "persons")
public class Person implements Serializable {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String surname;

    @Column
    @Digits(integer = 10, fraction = 2)
    @Schema(description = "Максимум 10 знаков до и 2 знака после запятой")
    private BigDecimal salaryRub;

    @Column
    private String hobby;

    @Column(name = "fk_organization_id")
    long organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "fk_organization_id",
            referencedColumnName = "organization_id",
            insertable = false,
            updatable = false
    )
    @JsonIgnore
    @ToString.Exclude
    private Organization organization;

    @Column(name = "fk_certificate_id", unique = true)
    long certificateId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "fk_certificate_id",
            referencedColumnName = "certificate_id",
            insertable = false,
            updatable = false
    )
    @ToString.Exclude
    private Certificate certificate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "persons_skills",
            joinColumns = @JoinColumn(name = "person_id", referencedColumnName = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id", referencedColumnName = "skill_id")
    )
    @ToString.Exclude
    private Set<Skill> skills;

    @Id
    @SequenceGenerator(
            name = "persons_seq",
            sequenceName = "persons_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "persons_seq")
    @Column(name = "person_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

}
