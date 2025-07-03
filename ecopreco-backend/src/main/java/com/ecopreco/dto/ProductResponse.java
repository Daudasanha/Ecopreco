package com.ecopreco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal currentPrice;
    private String brand;
    private String category;
    private String ecoLabel;
    private String productUrl;
    private String imageUrl;
    private String store;
    private String sustainabilityInfo;
    private BigDecimal averageRating;
    private Long reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

