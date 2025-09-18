package ru.pachan.main.service.main;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.pachan.main.dto.dictionary.PaginatedResponse;
import ru.pachan.main.dto.main.organization.OrganizationDto;
import ru.pachan.main.dto.main.organization.PersonOrganizationDto;
import ru.pachan.main.exception.data.RequestException;
import ru.pachan.main.model.main.Organization;
import ru.pachan.main.repository.main.OrganizationRepository;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static ru.pachan.main.util.enums.ExceptionEnum.OBJECT_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class OrganizationService {

    private final OrganizationRepository repository;

    // EXPLAIN_V Возможно обдумать и переделать (Evict) из-за pageable
    @Cacheable(value = "OrganizationService::getAll", key = "#pageable")
    public PaginatedResponse<Organization> getAll(Pageable pageable) {
        Page<Organization> result = repository.findAll(pageable);
        return new PaginatedResponse<>(result.getTotalElements(), result.getContent());
    }

    public PaginatedResponse<OrganizationDto> getAllWithEntityGraph(Pageable pageable) {
        Page<Organization> result = repository.findAllWithEntityGraphBy(pageable);
        return new PaginatedResponse<>(
                result.getTotalElements(),
                result.getContent().stream().map(
                        organization -> new OrganizationDto(
                                organization.getId(),
                                organization.getName(),
                                organization.getPersons().stream().map(
                                        person -> new PersonOrganizationDto(
                                                person.getId(),
                                                person.getFirstName()
//                                                person.getSurname()
                                        )
                                ).collect(Collectors.toSet())
                        )
                ).toList());
    }

    public PaginatedResponse<OrganizationDto> getAllWithEntityGraph2(Pageable pageable) {
        Page<Organization> result = repository.findAllWithEntityGraph2By(pageable);
        return new PaginatedResponse<>(
                result.getTotalElements(),
                result.getContent().stream().map(
                        organization -> new OrganizationDto(
                                organization.getId(),
                                organization.getName(),
                                organization.getPersons().stream().map(
                                        person -> new PersonOrganizationDto(
                                                person.getId(),
                                                person.getFirstName()
//                                                person.getSurname()
                                        )
                                ).collect(Collectors.toSet())
                        )
                ).toList());
    }

    @Cacheable(value = "OrganizationService::getOne", key = "#id")
    public Organization getOne(long id) throws RequestException {
        return repository.findById(id).orElseThrow(() ->
                new RequestException(OBJECT_NOT_FOUND.getMessage(), HttpStatus.GONE));

    }

    @Caching(
            evict = @CacheEvict(value = "OrganizationService::getAll", allEntries = true)
    )
    public Organization createOne(Organization organization) {
        return repository.save(organization);
    }

    @Caching(
            put = @CachePut(value = "OrganizationService::getOne", key = "#id"),
            evict = @CacheEvict(value = "OrganizationService::getAll", allEntries = true)
    )
    public Organization updateOne(long id, Organization organization) throws RequestException {
        Organization oldOrganization = repository.findById(id).orElseThrow(() ->
                new RequestException(OBJECT_NOT_FOUND.getMessage(), UNAUTHORIZED));
        oldOrganization.setName(organization.getName());
        return repository.save(oldOrganization);
    }

    @Caching(evict = {
            @CacheEvict(value = "OrganizationService::getOne", key = "#id"),
            @CacheEvict(value = "OrganizationService::getAll", allEntries = true)
    })
    public void deleteOne(long id) {
        repository.deleteById(id);
    }
}
