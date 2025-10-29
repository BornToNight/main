package ru.pachan.main.repository.main.person;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pachan.main.dto.main.PersonDto;
import ru.pachan.main.model.main.Person;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {

    @Query(
            """
            SELECT new ru.pachan.main.dto.main_kotlin.PersonDto(p.id, p.firstName, p.organization.name)
                FROM Person p
                WHERE
                    (LOWER(firstName) LIKE CONCAT('%', LOWER(:firstName), '%') OR :firstName IS NULL)
                    AND (firstName IN (:firstNames) OR :firstNames IS NULL )
            """
    )
    Page<PersonDto> findAllPersonsDTOWithFilters(
            @Param("firstName") String firstName,
            @Param("firstNames") List<String> firstNames,
            Pageable pageable
    );

}