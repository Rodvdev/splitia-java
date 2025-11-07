package com.splitia.mapper;

import com.splitia.dto.response.GroupMemberResponse;
import com.splitia.dto.response.GroupResponse;
import com.splitia.model.Group;
import com.splitia.model.GroupUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface GroupMapper {
    GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);
    
    GroupResponse toResponse(Group group);
    
    List<GroupResponse> toResponseList(List<Group> groups);
    
    @Mapping(source = "user", target = "user")
    @Mapping(source = "role", target = "role")
    GroupMemberResponse toMemberResponse(GroupUser groupUser);
    
    List<GroupMemberResponse> toMemberResponseList(List<GroupUser> groupUsers);
}

