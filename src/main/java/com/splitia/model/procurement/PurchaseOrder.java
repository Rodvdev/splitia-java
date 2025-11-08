package com.splitia.model.procurement;

import com.splitia.model.BaseEntity;
import com.splitia.model.User;
import com.splitia.model.enums.PurchaseOrderStatus;
import com.splitia.model.inventory.Product;
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
@Table(name = "purchase_orders")
@Data
@EqualsAndHashCode(callSuper = true)
public class PurchaseOrder extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "order_number", nullable = false, unique = true)
    @NotNull
    @Size(max = 50)
    private String orderNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    @NotNull
    private Vendor vendor;
    
    @Column(name = "order_date", nullable = false)
    @NotNull
    private LocalDate orderDate;
    
    @Column(name = "expected_date")
    private LocalDate expectedDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private PurchaseOrderStatus status = PurchaseOrderStatus.DRAFT;
    
    @Column(name = "total", precision = 19, scale = 2, nullable = false)
    @NotNull
    private BigDecimal total = BigDecimal.ZERO;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    @NotNull
    private User createdBy;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderItem> items = new ArrayList<>();
}

