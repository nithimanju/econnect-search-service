package com.e_connect.search.search.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class ItemSearchFilterResponse {
    private String attributeName;
    private String attributeId;
    private Long attributeTotalCount;
    private Map<String, Long> attributeValues; 
}
