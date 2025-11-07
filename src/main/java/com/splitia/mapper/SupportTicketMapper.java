package com.splitia.mapper;

import com.splitia.dto.response.SupportTicketResponse;
import com.splitia.model.SupportTicket;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface SupportTicketMapper {
    SupportTicketMapper INSTANCE = Mappers.getMapper(SupportTicketMapper.class);
    
    SupportTicketResponse toResponse(SupportTicket ticket);
    
    List<SupportTicketResponse> toResponseList(List<SupportTicket> tickets);
}

