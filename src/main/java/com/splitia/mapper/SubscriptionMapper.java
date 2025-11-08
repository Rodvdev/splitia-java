package com.splitia.mapper;

import com.splitia.dto.response.SubscriptionResponse;
import com.splitia.model.Subscription;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    SubscriptionMapper INSTANCE = Mappers.getMapper(SubscriptionMapper.class);
    
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", ignore = true)
    SubscriptionResponse toResponse(Subscription subscription);
    
    @AfterMapping
    default void setUserName(@MappingTarget SubscriptionResponse response, Subscription subscription) {
        if (subscription.getUser() != null) {
            String fullName = subscription.getUser().getName();
            if (subscription.getUser().getLastName() != null && !subscription.getUser().getLastName().isEmpty()) {
                fullName += " " + subscription.getUser().getLastName();
            }
            response.setUserName(fullName);
        }
    }
    
    List<SubscriptionResponse> toResponseList(List<Subscription> subscriptions);
}

