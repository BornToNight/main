package ru.pachan.main.exception.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

// EXPLAIN_V Нужен, т.к. CircuitBreaker и Retry игнорируют RequestException
@Setter
@Getter
@AllArgsConstructor
public class RequestSystemException extends Exception {
    private final String message;
    private final HttpStatus httpStatus;
}
