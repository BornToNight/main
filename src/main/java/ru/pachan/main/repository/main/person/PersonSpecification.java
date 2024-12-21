package ru.pachan.main.repository.main.person;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ru.pachan.main.model.main.Person;
import ru.pachan.main.model.main.Person_;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PersonSpecification implements Specification<Person> {

    private final String firstName;

    @Override
    public Predicate toPredicate(
            @NonNull Root<Person> root,
            @NonNull CriteriaQuery<?> query,
            @NonNull CriteriaBuilder criteriaBuilder
    ) {

        List<Predicate> predicates = new ArrayList<>();

        if (firstName != null && !firstName.isBlank()) {
            predicates.add(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get(Person_.firstName)),
                            "%" + firstName.toLowerCase() + "%"
                    )
            );
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

}
