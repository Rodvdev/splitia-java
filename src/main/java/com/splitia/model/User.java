package com.splitia.model;

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
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 100)
    private String name;
    
    @Column(name = "last_name", nullable = false)
    @NotNull
    @Size(min = 1, max = 100)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    @NotNull
    @Email
    private String email;
    
    @Column(nullable = false)
    @NotNull
    private String password; // Encriptado
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    private String image;
    
    @Column(nullable = false)
    private String currency = "PEN";
    
    @Column(nullable = false)
    private String language = "es";
    
    @Column(name = "external_id")
    private String externalId; // Para auth externa
    
    @Column(nullable = false)
    private String role = "USER"; // USER, ADMIN
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupUser> groupMemberships = new ArrayList<>();
    
    @OneToMany(mappedBy = "paidBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseShare> expenseShares = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Budget> budgets = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomCategory> categories = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subscription> subscriptions = new ArrayList<>();
    
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupportTicket> createdTickets = new ArrayList<>();
    
    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupportTicket> assignedTickets = new ArrayList<>();
}

