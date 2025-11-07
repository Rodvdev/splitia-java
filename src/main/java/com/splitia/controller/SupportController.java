package com.splitia.controller;

import com.splitia.dto.request.CreateSupportTicketRequest;
import com.splitia.dto.request.UpdateSupportTicketRequest;
import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.SupportTicketResponse;
import com.splitia.service.SupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
@Tag(name = "Support", description = "Support ticket endpoints")
public class SupportController {
    
    private final SupportService supportService;
    
    @GetMapping("/tickets")
    @Operation(summary = "Get user support tickets (paginated)")
    public ResponseEntity<ApiResponse<Page<SupportTicketResponse>>> getTickets(Pageable pageable) {
        Page<SupportTicketResponse> tickets = supportService.getTickets(pageable);
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }
    
    @PostMapping("/tickets")
    @Operation(summary = "Create a support ticket")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> createTicket(
            @Valid @RequestBody CreateSupportTicketRequest request) {
        SupportTicketResponse ticket = supportService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ticket, "Ticket created successfully"));
    }
    
    @GetMapping("/tickets/{id}")
    @Operation(summary = "Get ticket by ID")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> getTicketById(@PathVariable UUID id) {
        SupportTicketResponse ticket = supportService.getTicketById(id);
        return ResponseEntity.ok(ApiResponse.success(ticket));
    }
    
    @PutMapping("/tickets/{id}")
    @Operation(summary = "Update ticket (Admin only)")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> updateTicket(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSupportTicketRequest request) {
        SupportTicketResponse ticket = supportService.updateTicket(id, request);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Ticket updated successfully"));
    }
    
    @DeleteMapping("/tickets/{id}")
    @Operation(summary = "Delete ticket (soft delete, Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(@PathVariable UUID id) {
        supportService.softDeleteTicket(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Ticket deleted successfully"));
    }
}

