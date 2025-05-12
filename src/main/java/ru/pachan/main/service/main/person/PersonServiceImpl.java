package ru.pachan.main.service.main.person;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pachan.main.dto.dictionary.PaginatedResponse;
import ru.pachan.main.dto.main.PersonDto;
import ru.pachan.main.dto.main.PersonNameAndOrgNameDto;
import ru.pachan.main.dto.main.PersonNameDto;
import ru.pachan.main.exception.data.RequestException;
import ru.pachan.main.mapper.PersonMapper;
import ru.pachan.main.model.main.Person;
import ru.pachan.main.model.main.PersonQueryBuilder;
import ru.pachan.main.repository.main.person.PersonDao;
import ru.pachan.main.repository.main.person.PersonRepository;
import ru.pachan.main.repository.main.person.PersonSpecification;

import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static ru.pachan.main.util.enums.ExceptionEnum.OBJECT_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository repository;
    private final PersonDao personDao;
    private final PersonMapper personMapper;

    @Transactional
    @Override
    public PaginatedResponse<PersonDto> getAll(Pageable pageable, String firstName, List<String> firstNames) {
        Page<PersonDto> persons = repository.findAllPersonsDTOWithFilters(firstName, firstNames, pageable);
        return new PaginatedResponse<>(persons.getTotalElements(), persons.getContent());
    }

    @Transactional
    @Override
    public PaginatedResponse<PersonNameDto> getAllNames(Pageable pageable) {
        Page<Person> persons = repository.findAll(pageable);
        return new PaginatedResponse<>(persons.getTotalElements(), personMapper.toPersonNameListDto(persons.getContent()));
    }

    @Transactional
    @Override
    public PaginatedResponse<PersonNameAndOrgNameDto> getAllNamesAndOrgNames(Pageable pageable) {
        Page<Person> persons = repository.findAll(pageable);
        // ATTENTION будет n+1 - энтити граф может исправить ситуацию
        return new PaginatedResponse<>(persons.getTotalElements(), personMapper.toPersonNameAndOrgNameListDto(persons.getContent()));
    }

    @Override
    public PaginatedResponse<PersonQueryBuilder> getAllWithSqlQueryBuilder(String firstName, List<String> firstNames) {
        return personDao.getPersons(firstName, firstNames);
    }

    @Transactional
    @Override
    public PaginatedResponse<PersonDto> getAllWithSpecification(Pageable pageable, String firstName) {
        PersonSpecification specification = new PersonSpecification(firstName);
        Page<Person> persons = repository.findAll(specification, pageable);
        List<PersonDto> result = persons.getContent().stream().map(it -> new PersonDto(it.getId(), it.getFirstName(), it.getOrganization().getName())).toList();
        return new PaginatedResponse<>(persons.getTotalElements(), result);
    }

    @Override
    public Person getOne(long id) throws RequestException {
        return repository.findById(id).orElseThrow(() ->
                new RequestException(OBJECT_NOT_FOUND.getMessage(), HttpStatus.GONE));

    }

    @Override
    public Person createOne(Person person) {
        return repository.save(person);
    }

    @Override
    public Person updateOne(long id, Person person) throws RequestException {
        Person oldPerson = repository.findById(id).orElseThrow(() ->
                new RequestException(OBJECT_NOT_FOUND.getMessage(), UNAUTHORIZED));
        oldPerson.setFirstName(person.getFirstName());
//        oldPerson.setSurname(person.getSurname());
        oldPerson.setSalaryRub(person.getSalaryRub());
        oldPerson.setHobby(person.getHobby());
        return repository.save(oldPerson);
    }

    @Override
    public void deleteOne(long id) {
        repository.deleteById(id);
    }

}
