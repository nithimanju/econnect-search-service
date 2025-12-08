package com.e_connect.search.search.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.NestedQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Service;

import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.aggregations.Aggregate;
import org.opensearch.client.opensearch._types.aggregations.Aggregation;
import org.opensearch.client.opensearch._types.aggregations.StringTermsBucket;

import com.e_connect.search.opensearch.item.ItemDocument;
import com.e_connect.search.opensearch.service.OpenSearchSearchService;
import com.e_connect.search.search.dto.ItemAutoCompleteRequest;
import com.e_connect.search.search.dto.ItemAutoCompleteResponse;
import com.e_connect.search.search.dto.ItemAutoCompleteReturnResponse;
import com.e_connect.search.search.dto.ItemSearchFilterResponse;
import com.e_connect.search.search.dto.ItemSearchRequest;
import com.e_connect.search.search.dto.ItemSearchResponse;
import com.e_connect.search.search.dto.ItemSearchReturnResponse;

import co.elastic.clients.elasticsearch.ml.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class ItemSearchService {
  private static final String PART_INDEX = "item-v1";
  private final OpenSearchSearchService<ItemDocument> openSearchService;

  public ItemAutoCompleteReturnResponse getAutoCompleteResults(ItemAutoCompleteRequest itemAutoCompleteRequest) {

    BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

    String queryString = itemAutoCompleteRequest.getQueryString();
    String languageCode = itemAutoCompleteRequest.getLanguageCode();

    Map<String, Float> nestedBoastFieldMap = new HashMap<>();
    nestedBoastFieldMap.put("itemTitles.title.autocomplete", 1F);
    nestedBoastFieldMap.put("itemDescriptions.description", 1F);

    Map<String, Float> boastFieldMap = new HashMap<>();
    boastFieldMap.put("suggest", 1F);

    boolQueryBuilder
        .should(getNestPhaseMatchQueries(queryString, nestedBoastFieldMap, languageCode));
    boolQueryBuilder.should(getMatchPhasePrefix(queryString, nestedBoastFieldMap));
    boolQueryBuilder.should(getBoolPhasePrefix(queryString, nestedBoastFieldMap));

    SearchRequest request = new SearchRequest.Builder()
        .index(PART_INDEX)
        .query(boolQueryBuilder.build()._toQuery())
        .from(0)
        .size(20)
        .build();

    SearchResponse<ItemDocument> searchResponse = openSearchService.getQueryResults(request, ItemDocument.class);

    List<ItemAutoCompleteResponse> itemAutoCompleteResponses = buildItemAutoCompleteResponse(searchResponse,
        languageCode);

    return ItemAutoCompleteReturnResponse.builder().count(itemAutoCompleteResponses.size())
        .itemAutoCompleteResponses(itemAutoCompleteResponses).build();
  }

  private Map<String, Aggregation> getAggregationQuery() {
    Aggregation attributeValueAgg = Aggregation.of(valueAgg -> valueAgg.terms(
        trm -> trm.field("itemAttributes.attributeValue")));
    Aggregation attributeNameAgg = Aggregation.of(a -> a
        .terms(t -> t
            .field("itemAttributes.attributeName"))
        .aggregations("attribute_value_buckets", attributeValueAgg));
    Aggregation attributeNestedAgg = Aggregation.of(a -> a
        .nested(nested -> nested
            .path("itemAttributes"))
        .aggregations("attribute_name_buckets", attributeNameAgg));
    return Map.of("nested_attributes", attributeNestedAgg);
  }

  public ItemSearchReturnResponse getListResponse(ItemSearchRequest itemSearchRequest) {

    BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

    String queryString = itemSearchRequest.getQueryString();
    String languageCode = itemSearchRequest.getLanguageCode();
    String sortField = itemSearchRequest.getSortField();
    Integer from = itemSearchRequest.getFrom();
    Integer size = itemSearchRequest.getSize();
    String sortDirection = ObjectUtils.isNotEmpty(itemSearchRequest.getSortDirection())
        ? itemSearchRequest.getSortDirection().getValue()
        : null;
    Map<String, List<String>> filters = itemSearchRequest.getFilterValues();

    Map<String, Float> nestedBoastFieldMap = new HashMap<>();
    nestedBoastFieldMap.put("itemTitles.title.autocomplete", 1F);
    nestedBoastFieldMap.put("itemDescriptions.description", 1F);

    Map<String, Float> boastFieldMap = new HashMap<>();
    boastFieldMap.put("suggest", 1F);

    boolQueryBuilder
        .should(getNestPhaseMatchQueries(queryString, nestedBoastFieldMap, languageCode));
    boolQueryBuilder.should(getMatchPhasePrefix(queryString, nestedBoastFieldMap));
    boolQueryBuilder.should(getBoolPhasePrefix(queryString, nestedBoastFieldMap));
    if (ObjectUtils.isNotEmpty(filters)) {
      boolQueryBuilder.filter(getFilterQuery(filters));
    }

    SearchRequest.Builder requestBuilder = new SearchRequest.Builder()
        .index(PART_INDEX)
        .query(boolQueryBuilder.build()._toQuery())
        .aggregations(getAggregationQuery())
        .from(from)
        .size(size);

    if (StringUtils.isNotBlank(sortField) && ObjectUtils.isNotEmpty(sortDirection)) {
      requestBuilder.sort(SortOptions.of(
          sort -> sort.field(fieldSort -> fieldSort.field(sortField).order(SortOrder.valueOf(sortDirection)))));
    }

    SearchResponse<ItemDocument> searchResponse = openSearchService.getQueryResults(requestBuilder.build(),
        ItemDocument.class);
    Long totalCount = searchResponse.hits().total().value();
    List<ItemSearchFilterResponse> aggregations = buildAggregation(searchResponse);
    List<ItemSearchResponse> itemAutoCompleteResponses = buildListResponse(searchResponse,
        languageCode);

    return ItemSearchReturnResponse.builder().count(totalCount.intValue())
        .itemSearchResponses(itemAutoCompleteResponses).itemSearchFilterResponses(aggregations).build();
  }

  private List<Query> getMatchPhasePrefix(String value, Map<String, Float> boostFieldMap) {
    List<Query> querys = new ArrayList<>();
    boostFieldMap.entrySet().stream().forEach(entrySet -> {
      querys.add(Query.of(q -> q
          .matchPhrasePrefix(m -> m
              .field(entrySet.getKey())
              .query(value)
              .boost(entrySet.getValue()))));
    });
    return querys;
  }

  private List<Query> getBoolPhasePrefix(String value, Map<String, Float> boostFieldMap) {
    List<Query> querys = new ArrayList<>();
    boostFieldMap.entrySet().stream().forEach(entrySet -> {
      querys.add(Query.of(q -> q
          .matchBoolPrefix(m -> m
              .field(entrySet.getKey())
              .query(value)
              .boost(entrySet.getValue()))));
    });
    return querys;
  }

  private List<Query> getNestBoolMatchQueries(String value, Map<String, Float> boostFieldMap, String languageCode) {
    List<Query> querys = new ArrayList<>();
    boostFieldMap.entrySet().stream().forEach(entrySet -> {
      String path = entrySet.getKey().split("\\.")[0];
      querys.add(NestedQuery.of(fn -> fn.path(path)
          .query(q -> q.bool(bool -> bool.must(
              getBoolPhasePrefix(value, Map.of(entrySet.getKey(), entrySet.getValue())))
              .filter(fl -> fl.term(tr -> tr.field(path.concat(".languageCode")).value(FieldValue.of(languageCode)))))))
          ._toQuery());
    });
    return querys;
  }

  private List<Query> getNestPhaseMatchQueries(String value, Map<String, Float> boostFieldMap, String languageCode) {
    List<Query> querys = new ArrayList<>();
    boostFieldMap.entrySet().stream().forEach(entrySet -> {
      String path = entrySet.getKey().split("\\.")[0];
      querys.add(NestedQuery.of(fn -> fn.path(path)
          .query(q -> q.bool(bool -> bool.must(
              getMatchPhasePrefix(value, Map.of(entrySet.getKey(), entrySet.getValue())))
              .filter(fl -> fl.term(tr -> tr.field(path.concat(".languageCode")).value(FieldValue.of(languageCode)))))))
          ._toQuery());
    });
    return querys;
  }

  private List<ItemAutoCompleteResponse> buildItemAutoCompleteResponse(SearchResponse<ItemDocument> searchResponse,
      String languageCode) {
    List<ItemAutoCompleteResponse> itemAutoCompleteResponses = new ArrayList<>();
    searchResponse.hits().hits()
        .forEach(hit -> itemAutoCompleteResponses.add(convertItemAutoCompleteResponse(hit.source(), languageCode)));
    return itemAutoCompleteResponses;
  }

  private ItemAutoCompleteResponse convertItemAutoCompleteResponse(ItemDocument itemDocument, String languageCode) {
    return ItemAutoCompleteResponse.builder()
        .itemId(itemDocument.getItemId())
        .itemNumber(itemDocument.getItemNumber())
        .itemTitle(
            itemDocument.getItemTitles().stream()
                .filter(title -> languageCode.equals(title.getLanguageCode()))
                .findFirst().orElse(itemDocument.getItemTitles().getFirst()).getTitle())
        .popularity(itemDocument.getPopularity())
        .build();
  }

  private List<ItemSearchResponse> buildListResponse(SearchResponse<ItemDocument> searchResponse,
      String languageCode) {
    List<ItemSearchResponse> itemAutoCompleteResponses = new ArrayList<>();
    searchResponse.hits().hits()
        .forEach(hit -> itemAutoCompleteResponses.add(convertListResponse(hit.source(), languageCode)));
    return itemAutoCompleteResponses;
  }

  private ItemSearchResponse convertListResponse(ItemDocument itemDocument, String languageCode) {
    return ItemSearchResponse.builder()
        .itemId(itemDocument.getItemId())
        .itemNumber(itemDocument.getItemNumber())
        .itemTitle(
            itemDocument.getItemTitles().stream()
                .filter(title -> languageCode.equals(title.getLanguageCode()))
                .findFirst().orElse(itemDocument.getItemTitles().getFirst()).getTitle())
        .itemDescriptions(itemDocument.getItemDescriptions().stream()
            .filter(title -> languageCode.equals(title.getLanguageCode()))
            .flatMap(description -> description.getDescription().stream())
            .toList())
        .price(itemDocument.getPrice())
        .discountPercentage(itemDocument.getDiscountPercentage())
        .rating(itemDocument.getRating())
        .currencyCode(itemDocument.getCurrency())
        .totalQauntity(itemDocument.getAvailabilityCount())
        .availabilityMessage(itemDocument.getAvailability())
        .popularity(itemDocument.getPopularity())
        .mediaPaths(itemDocument.getImagePaths().stream().map(path -> path.getPath()).toList())
        .build();
  }

  private List<ItemSearchFilterResponse> buildAggregation(SearchResponse<ItemDocument> searchResponse) {
    List<ItemSearchFilterResponse> itemSearchFilterResponses = new ArrayList<>();
    Map<String, Aggregate> aggregations = searchResponse.aggregations();

    Aggregate nestedAttributesAggregate = aggregations.get("nested_attributes");

    if (nestedAttributesAggregate != null && nestedAttributesAggregate.isNested()) {
      Aggregate attributeNamesAggregate = nestedAttributesAggregate.nested().aggregations()
          .get("attribute_name_buckets");

      if (attributeNamesAggregate != null && attributeNamesAggregate.isSterms()) {
        for (StringTermsBucket nameBucket : attributeNamesAggregate.sterms().buckets().array()) {

          String attributeName = nameBucket.key();
          long nameDocCount = nameBucket.docCount();
          Map<String, Long> attributeValueMap = new HashMap<>();

          Aggregate valuesAggregate = nameBucket.aggregations().get("attribute_value_buckets");
          if (valuesAggregate != null && valuesAggregate.isSterms()) {
            for (StringTermsBucket valueBucket : valuesAggregate.sterms().buckets().array()) {
              attributeValueMap.put(valueBucket.key(), valueBucket.docCount());
            }
          }
          itemSearchFilterResponses.add(ItemSearchFilterResponse.builder()
              .attributeTotalCount(nameDocCount)
              .attributeName(attributeName)
              .attributeValues(attributeValueMap)
              .build());
        }
      }
    }
    return itemSearchFilterResponses;
  }

  // private Query getFilterForList(Map<String, String> filters) {
  //   return Query.of(f -> f.nested(nf -> nf
  //       .path("itemAttributes")
  //       .query(fq -> fq.bool(
  //           bf -> bf.must(getFilterQuery(filters))))));
  // }

  private List<Query> getFilterQuery(Map<String, List<String>> filters) {
    {
      List<Query> queries = new ArrayList<>();
      filters.forEach((key,
          values) -> values.forEach(value -> queries.add(Query.of(qu -> qu.nested(ns -> ns.path("itemAttributes")
              .query(nq -> nq.bool(
                  must -> must.must(
                      List.of(Query.of(mq -> mq.term(
                          tr -> tr.field("itemAttributes.attributeName")
                              .value(FieldValue.of(key)))),
                          Query.of(mq -> mq.term(
                              tr -> tr.field("itemAttributes.attributeValue")
                                  .value(FieldValue.of(value)))))))))))));
      return queries;
    }
  }
}
