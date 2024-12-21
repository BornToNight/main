package ru.pachan.main.controller.main;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;
import ru.pachan.main.dto.dictionary.PaginatedResponse;
import ru.pachan.main.dto.main.organization.OrganizationDto;
import ru.pachan.main.exception.data.RequestException;
import ru.pachan.main.model.main.Organization;
import ru.pachan.main.service.main.organization.OrganizationService;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/main/organization")
@Tag(name = "Organization")
public class OrganizationController {

    private final OrganizationService service;

    @Operation(summary = "Возвращение всех с фильтрацией")
    @GetMapping
    public ResponseEntity<PaginatedResponse<Organization>> getAll(
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @Operation(summary = "Возвращение всех с фильтрацией")
    @GetMapping("/entityGraph")
    public ResponseEntity<PaginatedResponse<OrganizationDto>> getAllWithEntityGraph(
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAllWithEntityGraph(pageable));
    }

    @Operation(summary = "Возвращение всех с фильтрацией")
    @GetMapping("/entityGraph/v2")
    public ResponseEntity<PaginatedResponse<OrganizationDto>> getAllWithEntityGraph2(
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAllWithEntityGraph2(pageable));
    }

    @Operation(summary = "Возвращение по переданному id")
    @GetMapping("/{id}")
    public ResponseEntity<Organization> getOne(
            @PathVariable long id
    ) throws RequestException {
        return ResponseEntity.ok(service.getOne(id));
    }

    @Operation(summary = "Создание")
    @PostMapping
    public ResponseEntity<Organization> createOne(
            @Valid @RequestBody Organization organization
    ) {
        return ResponseEntity.ok(service.createOne(organization));
    }

    @Operation(summary = "Обновление")
    @PutMapping("/{id}")
    public ResponseEntity<Organization> updateOne(
            @PathVariable long id,
            @Valid @RequestBody Organization organization
    ) throws RequestException {
        return ResponseEntity.ok(service.updateOne(id, organization));
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
