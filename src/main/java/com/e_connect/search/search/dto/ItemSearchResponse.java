package com.e_connect.search.search.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ItemSearchResponse extends ItemAutoCompleteResponse{
    private Float price;
    private Float discountPercentage;
    private String currencyCode;
    private Float rating;
    private Float totalQauntity;
    private String availabilityMessage;
    private List<String> itemDescriptions; 
}
