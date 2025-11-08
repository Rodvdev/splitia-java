package com.splitia.model.procurement;

import com.splitia.model.BaseEntity;
import com.splitia.model.User;
import com.splitia.model.enums.PaymentTerms;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "vendors")
@Data
@EqualsAndHashCode(callSuper = true)
public class Vendor extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;
    
    @Column(name = "contact_name")
    @Size(max = 255)
    private String contactName;
    
    @Email
    private String email;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "tax_id")
    @Size(max = 50)
    private String taxId;
    
    private String address;
    private String city;
    private String country;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_terms")
    private PaymentTerms paymentTerms;
    
    @Column(name = "rating")
    private Integer rating; // 1-5
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();
    
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VendorContract> contracts = new ArrayList<>();
}

