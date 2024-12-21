package ru.pachan.main.controller.main;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pachan.main.dto.dictionary.PaginatedResponse;
import ru.pachan.main.dto.main.PersonDto;
import ru.pachan.main.exception.data.RequestException;
import ru.pachan.main.model.main.Person;
import ru.pachan.main.service.main.person.PersonService;

import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/main/person")
@Tag(name = "Person")
@Slf4j
public class PersonController {

    private final PersonService service;

    @Operation(summary = "Возвращение всех с фильтрацией")
    @GetMapping
    public ResponseEntity<PaginatedResponse<PersonDto>> getAll(
            @ParameterObject Pageable pageable,
            @Parameter(description = "Фильтр по имени сотрудника")
            @RequestParam(required = false) String firstName,
            @Parameter(description = "Фильтр по именам сотрудника")
            @RequestParam(required = false) List<String> firstNames
    ) {
        // EXPLAIN_V Пример для сохранения в elastic
        log.info("PersonController getAll");
        return ResponseEntity.ok(service.getAll(pageable, firstName, firstNames));
    }

    @Operation(summary = "Возвращение по переданному id")
    @GetMapping("/{id}")
    public ResponseEntity<Person> getOne(
            @PathVariable long id
    ) throws RequestException {
        return ResponseEntity.ok(service.getOne(id));
    }

    @Operation(summary = "Создание")
    @PostMapping
    public ResponseEntity<Person> createOne(
            @Valid @RequestBody Person person
    ) {
        return ResponseEntity.ok(service.createOne(person));
    }

    @Operation(summary = "Обновление")
    @PutMapping("/{id}")
    public ResponseEntity<Person> updateOne(
            @PathVariable long id,
            @Valid @RequestBody Person person
    ) throws RequestException {
        return ResponseEntity.ok(service.updateOne(id, person));
    }

    @ApiResponse(responseCode = "204")
    @Operation(summary = "Удаление по переданному id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOne(
            @PathVariable long id
    ) {
        try {
            service.deleteOne(id);
            return ResponseEntity.status(NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(NO_CONTENT).build();
        }
    }

}
