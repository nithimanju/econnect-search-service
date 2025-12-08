package com.e_connect.search.search.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SortDirection {
    ASC("asc"),
    DESC("desc");

    private String value;

    public SortDirection getSortDirection(String value) {
        return SortDirection.valueOf(value);
    }

    public String getValue() {
        return value;
    }
}
