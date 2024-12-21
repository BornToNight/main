package ru.pachan.main.service.main.certificate;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.pachan.main.dto.dictionary.PaginatedResponse;
import ru.pachan.main.exception.data.RequestException;
import ru.pachan.main.model.main.Certificate;
import ru.pachan.main.repository.main.CertificateRepository;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static ru.pachan.main.util.enums.ExceptionEnum.OBJECT_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository repository;

    @Override
    public PaginatedResponse<Certificate> getAll(Pageable pageable) {
        Page<Certificate> result = repository.findAll(pageable);

        return new PaginatedResponse<>(result.getTotalElements(), result.getContent());
    }

    @Override
    public Certificate getOne(long id) throws RequestException {
        return repository.findById(id).orElseThrow(() ->
                new RequestException(OBJECT_NOT_FOUND.getMessage(), HttpStatus.GONE));
    }

    @Override
    public Certificate createOne(Certificate certificate) {
        return repository.save(certificate);
    }

    @Override
    public Certificate updateOne(long id, Certificate certificate) throws RequestException {
        Certificate oldCertificate = repository.findById(id).orElseThrow(() ->
                new RequestException(OBJECT_NOT_FOUND.getMessage(), UNAUTHORIZED));
        oldCertificate.setCode(certificate.getCode());
        return repository.save(oldCertificate);
    }

    @Override
    public void deleteOne(long id) {
        repository.deleteById(id);
    }

}
