package com.ecopreco.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    
    @NotBlank(message = "Nome do produto é obrigatório")
    private String name;
    
    private String description;
    
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
    private BigDecimal currentPrice;
    
    private String brand;
    
    private String category;
    
    private String ecoLabel;
    
    private String productUrl;
    
    private String imageUrl;
    
    private String store;
    
    private String sustainabilityInfo;
}

