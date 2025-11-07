package com.splitia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "message_seen",
       uniqueConstraints = @UniqueConstraint(columnNames = {"message_id", "user_id"}))
@Data
@EqualsAndHashCode(callSuper = true)
public class MessageSeen extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    @NotNull
    private Message message;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;
    
    @Column(name = "seen_at", nullable = false)
    @NotNull
    private LocalDateTime seenAt;
}

