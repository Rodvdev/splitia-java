package com.splitia.model.procurement;

import com.splitia.model.BaseEntity;
import com.splitia.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "vendor_contracts")
@Data
@EqualsAndHashCode(callSuper = true)
public class VendorContract extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    @NotNull
    private Vendor vendor;
    
    @Column(name = "contract_number", nullable = false, unique = true)
    @NotNull
    @Size(max = 50)
    private String contractNumber;
    
    @Column(name = "start_date", nullable = false)
    @NotNull
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(columnDefinition = "TEXT")
    private String terms;
    
    @Column(name = "document_path")
    private String documentPath;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    @NotNull
    private User createdBy;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
}

