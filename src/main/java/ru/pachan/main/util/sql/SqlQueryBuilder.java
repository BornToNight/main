package ru.pachan.main.util.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class SqlQueryBuilder<T> {

    private final String baseQuery;
    private final Map<String, String> conditions;
    private final RowMapper<T> rowMapper;
    private final NamedParameterJdbcTemplate jdbcTemplate;


    public SqlBuilderResult<T> execute(
            Map<String, Object> parameters,
            Long limit,
            String order,
            OrderDirection orderDirection,
            boolean fetchData
    ) {
        SqlBuilderResult<T> sqlBuilderResult = new SqlBuilderResult<>();
        String queryTemplate = makeQueryTemplate(parameters);
        if (fetchData) {
            List<T> result = jdbcTemplate.query(
                    makeResultQuery(queryTemplate, limit, order, orderDirection), parameters, rowMapper
            );
            sqlBuilderResult.setData(result);
        }
        Long total = jdbcTemplate.queryForObject(makeCountQuery(queryTemplate), parameters, Long.class);
        sqlBuilderResult.setAmount(total);

        return sqlBuilderResult;
    }

    public String makeQueryTemplate(Map<String, Object> parameters) {
        var queryBuilder = new StringBuilder(baseQuery);
        for (String key : parameters.keySet()) {
            Object parameter = parameters.get(key);
            String condition = conditions.get(key);
            if (condition != null && parameter != null) {
                queryBuilder.append(condition);
            }
        }
        return queryBuilder.toString();
    }

    private String makeResultQuery(String queryTemplate, Long limit, String order, OrderDirection orderDirection) {
        return queryTemplate.replace("{}", "*")
               + makeOrderByQuery(order, orderDirection)
               + makeLimitQuery(limit);
    }

    private String makeCountQuery(String queryTemplate) {
        return queryTemplate.replace("{}", "count(1)");
    }

    private String makeLimitQuery(Long limit) {
        return " LIMIT " + limit;
    }

    private String makeOrderByQuery(String order, OrderDirection orderDirection) {
        if (order != null && orderDirection != null) {
            return " order by " + camelToSnake(order) + " " + orderDirection;
        }
        return "";
    }

    private String camelToSnake(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        char c = str.charAt(0);
        result.append(Character.toLowerCase(c));
        for (int i = 1; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }
}
