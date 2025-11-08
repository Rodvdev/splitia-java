package com.splitia.model.contact;

import com.splitia.model.BaseEntity;
import com.splitia.model.User;
import com.splitia.model.enums.ContactType;
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
@Table(name = "contacts")
@Data
@EqualsAndHashCode(callSuper = true)
public class Contact extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "first_name", nullable = false)
    @NotNull
    @Size(max = 100)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    @NotNull
    @Size(max = 100)
    private String lastName;
    
    @Column(nullable = false)
    @NotNull
    @Email
    private String email;
    
    @Column(name = "phone")
    @Size(max = 50)
    private String phone;
    
    @Column(name = "mobile")
    @Size(max = 50)
    private String mobile;
    
    @Column(name = "job_title")
    @Size(max = 100)
    private String jobTitle;
    
    @Column(name = "department")
    @Size(max = 100)
    private String department;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private ContactType type = ContactType.PROSPECT;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @ManyToMany
    @JoinTable(
        name = "contact_contact_tags",
        joinColumns = @JoinColumn(name = "contact_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<ContactTag> tags = new ArrayList<>();
}

