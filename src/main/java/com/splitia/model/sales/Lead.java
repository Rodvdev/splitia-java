package com.splitia.model.sales;

import com.splitia.model.BaseEntity;
import com.splitia.model.User;
import com.splitia.model.contact.Contact;
import com.splitia.model.enums.LeadSource;
import com.splitia.model.enums.LeadStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Table(name = "leads")
@Data
@EqualsAndHashCode(callSuper = true)
public class Lead extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "first_name", nullable = false)
    @NotNull
    @Size(max = 100)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    @NotNull
    @Size(max = 100)
    private String lastName;
    
    @Column(nullable = false)
    @NotNull
    @Email
    private String email;
    
    @Column(name = "phone")
    @Size(max = 50)
    private String phone;
    
    @Column(name = "company")
    @Size(max = 255)
    private String company;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private LeadSource source;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private LeadStatus status = LeadStatus.NEW;
    
    @Column(nullable = false)
    @NotNull
    @Min(0)
    @Max(100)
    private Integer score = 0;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "converted_to_opportunity_id")
    private UUID convertedToOpportunityId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;
}

