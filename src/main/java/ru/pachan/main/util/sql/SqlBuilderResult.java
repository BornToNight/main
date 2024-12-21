package ru.pachan.main.util.sql;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SqlBuilderResult<T> {
    private List<T> data;
    private Long amount;
}
