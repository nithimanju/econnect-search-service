package com.e_connect.search.search.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder(toBuilder = true)
public class ItemSearchReturnResponse extends BaseResponse{
    private List<ItemSearchResponse> itemSearchResponses;
    private List<ItemSearchFilterResponse> itemSearchFilterResponses;
}
