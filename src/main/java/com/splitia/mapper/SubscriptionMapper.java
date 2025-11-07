package com.splitia.mapper;

import com.splitia.dto.response.SubscriptionResponse;
import com.splitia.model.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    SubscriptionMapper INSTANCE = Mappers.getMapper(SubscriptionMapper.class);
    
    SubscriptionResponse toResponse(Subscription subscription);
    
    List<SubscriptionResponse> toResponseList(List<Subscription> subscriptions);
}

