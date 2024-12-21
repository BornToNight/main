package ru.pachan.main.service.main.organization;

import org.springframework.data.domain.Pageable;
import ru.pachan.main.dto.dictionary.PaginatedResponse;
import ru.pachan.main.dto.main.organization.OrganizationDto;
import ru.pachan.main.exception.data.RequestException;
import ru.pachan.main.model.main.Organization;

public interface OrganizationService {

    PaginatedResponse<Organization> getAll(Pageable pageable);

    PaginatedResponse<OrganizationDto> getAllWithEntityGraph(Pageable pageable);

    PaginatedResponse<OrganizationDto> getAllWithEntityGraph2(Pageable pageable);

    Organization getOne(long id) throws RequestException;

    Organization createOne(Organization organization);

    Organization updateOne(long id, Organization organization) throws RequestException;

    void deleteOne(long id);

}
