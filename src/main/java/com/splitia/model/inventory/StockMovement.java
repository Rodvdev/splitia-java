package com.splitia.model.inventory;

import com.splitia.model.BaseEntity;
import com.splitia.model.enums.StockMovementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "stock_movements")
@Data
@EqualsAndHashCode(callSuper = true)
public class StockMovement extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    @NotNull
    private Stock stock;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    private Product product;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private StockMovementType type;
    
    @Column(nullable = false)
    @NotNull
    private Integer quantity;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Column(name = "reference")
    private String reference;
    
    @Column(nullable = false)
    @NotNull
    private LocalDate date;
}

