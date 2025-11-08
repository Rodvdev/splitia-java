package com.splitia.mapper;

import com.splitia.dto.response.ProductResponse;
import com.splitia.dto.response.ProductVariantResponse;
import com.splitia.dto.response.StockResponse;
import com.splitia.model.inventory.Product;
import com.splitia.model.inventory.ProductVariant;
import com.splitia.model.inventory.Stock;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);
    
    @Mapping(target = "stock", ignore = true)
    @Mapping(target = "variants", ignore = true)
    ProductResponse toResponse(Product product);
    
    @AfterMapping
    default void setRelatedData(@MappingTarget ProductResponse response, Product product) {
        if (product.getStock() != null) {
            response.setStock(stockToResponse(product.getStock()));
        }
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            response.setVariants(product.getVariants().stream()
                    .map(this::variantToResponse)
                    .toList());
        }
    }
    
    ProductVariantResponse variantToResponse(ProductVariant variant);
    
    StockResponse stockToResponse(Stock stock);
    
    @AfterMapping
    default void setStockProductInfo(@MappingTarget StockResponse response, Stock stock) {
        if (stock.getProduct() != null) {
            response.setProductId(stock.getProduct().getId());
            response.setProductName(stock.getProduct().getName());
            response.setProductSku(stock.getProduct().getSku());
            response.setIsLowStock(stock.getQuantity() <= stock.getMinQuantity());
        }
    }
    
    List<ProductResponse> toResponseList(List<Product> products);
}

