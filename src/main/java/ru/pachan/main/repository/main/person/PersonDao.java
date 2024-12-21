package ru.pachan.main.repository.main.person;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pachan.main.dto.dictionary.PaginatedResponse;
import ru.pachan.main.model.main.PersonQueryBuilder;
import ru.pachan.main.util.sql.OrderDirection;
import ru.pachan.main.util.sql.SqlBuilderResult;
import ru.pachan.main.util.sql.SqlQueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PersonDao {

    private static String BASE_QUERY =
            "SELECT {} FROM persons WHERE true";
    private static String FIRST_NAME_QUERY =
            " AND (LOWER(first_Name) LIKE CONCAT('%', LOWER(:firstName), '%'))";

    private static String FIRST_NAMES_QUERY =
            " AND (first_Name IN (:firstNames))";

    private static final RowMapper<PersonQueryBuilder> ROW_MAPPER = (rs, rowNum) ->
            new PersonQueryBuilder()
                    .setId(rs.getInt("person_id"))
                    .setFirstName(rs.getString("first_Name"))
                    .setSurname(rs.getString("surname"));

    private final SqlQueryBuilder<PersonQueryBuilder> sqlQueryBuilder;

    public PersonDao(NamedParameterJdbcTemplate jdbcTemplate) {
        sqlQueryBuilder = new SqlQueryBuilder<>(BASE_QUERY, Map.of(
                "firstName", FIRST_NAME_QUERY,
                "firstNames", FIRST_NAMES_QUERY
        ), ROW_MAPPER, jdbcTemplate);
    }

    public PaginatedResponse<PersonQueryBuilder> getPersons(String firstName, List<String> firstNames) {
        SqlBuilderResult<PersonQueryBuilder> result = sqlQueryBuilder.execute(
                makeParams(firstName, firstNames),
                1L,
                "personId",
                OrderDirection.DESC,
                true
        );
        return new PaginatedResponse<>(result.getAmount(), result.getData());
    }

    Map<String, Object> makeParams(String firstName, List<String> firstNames) {
        Map<String, Object> params = new HashMap<>();
        params.put("firstName", firstName);
        params.put("firstNames", firstNames);
        return params;
    }

}
