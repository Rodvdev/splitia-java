package com.splitia.mapper;

import com.splitia.dto.response.StockMovementResponse;
import com.splitia.model.inventory.StockMovement;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {
    StockMovementMapper INSTANCE = Mappers.getMapper(StockMovementMapper.class);
    
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "productSku", ignore = true)
    StockMovementResponse toResponse(StockMovement movement);
    
    @AfterMapping
    default void setProductInfo(@MappingTarget StockMovementResponse response, StockMovement movement) {
        if (movement.getProduct() != null) {
            response.setProductName(movement.getProduct().getName());
            response.setProductSku(movement.getProduct().getSku());
        }
    }
    
    List<StockMovementResponse> toResponseList(List<StockMovement> movements);
}

