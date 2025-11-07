package com.splitia.mapper;

import com.splitia.dto.response.MessageResponse;
import com.splitia.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);
    
    @Mapping(source = "sender", target = "sender")
    @Mapping(source = "createdAt", target = "createdAt")
    MessageResponse toResponse(Message message);
    
    List<MessageResponse> toResponseList(List<Message> messages);
}

