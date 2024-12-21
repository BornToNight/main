package ru.pachan.main.exception.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@AllArgsConstructor
public class RequestException extends Exception {
    private final String message;
    private final HttpStatus httpStatus;
}
