package ru.pachan.main.model.main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
@Entity
@Schema(description = "Удостоверение")
@Table(name = "certificates")
public class Certificate implements Serializable {

    @Column(nullable = false)
    private String code;

    @OneToOne(mappedBy = "certificate", optional = false, cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private Person person;

    @Id
    @SequenceGenerator(
            name = "certificates_seq",
            sequenceName = "certificates_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "certificates_seq")
    @Column(name = "certificate_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

}
