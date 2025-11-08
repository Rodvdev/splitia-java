package com.splitia.model.marketing;

import com.splitia.model.BaseEntity;
import com.splitia.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "email_templates")
@Data
@EqualsAndHashCode(callSuper = true)
public class EmailTemplate extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 255)
    private String subject;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotNull
    private String body; // HTML content
    
    @Column(name = "variables", columnDefinition = "TEXT[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> variables = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    @NotNull
    private User createdBy;
}

