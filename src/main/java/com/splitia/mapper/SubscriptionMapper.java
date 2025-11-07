package com.splitia.mapper;

import com.splitia.dto.response.SubscriptionResponse;
import com.splitia.model.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    SubscriptionMapper INSTANCE = Mappers.getMapper(SubscriptionMapper.class);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "planType", source = "planType")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "autoRenew", source = "autoRenew")
    @Mapping(target = "pricePerMonth", source = "pricePerMonth")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "createdAt", source = "createdAt")
    SubscriptionResponse toResponse(Subscription subscription);
    
    List<SubscriptionResponse> toResponseList(List<Subscription> subscriptions);
}

