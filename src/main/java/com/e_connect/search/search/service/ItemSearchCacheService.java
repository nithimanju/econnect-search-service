package com.e_connect.search.search.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.e_connect.search.search.dto.ItemAutoCompleteCacheResponse;
import com.e_connect.search.search.dto.ItemAutoCompleteRequest;
import com.e_connect.search.search.dto.ItemAutoCompleteResponse;
import com.e_connect.search.search.dto.ItemAutoCompleteReturnResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class ItemSearchCacheService {
  private final ItemSearchService itemSearchService;
  private final RedisTemplate<String, Object> redisTemplate;
  private final RedisCacheManager redisCacheManager;

  @Cacheable(value = "RecentRequestCache")
  public ItemAutoCompleteReturnResponse getAutoCompleteResults(ItemAutoCompleteRequest itemAutoCompleteRequest) {
    ItemAutoCompleteCacheResponse itemAutoCompleteCacheResponse = getItemAutoCompleteCacheResponse(
        itemAutoCompleteRequest.getQueryString());
    if (ObjectUtils.isEmpty(itemAutoCompleteCacheResponse)) {
      ItemAutoCompleteReturnResponse itemAutoCompleteReturnResponse = itemSearchService
          .getAutoCompleteResults(itemAutoCompleteRequest);
      //writeItemAutoCompleteCacheResponse(itemAutoCompleteRequest.getQueryString(),
      //    itemAutoCompleteReturnResponse.getItemAutoCompleteResponses());
      return itemAutoCompleteReturnResponse;
    }

    return ItemAutoCompleteReturnResponse.builder()
        .count(itemAutoCompleteCacheResponse.getItemAutoCompleteResponses().size())
        .itemAutoCompleteResponses(itemAutoCompleteCacheResponse.getItemAutoCompleteResponses()).build();
  }

  private ItemAutoCompleteCacheResponse getItemAutoCompleteCacheResponse(String prefix) {
    return (ItemAutoCompleteCacheResponse) redisTemplate.opsForValue().get(prefix);
  }

  private void writeItemAutoCompleteCacheResponse(String prefix,
      List<ItemAutoCompleteResponse> itemAutoCompleteResponses) {
    redisTemplate.opsForValue().set(prefix,
        ItemAutoCompleteCacheResponse.builder()
            .itemAutoCompleteCacheResponseHashKey(prefix).itemAutoCompleteResponses(itemAutoCompleteResponses).build(),
        10, TimeUnit.MINUTES);
  }
}
