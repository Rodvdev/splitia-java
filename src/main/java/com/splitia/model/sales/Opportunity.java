package com.splitia.model.sales;

import com.splitia.model.BaseEntity;
import com.splitia.model.User;
import com.splitia.model.contact.Contact;
import com.splitia.model.contact.Company;
import com.splitia.model.enums.OpportunityStage;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "opportunities")
@Data
@EqualsAndHashCode(callSuper = true)
public class Opportunity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "estimated_value", precision = 19, scale = 2)
    private BigDecimal estimatedValue;
    
    @Column(nullable = false)
    @NotNull
    @Min(0)
    @Max(100)
    private Integer probability = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private OpportunityStage stage = OpportunityStage.LEAD;
    
    @Column(name = "expected_close_date")
    private LocalDate expectedCloseDate;
    
    @Column(name = "actual_close_date")
    private LocalDate actualCloseDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Activity> activities = new ArrayList<>();
    
    @Column(name = "won_amount", precision = 19, scale = 2)
    private BigDecimal wonAmount;
    
    @Column(name = "lost_reason", columnDefinition = "TEXT")
    private String lostReason;
    
    @Column(nullable = false)
    private String currency = "USD";
}

