package com.splitia.model.finance;

import com.splitia.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "journal_entries")
@Data
@EqualsAndHashCode(callSuper = true)
public class JournalEntry extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "entry_number", nullable = false, unique = true)
    @NotNull
    @Size(max = 50)
    private String entryNumber;
    
    @Column(nullable = false)
    @NotNull
    private LocalDate date;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotNull
    private String description;
    
    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JournalTransaction> transactions = new ArrayList<>();
}

