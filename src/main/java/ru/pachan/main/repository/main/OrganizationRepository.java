package ru.pachan.main.repository.main;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pachan.main.model.main.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    // v1
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = "persons")
    Page<Organization> findAllWithEntityGraphBy(Pageable pageable);

    // v2
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "organization_entity-graph")
    Page<Organization> findAllWithEntityGraph2By(Pageable pageable);

}
