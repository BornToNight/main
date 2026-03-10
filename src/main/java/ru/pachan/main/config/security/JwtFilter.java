package ru.pachan.main.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import ru.pachan.main.exception.data.RequestException;
import ru.pachan.main.util.RequestLogger;
import ru.pachan.main.util.enums.AuthorityEnum;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import static ru.pachan.main.util.enums.MdcKeyEnum.*;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final RequestProvider requestProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String requestUid = request.getHeader(REQUEST_UID.getKey());
        if (requestUid == null) {
            requestUid = UUID.randomUUID().toString();
        }

        var url = request.getRequestURI();

        MDC.put(REQUEST_UID.getKey(), requestUid);
        MDC.put(REQUEST_URL.getKey(), url);

        try {
            try {
                String token = requestProvider.resolveToken(request);
                if (requestProvider.validateToken(token)) {
                    requestProvider.checkAdmin(token, request);
                    requestProvider.checkPermission(token, request);
                    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                            null, null, Collections.singletonList(
                            new SimpleGrantedAuthority(AuthorityEnum.VERIFIED_TOKEN.getAuthority())
                    )));
                }
            } catch (RequestException e) {
                SecurityContextHolder.clearContext();
                response.sendError(e.getHttpStatus().value(), e.getMessage());
                RequestLogger.writeSlf4jLog(requestWrapper, responseWrapper, requestProvider, e.getMessage());
                return;
            }
            filterChain.doFilter(requestWrapper, responseWrapper);
            RequestLogger.writeSlf4jLog(requestWrapper, responseWrapper, requestProvider, "");
        } finally {
            MDC.remove(REQUEST_UID.getKey());
            MDC.remove(REQUEST_URL.getKey());
            MDC.remove(USER_ID.getKey());
        }

    }

}
