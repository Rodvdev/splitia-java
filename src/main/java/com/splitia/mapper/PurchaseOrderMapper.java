package com.splitia.mapper;

import com.splitia.dto.response.PurchaseOrderItemResponse;
import com.splitia.dto.response.PurchaseOrderResponse;
import com.splitia.model.procurement.PurchaseOrder;
import com.splitia.model.procurement.PurchaseOrderItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {
    PurchaseOrderMapper INSTANCE = Mappers.getMapper(PurchaseOrderMapper.class);
    
    @Mapping(target = "vendorId", source = "vendor.id")
    @Mapping(target = "vendorName", ignore = true)
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", ignore = true)
    @Mapping(target = "items", ignore = true)
    PurchaseOrderResponse toResponse(PurchaseOrder purchaseOrder);
    
    @AfterMapping
    default void setRelatedData(@MappingTarget PurchaseOrderResponse response, PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getVendor() != null) {
            response.setVendorName(purchaseOrder.getVendor().getName());
        }
        if (purchaseOrder.getCreatedBy() != null) {
            String fullName = purchaseOrder.getCreatedBy().getName();
            if (purchaseOrder.getCreatedBy().getLastName() != null && !purchaseOrder.getCreatedBy().getLastName().isEmpty()) {
                fullName += " " + purchaseOrder.getCreatedBy().getLastName();
            }
            response.setCreatedByName(fullName);
        }
        if (purchaseOrder.getItems() != null && !purchaseOrder.getItems().isEmpty()) {
            response.setItems(purchaseOrder.getItems().stream()
                    .map(this::itemToResponse)
                    .toList());
        }
    }
    
    PurchaseOrderItemResponse itemToResponse(PurchaseOrderItem item);
    
    @AfterMapping
    default void setItemProductInfo(@MappingTarget PurchaseOrderItemResponse response, PurchaseOrderItem item) {
        if (item.getProduct() != null) {
            response.setProductName(item.getProduct().getName());
            response.setProductSku(item.getProduct().getSku());
        }
    }
    
    List<PurchaseOrderResponse> toResponseList(List<PurchaseOrder> purchaseOrders);
}

