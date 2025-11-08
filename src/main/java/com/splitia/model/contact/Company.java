package com.splitia.model.contact;

import com.splitia.model.BaseEntity;
import com.splitia.model.User;
import com.splitia.model.enums.CompanySize;
import com.splitia.model.enums.Industry;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "companies")
@Data
@EqualsAndHashCode(callSuper = true)
public class Company extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;
    
    @Column(name = "legal_name")
    @Size(max = 255)
    private String legalName;
    
    @Column(name = "tax_id")
    @Size(max = 50)
    private String taxId;
    
    @Column(name = "website")
    @Size(max = 255)
    private String website;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "industry")
    private Industry industry;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private CompanySize size;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "city")
    @Size(max = 100)
    private String city;
    
    @Column(name = "country")
    @Size(max = 100)
    private String country;
    
    @Column(name = "phone")
    @Size(max = 50)
    private String phone;
    
    @Column(name = "email")
    @Email
    private String email;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Contact> contacts = new ArrayList<>();
}

