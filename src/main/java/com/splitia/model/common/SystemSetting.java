package com.splitia.model.common;

import com.splitia.model.BaseEntity;
import com.splitia.model.enums.SettingType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Table(name = "system_settings")
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemSetting extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    @NotNull
    @Size(max = 100)
    private String key;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotNull
    private String value;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private SettingType type;
    
    @Column(columnDefinition = "TEXT")
    private String description;
}

