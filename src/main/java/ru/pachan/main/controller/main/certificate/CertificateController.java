package ru.pachan.main.controller.main.certificate;

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
import ru.pachan.main.exception.data.RequestException;
import ru.pachan.main.model.main.Certificate;
import ru.pachan.main.service.main.certificate.CertificateService;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/main/certificate")
@Tag(name = "Certificate")
public class CertificateController {

    private final CertificateService service;

    @Operation(summary = "Возвращение всех с фильтрацией")
    @GetMapping
    public ResponseEntity<PaginatedResponse<Certificate>> getAll(
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @Operation(summary = "Возвращение по переданному id")
    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getOne(
            @PathVariable long id
    ) throws RequestException {
        return ResponseEntity.ok(service.getOne(id));
    }

    @Operation(summary = "Создание")
    @PostMapping
    public ResponseEntity<Certificate> createOne(
            @Valid @RequestBody Certificate certificate
    ) {
        return ResponseEntity.ok(service.createOne(certificate));
    }

    @Operation(summary = "Обновление")
    @PutMapping("/{id}")
    public ResponseEntity<Certificate> updateOne(
            @PathVariable long id,
            @Valid @RequestBody Certificate certificate
    ) throws RequestException {
        return ResponseEntity.ok(service.updateOne(id, certificate));
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

    @Operation(summary = "Создание по 500 пользователей в 3-ёх потоках")
    @PostMapping("/massiveCreation")
    public void massiveCreation(
    ) {
        service.massiveCreation();
    }

}
