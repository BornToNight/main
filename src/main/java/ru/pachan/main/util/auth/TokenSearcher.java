package ru.pachan.main.util.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pachan.main.exception.data.RequestException;
import ru.pachan.main.repository.auth.UserRepository;

import java.util.Base64;
import java.util.Objects;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static ru.pachan.main.util.enums.ExceptionEnum.*;

@RequiredArgsConstructor
@Component
public class TokenSearcher {

    public static String ADMIN = "admin";

    private final UserRepository userRepository;

    public boolean isAdmin(String token) throws RequestException {
        return !Objects.equals(userRepository.findById(Long.parseLong(getPayloadField(token, "userId"))).orElseThrow(() ->
                new RequestException(USER_IS_MISSING.getMessage(), UNAUTHORIZED)
        ).getRole().getName(), ADMIN);

    }

    public boolean isOriginalUser(String token, long userId) throws RequestException {
        return Long.parseLong(getPayloadField(token, "userId")) == userId;
    }

    public String getPayloadField(String token, String fieldName) throws RequestException {
        String payload = getPayload(token);
        try {
            return new ObjectMapper().readTree(payload).get(fieldName).asText();
        } catch (Throwable e) {
            throw new RequestException(EMPTY_TOKEN_FIELD.getMessage(), UNAUTHORIZED);
        }
    }

    public String getPayload(String token) throws RequestException {
        String payload;
        try {
            payload = new String(
                    Base64.getDecoder().decode(
                            token.split("\\.")[1]
                    )
            );
        } catch (IllegalArgumentException e) {
            throw new RequestException(EXPIRED_OR_INVALID_TOKEN.getMessage(), INTERNAL_SERVER_ERROR);
        }
        return payload;
    }

}
