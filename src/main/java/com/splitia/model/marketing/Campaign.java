package com.splitia.model.marketing;

import com.splitia.model.BaseEntity;
import com.splitia.model.User;
import com.splitia.model.enums.CampaignStatus;
import com.splitia.model.enums.CampaignType;
import jakarta.persistence.*;
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
@Table(name = "campaigns")
@Data
@EqualsAndHashCode(callSuper = true)
public class Campaign extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private CampaignType type;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private CampaignStatus status = CampaignStatus.DRAFT;
    
    @Column(name = "budget", precision = 19, scale = 2)
    private BigDecimal budget;
    
    @Column(name = "spent", precision = 19, scale = 2)
    private BigDecimal spent = BigDecimal.ZERO;
    
    @Column(name = "sent_count", nullable = false)
    @NotNull
    private Integer sentCount = 0;
    
    @Column(name = "opened_count", nullable = false)
    @NotNull
    private Integer openedCount = 0;
    
    @Column(name = "clicked_count", nullable = false)
    @NotNull
    private Integer clickedCount = 0;
    
    @Column(name = "converted_count", nullable = false)
    @NotNull
    private Integer convertedCount = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    @NotNull
    private User createdBy;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampaignContact> contacts = new ArrayList<>();
}

