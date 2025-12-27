package com.e_connect.search.search.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@SuperBuilder(toBuilder = true)
public class ItemAutoCompleteRequest {
    private String queryString;
    private List<String> notRequiredResults;
    private Map<String, List<String>> filterValues;
    private String languageCode;
    private List<String> categoryIds;
    private List<String> brandIds;
}
