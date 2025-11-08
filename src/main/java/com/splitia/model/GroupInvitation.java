package com.splitia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;
import com.splitia.model.enums.InvitationStatus;

@Entity
@Table(name = "group_invitations")
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupInvitation extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    @NotNull
    private String token;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "max_uses")
    private Integer maxUses;
    
    @Column(name = "current_uses", nullable = false)
    private Integer currentUses = 0;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "email")
    private String email;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_user_id")
    private User invitedUser;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @NotNull
    private Group group;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    @NotNull
    private User createdBy;
}
