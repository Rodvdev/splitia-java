package com.splitia.model.finance;

import com.splitia.model.BaseEntity;
import com.splitia.model.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Data
@EqualsAndHashCode(callSuper = true)
public class Account extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    @NotNull
    @Size(max = 20)
    private String code;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private AccountType type;
    
    @Column(name = "balance", precision = 19, scale = 2, nullable = false)
    @NotNull
    private BigDecimal balance = BigDecimal.ZERO;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_account_id")
    private Account parentAccount;
    
    @OneToMany(mappedBy = "parentAccount", cascade = CascadeType.ALL)
    private List<Account> childAccounts = new ArrayList<>();
    
    @Column(name = "is_active", nullable = false)
    @NotNull
    private Boolean isActive = true;
}

