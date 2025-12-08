package com.e_connect.search.opensearch.service;

import java.io.IOException;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class OpenSearchSearchService<T> {
  private final OpenSearchClient openSearchClient;

  public SearchResponse<T> getQueryResults(SearchRequest request, Class<T> documentType){
    SearchResponse<T> searchResponse = null;
    try {
      searchResponse = openSearchClient.search(request, documentType);
    } catch (OpenSearchException | IOException e) {
      log.error("Error while fetching the get Query Results results:", e);
    }
    return searchResponse;
  } 
}
