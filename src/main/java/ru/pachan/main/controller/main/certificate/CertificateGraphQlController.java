package ru.pachan.main.controller.main.certificate;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import ru.pachan.main.dto.main.CertificateGraphQlDto;
import ru.pachan.main.model.main.Certificate;
import ru.pachan.main.model.main.Certificate_;
import ru.pachan.main.model.main.Person;
import ru.pachan.main.repository.main.CertificateRepository;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Controller
public class CertificateGraphQlController {

    private final CertificateRepository repository;

    @MutationMapping
    public Certificate newCertificate(@Argument CertificateGraphQlDto certificateGraphQlDto) {
        return repository.save(new Certificate(certificateGraphQlDto.code(), null, 0));
    }

    @QueryMapping
    public Iterable<Certificate> certificates(DataFetchingEnvironment environment) {
        DataFetchingFieldSelectionSet s = environment.getSelectionSet();
        if (s.contains("person"))
            return repository.findAll(fetchPerson());
        else
            return repository.findAll();
    }

    @QueryMapping
    public Certificate certificate(@Argument Integer id, DataFetchingEnvironment environment) {
        Specification<Certificate> spec = byId(id);
        DataFetchingFieldSelectionSet selectionSet = environment
                .getSelectionSet();
        if (selectionSet.contains(Certificate_.person.getName()))
            spec = spec.and(fetchPerson());
        return repository.findOne(spec).orElseThrow(NoSuchElementException::new);
    }

    private Specification<Certificate> fetchPerson() {
        return (root, query, builder) -> {
            Fetch<Certificate, Person> f = root.fetch(Certificate_.person, JoinType.LEFT);
            Join<Certificate, Person> join = (Join<Certificate, Person>) f;
            return join.getOn();
        };
    }

    private Specification<Certificate> byId(Integer id) {
        return (root, query, builder) -> builder.equal(root.get(Certificate_.id), id);
    }

}
