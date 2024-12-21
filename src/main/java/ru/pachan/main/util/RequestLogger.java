package ru.pachan.main.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import ru.pachan.main.security.RequestProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RequestLogger {

    public static void writeSlf4jLog(
            ContentCachingRequestWrapper requestWrapper,
            ContentCachingResponseWrapper responseWrapper,
            RequestProvider requestProvider,
            String exceptionMessage
    ) {
        if (requestWrapper.getRequestURI().contains("actuator")) {
            try {
                responseWrapper.copyBodyToResponse(); // EXPLAIN_V вернуть бади респонса
            } catch (IOException ignored) {
            }
            return;
        }
        var message = new StringBuilder();
        message.append("\n");
        message.append(requestWrapper.getMethod()).append(" ").append(responseWrapper.getStatus());
        message.append("\n");
        if (responseWrapper.getStatus() != HttpStatus.OK.value() && !extractResponseBody(responseWrapper).isBlank()) {
            message.append(extractResponseBody(responseWrapper)).append("\n");
        }
        message.append(!exceptionMessage.isBlank() ? exceptionMessage + "\n" : "");
        message.append(requestWrapper.getRequestURI());
        message.append("\n");
        message.append("userId = ").append(getUserId(requestWrapper, requestProvider));
        message.append("\n");
        message.append(extractPostRequestBody(requestWrapper));

        Logger logger = LoggerFactory.getLogger(RequestLogger.class);
        if (responseWrapper.getStatus() == HttpStatus.OK.value()) {
            logger.info(message.toString());
        } else {
            logger.warn(message.toString());
        }

        try {
            responseWrapper.copyBodyToResponse(); // EXPLAIN_V вернуть бади респонса
        } catch (IOException ignored) {
        }
    }

    private static String extractPostRequestBody(ContentCachingRequestWrapper httpServletRequest) {
        if ("POST".equalsIgnoreCase(httpServletRequest.getMethod())) {
            Scanner s = new Scanner(new ByteArrayInputStream(httpServletRequest.getContentAsByteArray()), UTF_8)
                    .useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
        return "";
    }

    private static String extractResponseBody(ContentCachingResponseWrapper httpServletResponse) {
        Scanner s = new Scanner(new ByteArrayInputStream(httpServletResponse.getContentAsByteArray()), UTF_8)
                .useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private static String getUserId(ContentCachingRequestWrapper httpServletRequest, RequestProvider requestProvider) {
        try {
            String payload = new String(Base64.getDecoder().decode(requestProvider.resolveToken(httpServletRequest).split("\\.")[1]));
            return new ObjectMapper().readTree(payload).get("userId").asText();
        } catch (Exception e) {
            return "";
        }
    }
}
