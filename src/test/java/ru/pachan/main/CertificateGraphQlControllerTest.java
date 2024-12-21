package ru.pachan.main;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.pachan.main.model.main.Certificate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Testcontainers
@AutoConfigureGraphQlTester
public class CertificateGraphQlControllerTest {

    @Autowired
    private GraphQlTester tester;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16")
            .withUsername(System.getenv("POSTGRES_USER"))
            .withPassword("password");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test()
    @DisplayName("Add certificate with GraphQL")
    void addCertificate() {
        var query = """
                mutation {
                  newCertificate(certificateGraphQlDto: { code: "codeTestGraphQl" }) {
                    id
                    code
                  }
                }""";
        Certificate certificate = tester.document(query)
                .execute()
                .path("data.newCertificate")
                .entity(Certificate.class)
                .get();
        assertNotNull(certificate);
        assertEquals("codeTestGraphQl", certificate.getCode());
    }

    @Test
    @DisplayName("Get all certificates with GraphQL")
    void findAll() {
        var query = """
                {
                  certificates {
                    id
                    code
                    person {
                      id
                      firstName
                    }
                  }
                }""";
        List<Certificate> certificates = tester.document(query)
                .execute()
                .path("data.certificates[*]")
                .entityList(Certificate.class)
                .get();
        assertFalse(certificates.isEmpty());
        assertEquals(1, certificates.size());
        assertEquals("codeTestGraphQl", certificates.getFirst().getCode());
        assertNull(certificates.getFirst().getPerson());
    }

    @Test
    @DisplayName("Get certificate by Id with GraphQL")
    void findById() {
        var query = """
                  {
                  certificate(id: 1) {
                    id,
                    code
                  }
                }""";
        Certificate certificate = tester.document(query)
                .execute()
                .path("data.certificate")
                .entity(Certificate.class)
                .get();
        assertNotNull(certificate);
        assertEquals("codeTestGraphQl", certificate.getCode());
    }

}