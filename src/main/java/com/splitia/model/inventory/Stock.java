package com.splitia.model.inventory;

import com.splitia.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "stock")
@Data
@EqualsAndHashCode(callSuper = true)
public class Stock extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    @NotNull
    private Product product;
    
    @Column(nullable = false)
    @NotNull
    private Integer quantity = 0;
    
    @Column(name = "min_quantity", nullable = false)
    @NotNull
    private Integer minQuantity = 0;
    
    @Column(name = "max_quantity")
    private Integer maxQuantity;
    
    @Column(name = "location")
    private String location;
    
    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    private List<StockMovement> movements = new ArrayList<>();
}

