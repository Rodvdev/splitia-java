package com.splitia.model.finance;

import com.splitia.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "invoice_items")
@Data
@EqualsAndHashCode(callSuper = true)
public class InvoiceItem extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotNull
    private String description;
    
    @Column(nullable = false)
    @NotNull
    private Integer quantity;
    
    @Column(name = "unit_price", precision = 19, scale = 2, nullable = false)
    @NotNull
    private BigDecimal unitPrice;
    
    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate = BigDecimal.ZERO;
    
    @Column(name = "total", precision = 19, scale = 2, nullable = false)
    @NotNull
    private BigDecimal total;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @NotNull
    private Invoice invoice;
}

