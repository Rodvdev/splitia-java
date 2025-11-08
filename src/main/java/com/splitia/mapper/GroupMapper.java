package com.splitia.mapper;

import com.splitia.dto.response.GroupMemberResponse;
import com.splitia.dto.response.GroupResponse;
import com.splitia.model.Group;
import com.splitia.model.GroupUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface GroupMapper {
    GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);
    
    @Mapping(source = "members", target = "members", qualifiedByName = "filterActiveMembers")
    @Mapping(source = "conversation.id", target = "conversationId")
    GroupResponse toResponse(Group group);
    
    List<GroupResponse> toResponseList(List<Group> groups);
    
    @Mapping(source = "user", target = "user")
    @Mapping(source = "role", target = "role")
    GroupMemberResponse toMemberResponse(GroupUser groupUser);
    
    List<GroupMemberResponse> toMemberResponseList(List<GroupUser> groupUsers);
    
    @Named("filterActiveMembers")
    default List<GroupMemberResponse> filterActiveMembers(List<GroupUser> members) {
        if (members == null) {
            return List.of();
        }
        return members.stream()
                .filter(member -> member.getDeletedAt() == null)
                .map(this::toMemberResponse)
                .collect(Collectors.toList());
    }
}

