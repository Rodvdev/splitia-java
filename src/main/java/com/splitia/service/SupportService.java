package com.splitia.service;

import com.splitia.dto.request.CreateSupportTicketRequest;
import com.splitia.dto.response.SupportTicketResponse;
import com.splitia.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupportService {
    
    private final SupportTicketRepository supportTicketRepository;
    
    public SupportTicketResponse createTicket(CreateSupportTicketRequest request) {
        // Implementation placeholder
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public SupportTicketResponse getTicketById(UUID ticketId) {
        // Implementation placeholder
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

