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
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Schema(description = "Организация")
@Table(name = "organizations")
// v2
@NamedEntityGraph(name = "organization_entity-graph", attributeNodes = @NamedAttributeNode("persons"))
public class Organization implements Serializable {

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private Set<Person> persons;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

}
