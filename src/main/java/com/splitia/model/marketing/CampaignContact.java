package com.splitia.model.marketing;

import com.splitia.model.BaseEntity;
import com.splitia.model.contact.Contact;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "campaign_contacts")
@Data
@EqualsAndHashCode(callSuper = true)
public class CampaignContact extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    @NotNull
    private Campaign campaign;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    @NotNull
    private Contact contact;
    
    @Column(name = "status", nullable = false)
    @NotNull
    private String status = "PENDING"; // PENDING, SENT, OPENED, CLICKED, CONVERTED, BOUNCED
    
    @Column(name = "sent_date")
    private LocalDateTime sentDate;
    
    @Column(name = "opened_date")
    private LocalDateTime openedDate;
    
    @Column(name = "clicked_date")
    private LocalDateTime clickedDate;
}

