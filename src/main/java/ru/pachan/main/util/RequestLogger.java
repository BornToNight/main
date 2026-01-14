package ru.pachan.main.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import ru.pachan.main.security.RequestProvider;

import java.io.IOException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static ru.pachan.main.util.enums.MdcKeyEnum.USER_ID;

@Slf4j
public class RequestLogger {

    private static final int MAX_BODY_LENGTH_KB = 10 * 1024;

    public static void writeSlf4jLog(
            ContentCachingRequestWrapper requestWrapper,
            ContentCachingResponseWrapper responseWrapper,
            RequestProvider requestProvider,
            String exceptionMessage
    ) throws IOException {

        var url = requestWrapper.getRequestURI();
        if (isActuatorOrSwagger(url)) {
            responseWrapper.copyBodyToResponse(); // EXPLAIN_V вернуть бади респонса
            return;
        }

        MDC.put(USER_ID.getKey(), getUserId(requestWrapper, requestProvider));

        var method = requestWrapper.getMethod();
        var message = new StringBuilder();

        message.append("Url - ").append(url);
        message.append(" | ");
        message.append("Status - ").append(responseWrapper.getStatus());
        message.append(" | ");
        message.append("Method - ").append(method);
        message.append(" | ");
        message.append("Args - ").append(requestWrapper.getQueryString());
        message.append(" | ");

        if (isPostMethod(method)) {
            String requestBody = extractPostRequestBody(requestWrapper).trim();
            if (!requestBody.isBlank()) {
                message.append("Request body - ").append(requestBody);
                message.append(" | ");
            }
        }
        message.append("Response body - ").append(extractResponseBody(responseWrapper).trim());

        message.append(!exceptionMessage.isBlank() ? " | " + exceptionMessage : "");

        if (responseWrapper.getStatus() == HttpStatus.OK.value()) {
            log.info(message.toString());
        } else {
            log.error(message.toString());
        }

        responseWrapper.copyBodyToResponse();

    }

    private static String getUserId(ContentCachingRequestWrapper httpServletRequest, RequestProvider requestProvider) {
        try {
            String payload = new String(Base64.getDecoder().decode(requestProvider.resolveToken(httpServletRequest).split("\\.")[1]));
            return new ObjectMapper().readTree(payload).get("userId").asText();
        } catch (Exception e) {
            return "";
        }
    }

    private static boolean isActuatorOrSwagger(String url) {
        return url.contains("/actuator") || url.contains("swagger") || url.contains("/v3/api-docs");
    }

    private static String extractPostRequestBody(ContentCachingRequestWrapper request) {
        return extractBody(request.getContentAsByteArray());
    }

    private static String extractResponseBody(ContentCachingResponseWrapper response) {
        return extractBody(response.getContentAsByteArray());
    }

    private static String extractBody(byte[] contentAsByteArray) {
        if (contentAsByteArray.length == 0) return "";
        String body = new String(contentAsByteArray, UTF_8);
        return body.isEmpty() ? "" : truncate(body);
    }

    private static boolean isPostMethod(String method) {
        return "POST".equalsIgnoreCase(method);
    }

    private static String truncate(String body) {
        if (MAX_BODY_LENGTH_KB > body.length()) return body;
        return body.substring(0, MAX_BODY_LENGTH_KB) + "... [truncated]";
    }

}
