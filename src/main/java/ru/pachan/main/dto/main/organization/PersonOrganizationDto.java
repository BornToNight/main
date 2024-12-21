package ru.pachan.main.dto.main.organization;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class PersonOrganizationDto implements Serializable {
    private long id;
    private String firstName;
    private String surname;
}
