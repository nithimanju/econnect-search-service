package com.e_connect.search.search.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.apache.commons.lang3.ObjectUtils;

import com.e_connect.search.search.dto.ItemAutoCompleteRequest;
import com.e_connect.search.search.dto.ItemAutoCompleteReturnResponse;
import com.e_connect.search.search.dto.ItemSearchRequest;
import com.e_connect.search.search.dto.ItemSearchReturnResponse;
import com.e_connect.search.search.service.ItemSearchCacheService;
import com.e_connect.search.search.service.ItemSearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/item")
public class ItemSearchController {

  private final ItemSearchService itemSearchService;
  private final ItemSearchCacheService itemSearchCacheService;

  @PostMapping(value = "/v1/autocomplete-search", consumes="application/json", produces = "application/json")
  public ResponseEntity<ItemAutoCompleteReturnResponse> autoCompleteResult(@RequestBody ItemAutoCompleteRequest itemAutoCompleteRequest) {
    ItemAutoCompleteReturnResponse itemAutoCompleteResponse = itemSearchService.getAutoCompleteResults(itemAutoCompleteRequest);
    if(ObjectUtils.isNotEmpty(itemAutoCompleteResponse.getErrors())){
      return new ResponseEntity<>(itemAutoCompleteResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    } else if (itemAutoCompleteResponse.getCount() == 0) {
      return new ResponseEntity<>(itemAutoCompleteResponse, HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(itemAutoCompleteResponse, HttpStatus.OK);
  }

  @PostMapping(value = "/v2/autocomplete-search", consumes="application/json", produces = "application/json")
  public ResponseEntity<ItemAutoCompleteReturnResponse> autoCompleteCacheResult(@RequestBody ItemAutoCompleteRequest itemAutoCompleteRequest) {
    ItemAutoCompleteReturnResponse itemAutoCompleteResponse = itemSearchCacheService.getAutoCompleteResults(itemAutoCompleteRequest);
    if(ObjectUtils.isNotEmpty(itemAutoCompleteResponse.getErrors())){
      return new ResponseEntity<>(itemAutoCompleteResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    } else if (itemAutoCompleteResponse.getCount() == 0) {
      return new ResponseEntity<>(itemAutoCompleteResponse, HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(itemAutoCompleteResponse, HttpStatus.OK);
  }

  @PostMapping(value = "/v1/list", consumes="application/json", produces = "application/json")
  public ResponseEntity<ItemSearchReturnResponse> list(@RequestBody ItemSearchRequest itemSearchRequest) {
    ItemSearchReturnResponse itemAutoCompleteResponse = itemSearchService.getListResponse(itemSearchRequest);
    if(ObjectUtils.isNotEmpty(itemAutoCompleteResponse.getErrors())){
      return new ResponseEntity<>(itemAutoCompleteResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    } else if (itemAutoCompleteResponse.getCount() == 0) {
      return new ResponseEntity<>(itemAutoCompleteResponse, HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(itemAutoCompleteResponse, HttpStatus.OK);
  }
}
