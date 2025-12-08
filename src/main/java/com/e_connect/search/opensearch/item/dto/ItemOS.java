package com.e_connect.search.opensearch.item.dto;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ItemOS {
    
    private String partId;
    private String partNumber;
    private String partTitle;
    private String description;
    private List<String> brandIds;
    private List<String> brandNames;
    private List<String> categoryIds;
    private List<String> categoryNames;
    private Float price;
    private Float currency;
    private Float discountPercentage;
    private Float rating;
    private String availability;
    private Float availabilityCount;
    private Date createdAt;
    private Date updatedAt;
    private List<ItemAttributeOS> attributes;
    private Long popularity;
}
