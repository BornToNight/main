package ru.pachan.main.dto.dictionary;

import java.io.Serializable;
import java.util.List;

public record PaginatedResponse<T>(
        long total,
        List<T> result
) implements Serializable {
}
