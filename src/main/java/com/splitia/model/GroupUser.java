package com.splitia.model;

import com.splitia.model.enums.GroupRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Table(name = "group_users", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "group_id"}))
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupUser extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupRole role = GroupRole.MEMBER;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @NotNull
    private Group group;
}

