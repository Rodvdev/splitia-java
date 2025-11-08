package com.splitia.model.contact;

import com.splitia.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Table(name = "contact_tags")
@Data
@EqualsAndHashCode(callSuper = true)
public class ContactTag extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    @NotNull
    @Size(max = 50)
    private String name;
    
    @Column(name = "color")
    @Size(max = 20)
    private String color;
}

