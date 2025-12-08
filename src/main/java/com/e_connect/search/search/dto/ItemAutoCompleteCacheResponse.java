package com.e_connect.search.search.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class ItemAutoCompleteCacheResponse {
    private String itemAutoCompleteCacheResponseHashKey;
    private Character presentChar;
    private List<ItemAutoCompleteResponse> itemAutoCompleteResponses = new ArrayList<>();
    private Map<Character, String> childItemAutoCompleteCacheResponseHashKeyMap = new HashMap<>();
}
