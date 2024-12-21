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
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.pachan.main.dto.dictionary.PaginatedResponse;
import ru.pachan.main.model.main.Certificate;
import ru.pachan.main.repository.main.CertificateRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @ActiveProfiles("test")
class CertificatesIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private WebTestClient webTestClient;

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

    @Order(1)
    @Test
    @DisplayName("Check Certificates API with PostgreSQL with WebTestClient")
    void shouldReturnAllCertificatesWithWebTestClient() {
        var certificate1 = new Certificate();
        certificate1.setCode("codeTest1");

        var certificate2 = new Certificate();
        certificate2.setCode("codeTest2");

        this.certificateRepository.save(certificate1);
        this.certificateRepository.save(certificate2);

        this.webTestClient.get()
                .uri("api/main/certificate")
                .headers(header -> header.setBearerAuth(
                        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInJvbGVJZCI6MSwiaWF0IjoxNzIxMTI0" +
                                "ODE1LCJleHAiOjE3MjExMjUxMTU5fQ.Vra7txDcKhzN_lJtneuijoUttm20cueLTHAZH3vc5Mg")
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("result.length()").isEqualTo(2)
                .jsonPath("total").isEqualTo(2)
                .jsonPath("result[0].id").isEqualTo(1)
                .jsonPath("result[0].code").isEqualTo("codeTest1")
                .jsonPath("result[1].id").isEqualTo(2)
                .jsonPath("result[1].code").isEqualTo("codeTest2");
    }

    @Order(10)
    @Test
    @DisplayName("Check Certificates API with PostgreSQL with TestRestTemplate")
    void shouldReturnAllCertificatesWithTestRestTemplate() {
        var certificate3 = new Certificate();
        certificate3.setCode("codeTest3");

        var certificate4 = new Certificate();
        certificate4.setCode("codeTest4");

        this.certificateRepository.save(certificate3);
        this.certificateRepository.save(certificate4);

        HttpHeaders headers = new HttpHeaders();
        headers.set(
                "Authorization",
                "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInJvbGVJZCI6MSwiaWF0IjoxNzIxMTI0ODE1LCJleH" +
                        "AiOjE3MjExMjUxMTU5fQ.Vra7txDcKhzN_lJtneuijoUttm20cueLTHAZH3vc5Mg"
        );

        ResponseEntity<PaginatedResponse> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/api/main/certificate",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                PaginatedResponse.class
        );

        var mapper = new ObjectMapper();
        List<Certificate> certificateList = new ArrayList<>();
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());

        try {
            certificateList = mapper.convertValue(response.getBody().result(), new TypeReference<List<Certificate>>() {
            });
        } catch (IllegalArgumentException e) {
            System.err.println("not deserializable object");
        }

        assertEquals(4, certificateList.size());
        assertEquals("codeTest3", certificateList.get(2).getCode());
        assertEquals("codeTest4", certificateList.get(3).getCode());
    }

}