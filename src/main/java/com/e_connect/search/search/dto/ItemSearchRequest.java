package com.e_connect.search.search.dto;

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
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class ItemSearchRequest extends ItemAutoCompleteRequest {

    private int from;
    private int size;
    private String sortField;
    private SortDirection sortDirection;
}
