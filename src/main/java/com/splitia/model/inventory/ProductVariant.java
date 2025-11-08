package com.splitia.model.inventory;

import com.splitia.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductVariant extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 100)
    private String sku;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;
    
    @Column(name = "price", precision = 19, scale = 2)
    private BigDecimal price;
    
    @Column(name = "cost", precision = 19, scale = 2)
    private BigDecimal cost;
    
    @Column(name = "attributes", columnDefinition = "JSONB")
    private String attributes; // JSON string for variant attributes (size, color, etc.)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    private Product product;
}

