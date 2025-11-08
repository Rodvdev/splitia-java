package com.splitia.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductVariantRequest {
    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU must be less than 100 characters")
    private String sku;
    
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;
    
    private BigDecimal price;
    
    private BigDecimal cost;
    
    private String attributes; // JSON string
}

