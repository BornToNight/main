package ru.pachan.main.service.main;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.pachan.main.dto.dictionary.PaginatedResponse;
import ru.pachan.main.exception.data.RequestException;
import ru.pachan.main.model.main.Certificate;
import ru.pachan.main.repository.main.CertificateRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static ru.pachan.main.util.enums.ExceptionEnum.OBJECT_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class CertificateService {

    private final CertificateRepository repository;

    public PaginatedResponse<Certificate> getAll(Pageable pageable) {
        Page<Certificate> result = repository.findAll(pageable);

        return new PaginatedResponse<>(result.getTotalElements(), result.getContent());
    }

    public Certificate getOne(long id) throws RequestException {
        return repository.findById(id).orElseThrow(() ->
                new RequestException(OBJECT_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));
    }

    public Certificate createOne(Certificate certificate) {
        return repository.save(certificate);
    }

    public Certificate updateOne(long id, Certificate certificate) throws RequestException {
        Certificate oldCertificate = repository.findById(id).orElseThrow(() ->
                new RequestException(OBJECT_NOT_FOUND.getMessage(), UNAUTHORIZED));
        oldCertificate.setCode(certificate.getCode());
        return repository.save(oldCertificate);
    }

    public void deleteOne(long id) {
        repository.deleteById(id);
    }

    public void massiveCreation() {
        int threadCount = 3;
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            // Каждый поток будет сохранять по 500 сертификатов
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                executor.submit(() -> saveCertificatesForThread(threadId));
            }
        }
    }

    private void saveCertificatesForThread(int threadId) {
        int totalCertificates = 500;
        // Каждый поток сохраняет по 500 сертификатов
        for (int i = 0; i < totalCertificates; i++) {
            repository.save(new Certificate().setCode("massCert" + i + "threadId" + threadId));
        }
    }

}
