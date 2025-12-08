package com.e_connect.search.opensearch.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ItemAttributeOS {

    private String name;
    private String value;
    private String partAttributeId;
}
