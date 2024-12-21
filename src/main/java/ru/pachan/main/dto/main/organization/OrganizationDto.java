package ru.pachan.main.dto.main.organization;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
public class OrganizationDto implements Serializable {
    private long id;
    private String name;
    private Set<PersonOrganizationDto> person;
}
