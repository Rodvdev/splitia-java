package com.splitia.model.inventory;

import com.splitia.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    @NotNull
    @Size(max = 100)
    private String sku;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private com.splitia.model.enums.ProductType type;
    
    @Column(name = "price", precision = 19, scale = 2, nullable = false)
    @NotNull
    private BigDecimal price;
    
    @Column(name = "cost", precision = 19, scale = 2)
    private BigDecimal cost;
    
    @Column(nullable = false)
    private String currency = "USD";
    
    @Column(name = "category")
    @Size(max = 100)
    private String category;
    
    @Column(name = "images", columnDefinition = "TEXT[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> images = new ArrayList<>();
    
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Stock stock;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();
}

