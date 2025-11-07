package com.splitia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Table(name = "support_messages")
@Data
@EqualsAndHashCode(callSuper = true)
public class SupportMessage extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotNull
    @Size(min = 1)
    private String content;
    
    @Column(name = "is_from_support", nullable = false)
    @NotNull
    private Boolean isFromSupport = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    @NotNull
    private SupportTicket ticket;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}

