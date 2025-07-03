package com.ecopreco.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    
    @NotNull(message = "Produto é obrigatório")
    private Long productId;
    
    @NotNull(message = "Rating é obrigatório")
    @Min(value = 1, message = "Rating deve ser entre 1 e 5")
    @Max(value = 5, message = "Rating deve ser entre 1 e 5")
    private Integer rating;
    
    private String comment;
}

