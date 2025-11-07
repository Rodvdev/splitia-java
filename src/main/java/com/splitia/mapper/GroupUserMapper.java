package com.splitia.mapper;

import com.splitia.dto.response.GroupUserResponse;
import com.splitia.model.GroupUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, GroupMapper.class})
public interface GroupUserMapper {
    GroupUserMapper INSTANCE = Mappers.getMapper(GroupUserMapper.class);
    
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "group.id", target = "groupId")
    @Mapping(source = "group.name", target = "groupName")
    GroupUserResponse toResponse(GroupUser groupUser);
    
    List<GroupUserResponse> toResponseList(List<GroupUser> groupUsers);
}

