package ru.pachan.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.pachan.main.dto.main.PersonNameAndOrgNameDto;
import ru.pachan.main.dto.main.PersonNameDto;
import ru.pachan.main.model.main.Person;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PersonMapper {

    PersonNameDto toPersonNameDto(Person person);

    List<PersonNameDto> toPersonNameListDto(List<Person> personList);

    @Mapping(source = "organization.name", target = "organizationName")
    PersonNameAndOrgNameDto toPersonNameAndOrgNameDto(Person person);

    List<PersonNameAndOrgNameDto> toPersonNameAndOrgNameListDto(List<Person> personList);

}
