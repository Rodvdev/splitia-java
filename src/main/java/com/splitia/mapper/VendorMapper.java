package com.splitia.mapper;

import com.splitia.dto.response.PurchaseOrderItemResponse;
import com.splitia.dto.response.PurchaseOrderResponse;
import com.splitia.dto.response.VendorResponse;
import com.splitia.model.procurement.PurchaseOrder;
import com.splitia.model.procurement.PurchaseOrderItem;
import com.splitia.model.procurement.Vendor;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VendorMapper {
    VendorMapper INSTANCE = Mappers.getMapper(VendorMapper.class);
    
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", ignore = true)
    VendorResponse toResponse(Vendor vendor);
    
    @AfterMapping
    default void setCreatedByName(@MappingTarget VendorResponse response, Vendor vendor) {
        if (vendor.getCreatedBy() != null) {
            String fullName = vendor.getCreatedBy().getName();
            if (vendor.getCreatedBy().getLastName() != null && !vendor.getCreatedBy().getLastName().isEmpty()) {
                fullName += " " + vendor.getCreatedBy().getLastName();
            }
            response.setCreatedByName(fullName);
        }
    }
    
    List<VendorResponse> toResponseList(List<Vendor> vendors);
}

