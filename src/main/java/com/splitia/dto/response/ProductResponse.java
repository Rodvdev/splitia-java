package com.splitia.dto.response;

import com.splitia.model.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private UUID id;
    private String sku;
    private String name;
    private String description;
    private ProductType type;
    private BigDecimal price;
    private BigDecimal cost;
    private String currency;
    private String category;
    private List<String> images;
    private StockResponse stock;
    private List<ProductVariantResponse> variants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

