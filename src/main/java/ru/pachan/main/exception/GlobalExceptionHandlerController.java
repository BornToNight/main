package ru.pachan.main.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ru.pachan.main.exception.data.RequestException;
import ru.pachan.main.exception.data.RequestSystemException;

import static org.springframework.http.HttpStatus.*;
import static ru.pachan.main.util.enums.ExceptionEnum.*;

@RestControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(RequestSystemException.class)
    ResponseEntity<String> handleRequestSystemException(HttpServletResponse res, RequestSystemException e) {
        return new ResponseEntity<>(e.getMessage(), e.getHttpStatus());
    }

    @ExceptionHandler(RequestException.class)
    ResponseEntity<String> handleRequestException(HttpServletResponse res, RequestException e) {
        return new ResponseEntity<>(e.getMessage(), e.getHttpStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<String> handleSQLUniqueFieldException(DataIntegrityViolationException e) {
        String field = "";
        if (e.getMessage() != null && !e.getMessage().isBlank()) {
            if (e.getMessage().contains("not present in table") || e.getMessage().contains("отсутствует в таблице")) {
                return new ResponseEntity<>(NOT_FOUND_REFERENCE.getMessage(), GONE);
            } else if (e.getMessage().contains("overflow")) {
                return new ResponseEntity<>(INVALID_DATA_FORMAT.getMessage(), BAD_REQUEST);
            } else {
                int startIndex = e.getMessage().indexOf("=") + 2;
                int endIndex = e.getMessage().indexOf(")", startIndex);
                field = e.getMessage().substring(startIndex, endIndex);
            }
        }
        return new ResponseEntity<>(DUPLICATE_UNIQUE_FIELD.getMessage() + " - " + field, CONFLICT);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<String> requiredFieldsEmptyHandler(HttpMessageNotReadableException e) {
        if (e.getMessage() != null && !e.getMessage().isBlank()) {
            if (e.getMessage().contains("deserialize")) {
                return new ResponseEntity<>(INVALID_DATA_FORMAT.getMessage(), BAD_REQUEST);
            } else if (e.getMessage().contains("non-nullable")) {
                return new ResponseEntity<>(REQUIRED_FIELDS_EMPTY.getMessage(), BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("", BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<String[]> validEntityException(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(
                e.getBindingResult().getFieldErrors().stream().map(it -> it.getField() + ": " + it.getDefaultMessage()).toArray(String[]::new),
                BAD_REQUEST
        );
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<String> validQueryParametersException(HandlerMethodValidationException e) {
        return new ResponseEntity<>(NOT_VALID_QUERY_PARAMETERS.getMessage(), BAD_REQUEST);
    }

}
