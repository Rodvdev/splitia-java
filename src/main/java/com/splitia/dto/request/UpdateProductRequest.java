package com.splitia.dto.request;

import com.splitia.model.enums.ProductType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
    @Size(max = 100, message = "SKU must be less than 100 characters")
    private String sku;
    
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;
    
    @Size(max = 10000, message = "Description must be less than 10000 characters")
    private String description;
    
    private ProductType type;
    
    private BigDecimal price;
    
    private BigDecimal cost;
    
    @Size(max = 10)
    private String currency;
    
    @Size(max = 100, message = "Category must be less than 100 characters")
    private String category;
    
    private List<String> images;
    
    @Valid
    private List<CreateProductVariantRequest> variants;
    
    // Stock fields
    private Integer minQuantity;
    private Integer maxQuantity;
    private String location;
}

