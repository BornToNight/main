package ru.pachan.main.util.sql;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderDirection {

    @JsonProperty("DESC")
    DESC,
    @JsonProperty("ASC")
    ASC

}
