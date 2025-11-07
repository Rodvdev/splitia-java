package com.splitia.mapper;

import com.splitia.dto.response.UserResponse;
import com.splitia.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    UserResponse toResponse(User user);
}

