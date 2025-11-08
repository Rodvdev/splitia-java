package com.splitia.model.finance;

import com.splitia.model.BaseEntity;
import com.splitia.model.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "journal_transactions")
@Data
@EqualsAndHashCode(callSuper = true)
public class JournalTransaction extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_entry_id", nullable = false)
    @NotNull
    private JournalEntry journalEntry;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @NotNull
    private Account account;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private TransactionType type;
    
    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal amount;
    
    @Column(columnDefinition = "TEXT")
    private String description;
}

