package com.e_connect.search.search.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ItemAutoCompleteResponse {

    private String itemNumber;
    private String itemTitle;
    private String itemId;
    private Map<String, String> categories;
    private Map<String, String> brands;
    private Long popularity;
    private List<String> mediaPaths;
    private Long searchCounts;
}
