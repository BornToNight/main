package ru.pachan.main.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import ru.pachan.main.exception.data.RequestException;
import ru.pachan.main.repository.auth.PermissionLevelRepository;
import ru.pachan.main.repository.auth.UserRepository;
import ru.pachan.main.util.auth.TokenSearcher;
import ru.pachan.main.util.enums.PermissionLevelEnum;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;
import static ru.pachan.main.util.auth.TokenSearcher.ADMIN;
import static ru.pachan.main.util.enums.ExceptionEnum.*;

@RequiredArgsConstructor
@Component
public class RequestProvider {

    @Value("${jwt.key}")
    private String secretKey;

    private final UserRepository userRepository;
    private final PermissionLevelRepository permissionLevelRepository;
    private final TokenSearcher tokenSearcher;

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    Boolean validateToken(String token) throws RequestException {
        if (token == null || token.isBlank()) {
            throw new RequestException(EXPIRED_OR_INVALID_TOKEN.getMessage(), UNAUTHORIZED);
        }
        try {
            JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            throw new RequestException(EXPIRED_OR_INVALID_TOKEN.getMessage(), UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            throw new RequestException(SYSTEM_ERROR.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    void checkPermission(String token, HttpServletRequest httpServletRequest) throws RequestException {
        if (token == null) {
            throw new RequestException(INVALID_TOKEN.getMessage(), FORBIDDEN);
        }
        try {
            List<String> path = Arrays.stream(httpServletRequest.getRequestURI().split("/")).filter(it -> !it.isBlank()).toList();
            if (Objects.equals(path.get(0), "graphql") || Objects.equals(path.get(2), "refresh")) {
                return;
            }
            Short permission = getPermission(token, path.get(2));
            if (
                    Objects.equals(httpServletRequest.getMethod(), HttpMethod.GET.name())
                    && permission < PermissionLevelEnum.PERMISSION_READ.getPermissionLevel()
            ) {
                throw new RequestException(PERMISSION_DENIED.getMessage(), FORBIDDEN);
            }
            if (
                    Objects.equals(httpServletRequest.getMethod(), HttpMethod.POST.name())
                    && permission < PermissionLevelEnum.PERMISSION_WRITE.getPermissionLevel()
            ) {
                throw new RequestException(PERMISSION_DENIED.getMessage(), FORBIDDEN);
            }
            if (
                    Objects.equals(httpServletRequest.getMethod(), HttpMethod.PUT.name())
                    && permission < PermissionLevelEnum.PERMISSION_UPDATE.getPermissionLevel()
            ) {
                throw new RequestException(PERMISSION_DENIED.getMessage(), FORBIDDEN);
            }
            if (
                    Objects.equals(httpServletRequest.getMethod(), HttpMethod.DELETE.name())
                    && permission < PermissionLevelEnum.PERMISSION_DELETE.getPermissionLevel()
            ) {
                throw new RequestException(PERMISSION_DENIED.getMessage(), FORBIDDEN);
            }
        } catch (NullPointerException e) {
            throw new RequestException(INVALID_TOKEN.getMessage(), FORBIDDEN);
        }
    }

    Short getPermission(String token, String uname) throws RequestException {
        String payload = tokenSearcher.getPayload(token);
        try {
            return permissionLevelRepository.findPermissionLevelByRoleIdAndPermissionUname(
                    new ObjectMapper().readTree(payload).get("roleId").longValue(),
                    uname
            );
        } catch (Throwable e) {
            throw new RequestException(SYSTEM_ERROR.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    void adminCheck(String token, HttpServletRequest httpServletRequest) throws RequestException {
        List<String> path = Arrays.stream(httpServletRequest.getRequestURI().split("/")).filter(it -> !it.isBlank()).toList();
        // < 2 - проверка для graphQL
        if (path.size() < 2 || !Objects.equals(path.get(1), "auth")) return;

        String payload = tokenSearcher.getPayload(token);

        long userId;

        try {
            userId = new ObjectMapper().readTree(payload).get("userId").asLong();
        } catch (NumberFormatException e) {
            throw new RequestException(EXPIRED_OR_INVALID_TOKEN.getMessage(), UNAUTHORIZED);
        } catch (Throwable e) {
            throw new RequestException(SYSTEM_ERROR.getMessage(), INTERNAL_SERVER_ERROR);
        }

        try {
            if (
                    Objects.equals(httpServletRequest.getMethod(), HttpMethod.GET.name())
                    || Objects.equals(httpServletRequest.getMethod(), HttpMethod.POST.name())
                    || Objects.equals(httpServletRequest.getMethod(), HttpMethod.PUT.name())
            ) {
                if (
                        path.size() == 4 &&
                        (Objects.equals(path.get(2), "user") || Objects.equals(path.get(2), "refresh")) ||
                        path.size() == 3 && Objects.equals(path.get(2), "refresh")
                ) return;
            }
        } catch (NullPointerException e) {
            throw new RequestException(INVALID_PATH.getMessage(), INTERNAL_SERVER_ERROR);
        }

        if (!Objects.equals(userRepository.findById(userId).orElseThrow(() ->
                new RequestException(USER_IS_MISSING.getMessage(), UNAUTHORIZED)
        ).getRole().getName(), ADMIN)
        ) throw new RequestException(PERMISSION_DENIED.getMessage(), FORBIDDEN);
    }

}
