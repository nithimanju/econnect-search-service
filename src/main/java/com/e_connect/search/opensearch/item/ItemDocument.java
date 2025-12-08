package com.e_connect.search.opensearch.item;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Document(indexName = "${opensearch.item.index}", createIndex = false)
@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDocument {

  @Id
  @Field(type = FieldType.Keyword)
  private String itemId;

  @MultiField(mainField = @Field(type = FieldType.Text), otherFields = {
      @InnerField(suffix = "keyword", type = FieldType.Keyword, ignoreAbove = 256)
  })
  private String itemNumber;

  @Field(type = FieldType.Nested, includeInParent = true)
  private List<ItemTitle> itemTitles;

  @Field(type = FieldType.Nested, includeInParent = true)
  private List<ItemDescription> itemDescriptions;

  @Field(type = FieldType.Keyword)
  private String brandId;

  @Field(type = FieldType.Keyword)
  private String categoryId;

  @Field(type = FieldType.Float)
  private Float price;

  @Field(type = FieldType.Keyword)
  private String currency;

  @Field(type = FieldType.Float)
  private Float discountPercentage;

  @Field(type = FieldType.Float)
  private Float rating;

  @MultiField(mainField = @Field(type = FieldType.Keyword))
  private String availability;

  @Field(type = FieldType.Float)
  private Float availabilityCount;

  @Field(type = FieldType.Date)
  private Date createdAt;

  @Field(type = FieldType.Date)
  private Date updatedAt;

  @Field(type = FieldType.Nested, includeInParent = true)
  private List<ItemAttribute> itemAttributes;

  @Field(type = FieldType.Text, analyzer = "autocomplete_index_analyzer", searchAnalyzer = "autocomplete_search_analyzer")
  private List<String> suggest;

  @Field(type = FieldType.Long)
  private Long popularity;

  @Field(type = FieldType.Nested, includeInParent = true)
  private List<ImagePath> imagePaths;

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Builder(toBuilder = true)
  public static class ItemTitle {
    private String languageCode;
    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "std_with_synonyms"), otherFields = {
        @InnerField(suffix = "keyword", type = FieldType.Keyword, ignoreAbove = 256),
        @InnerField(suffix = "autocomplete", type = FieldType.Text, analyzer = "autocomplete_index_analyzer", searchAnalyzer = "autocomplete_search_analyzer")
    })
    private String title;
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Builder(toBuilder = true)
  public static class ItemDescription {
    private String languageCode;
    @Field(type = FieldType.Text, analyzer = "std_with_synonyms")
    private List<String> description;
  }

  @Getter
  @Builder(toBuilder = true)
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ItemAttribute {
    @Field(type = FieldType.Keyword)
    private String attributeName;

    @Field(type = FieldType.Keyword)
    private List<String> attributeValue;

    @Field(type = FieldType.Keyword)
    private String attributeId;

    @Field(type = FieldType.Keyword)
    private String attributeType;
  }

  @Getter
  @Builder(toBuilder = true)
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ImagePath {
    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Keyword)
    private String path;

    @Field(type = FieldType.Keyword)
    private String imageId;

    @Field(type = FieldType.Keyword)
    private Long sequence;
  }
}