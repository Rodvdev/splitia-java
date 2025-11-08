package com.splitia.model.finance;

import com.splitia.model.BaseEntity;
import com.splitia.model.User;
import com.splitia.model.contact.Company;
import com.splitia.model.contact.Contact;
import com.splitia.model.enums.InvoiceStatus;
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
@Table(name = "invoices")
@Data
@EqualsAndHashCode(callSuper = true)
public class Invoice extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "invoice_number", nullable = false, unique = true)
    @NotNull
    @Size(max = 50)
    private String invoiceNumber;
    
    @Column(name = "issue_date", nullable = false)
    @NotNull
    private LocalDate issueDate;
    
    @Column(name = "due_date", nullable = false)
    @NotNull
    private LocalDate dueDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private InvoiceStatus status = InvoiceStatus.DRAFT;
    
    @Column(name = "subtotal", precision = 19, scale = 2, nullable = false)
    @NotNull
    private BigDecimal subtotal;
    
    @Column(name = "tax", precision = 19, scale = 2, nullable = false)
    @NotNull
    private BigDecimal tax = BigDecimal.ZERO;
    
    @Column(name = "total", precision = 19, scale = 2, nullable = false)
    @NotNull
    private BigDecimal total;
    
    @Column(nullable = false)
    private String currency = "USD";
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    @NotNull
    private User createdBy;
    
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();
    
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();
}

