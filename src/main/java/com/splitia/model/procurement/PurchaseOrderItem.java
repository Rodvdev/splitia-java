package com.splitia.model.procurement;

import com.splitia.model.BaseEntity;
import com.splitia.model.inventory.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "purchase_order_items")
@Data
@EqualsAndHashCode(callSuper = true)
public class PurchaseOrderItem extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    @NotNull
    private PurchaseOrder purchaseOrder;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    private Product product;
    
    @Column(nullable = false)
    @NotNull
    private Integer quantity;
    
    @Column(name = "unit_price", precision = 19, scale = 2, nullable = false)
    @NotNull
    private BigDecimal unitPrice;
    
    @Column(name = "total", precision = 19, scale = 2, nullable = false)
    @NotNull
    private BigDecimal total;
    
    @Column(name = "received_quantity", nullable = false)
    @NotNull
    private Integer receivedQuantity = 0;
}

