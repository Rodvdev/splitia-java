package com.splitia.service;

import com.splitia.dto.request.CreateSupportTicketRequest;
import com.splitia.dto.request.UpdateSupportTicketRequest;
import com.splitia.dto.response.SupportTicketResponse;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.SupportTicketMapper;
import com.splitia.model.SupportTicket;
import com.splitia.model.User;
import com.splitia.repository.SupportTicketRepository;
import com.splitia.repository.UserRepository;
import com.splitia.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupportService {
    
    private final SupportTicketRepository supportTicketRepository;
    private final SupportTicketMapper supportTicketMapper;
    private final UserRepository userRepository;
    
    public Page<SupportTicketResponse> getTickets(Pageable pageable) {
        User currentUser = getCurrentUser();
        return supportTicketRepository.findByCreatedById(currentUser.getId(), pageable)
                .map(supportTicketMapper::toResponse);
    }
    
    @Transactional
    public SupportTicketResponse createTicket(CreateSupportTicketRequest request) {
        User currentUser = getCurrentUser();
        
        SupportTicket ticket = new SupportTicket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setCategory(request.getCategory());
        ticket.setPriority(request.getPriority());
        ticket.setCreatedBy(currentUser);
        
        ticket = supportTicketRepository.save(ticket);
        return supportTicketMapper.toResponse(ticket);
    }
    
    public SupportTicketResponse getTicketById(UUID ticketId) {
        User currentUser = getCurrentUser();
        SupportTicket ticket = supportTicketRepository.findByIdAndDeletedAtIsNull(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", ticketId));
        
        // Users can only see their own tickets, admins can see all
        if (!ticket.getCreatedBy().getId().equals(currentUser.getId()) && !currentUser.getRole().equals("ADMIN")) {
            throw new ResourceNotFoundException("SupportTicket", "id", ticketId);
        }
        
        return supportTicketMapper.toResponse(ticket);
    }
    
    @Transactional
    public SupportTicketResponse updateTicket(UUID ticketId, UpdateSupportTicketRequest request) {
        // Only admins can update tickets
        SupportTicket ticket = supportTicketRepository.findByIdAndDeletedAtIsNull(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", ticketId));
        
        if (request.getTitle() != null) {
            ticket.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            ticket.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            ticket.setStatus(request.getStatus());
            if (request.getStatus().name().equals("RESOLVED") && ticket.getResolvedAt() == null) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
        }
        if (request.getPriority() != null) {
            ticket.setPriority(request.getPriority());
        }
        if (request.getResolution() != null) {
            ticket.setResolution(request.getResolution());
        }
        
        ticket = supportTicketRepository.save(ticket);
        return supportTicketMapper.toResponse(ticket);
    }
    
    @Transactional
    public void softDeleteTicket(UUID ticketId) {
        // Only admins can delete tickets
        SupportTicket ticket = supportTicketRepository.findByIdAndDeletedAtIsNull(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", ticketId));
        
        ticket.setDeletedAt(LocalDateTime.now());
        supportTicketRepository.save(ticket);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        return userRepository.findByIdAndDeletedAtIsNull(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));
    }
}

