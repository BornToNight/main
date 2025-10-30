package ru.pachan.main;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.pachan.main.dto.dictionary.PaginatedResponse;
import ru.pachan.main.model.main.Certificate;
import ru.pachan.main.repository.main.CertificateRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CertificatesIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CertificateRepository certificateRepository;

    @LocalServerPort
    private Integer port;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16")
            .withUsername(System.getenv("POSTGRES_USER"))
            .withPassword("password");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(
                "Authorization",
                "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInJvbGVJZCI6MSwiaWF0IjoxNzIxMTI0" +
                "ODE1LCJleHAiOjE3MjExMjUxMTU5fQ.Vra7txDcKhzN_lJtneuijoUttm20cueLTHAZH3vc5Mg"
        );
        return headers;
    }

    @Order(1)
    @Test
    @DisplayName("Check Certificates API with PostgreSQL - first batch")
    void shouldReturnFirstBatchOfCertificates() {
        // Setup
        var certificate1 = new Certificate();
        certificate1.setCode("codeTest1");

        var certificate2 = new Certificate();
        certificate2.setCode("codeTest2");

        certificateRepository.save(certificate1);
        certificateRepository.save(certificate2);

        // Execute
        ResponseEntity<PaginatedResponse> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/api/main/certificate",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders()),
                PaginatedResponse.class
        );

        // Verify
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());

        var mapper = new ObjectMapper();
        List<Certificate> certificateList = mapper.convertValue(
                response.getBody().result(),
                new TypeReference<List<Certificate>>() {}
        );

        assertEquals(2, certificateList.size());
        assertEquals("codeTest1", certificateList.get(0).getCode());
        assertEquals("codeTest2", certificateList.get(1).getCode());
        assertEquals(2, response.getBody().total());
    }

    @Order(10)
    @Test
    @DisplayName("Check Certificates API with PostgreSQL - second batch")
    void shouldReturnAllCertificatesWithTestRestTemplate() {
        // Setup
        var certificate3 = new Certificate();
        certificate3.setCode("codeTest3");

        var certificate4 = new Certificate();
        certificate4.setCode("codeTest4");

        certificateRepository.save(certificate3);
        certificateRepository.save(certificate4);

        // Execute
        ResponseEntity<PaginatedResponse> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/api/main/certificate",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders()),
                PaginatedResponse.class
        );

        // Verify
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());

        var mapper = new ObjectMapper();
        List<Certificate> certificateList = mapper.convertValue(
                response.getBody().result(),
                new TypeReference<List<Certificate>>() {}
        );

        assertEquals(4, certificateList.size());
        assertEquals("codeTest3", certificateList.get(2).getCode());
        assertEquals("codeTest4", certificateList.get(3).getCode());
        assertEquals(4, response.getBody().total());
    }
}