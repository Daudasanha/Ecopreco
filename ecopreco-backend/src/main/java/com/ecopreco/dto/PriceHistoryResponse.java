package com.ecopreco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryResponse {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private String store;
    private LocalDateTime recordedAt;
}

