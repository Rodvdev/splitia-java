package com.splitia.mapper;

import com.splitia.dto.response.ConversationResponse;
import com.splitia.model.Conversation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ConversationMapper {
    ConversationMapper INSTANCE = Mappers.getMapper(ConversationMapper.class);
    
    @Mapping(source = "participants", target = "participants")
    @Mapping(source = "createdAt", target = "createdAt")
    ConversationResponse toResponse(Conversation conversation);
    
    List<ConversationResponse> toResponseList(List<Conversation> conversations);
}

