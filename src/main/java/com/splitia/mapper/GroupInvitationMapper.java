package com.splitia.mapper;

import com.splitia.dto.response.GroupInvitationResponse;
import com.splitia.model.GroupInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface GroupInvitationMapper {
    GroupInvitationMapper INSTANCE = Mappers.getMapper(GroupInvitationMapper.class);
    
    @Mapping(source = "group.id", target = "groupId")
    @Mapping(source = "group.name", target = "groupName")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "createdBy.name", target = "createdByName")
    GroupInvitationResponse toResponse(GroupInvitation invitation);
    
    List<GroupInvitationResponse> toResponseList(List<GroupInvitation> invitations);
}

