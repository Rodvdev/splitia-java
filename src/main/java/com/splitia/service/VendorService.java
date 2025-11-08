package com.splitia.service;

import com.splitia.dto.request.CreatePurchaseOrderRequest;
import com.splitia.dto.request.CreateVendorRequest;
import com.splitia.dto.response.PurchaseOrderResponse;
import com.splitia.dto.response.VendorResponse;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.PurchaseOrderMapper;
import com.splitia.mapper.VendorMapper;
import com.splitia.model.User;
import com.splitia.model.enums.PurchaseOrderStatus;
import com.splitia.model.enums.StockMovementType;
import com.splitia.model.inventory.Product;
import com.splitia.model.inventory.Stock;
import com.splitia.model.inventory.StockMovement;
import com.splitia.model.procurement.PurchaseOrder;
import com.splitia.model.procurement.PurchaseOrderItem;
import com.splitia.model.procurement.Vendor;
import com.splitia.repository.*;
import com.splitia.service.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VendorService {
    
    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final VendorMapper vendorMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    
    @Transactional(readOnly = true)
    public Page<VendorResponse> getAllVendors(Pageable pageable) {
        return vendorRepository.findAllActive(pageable)
                .map(vendorMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public VendorResponse getVendorById(UUID id) {
        Vendor vendor = vendorRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));
        return vendorMapper.toResponse(vendor);
    }
    
    @Transactional
    public VendorResponse createVendor(CreateVendorRequest request, UUID createdById) {
        User createdBy = userRepository.findByIdAndDeletedAtIsNull(createdById)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", createdById));
        
        Vendor vendor = new Vendor();
        vendor.setName(request.getName());
        vendor.setContactName(request.getContactName());
        vendor.setEmail(request.getEmail());
        vendor.setPhoneNumber(request.getPhoneNumber());
        vendor.setTaxId(request.getTaxId());
        vendor.setAddress(request.getAddress());
        vendor.setCity(request.getCity());
        vendor.setCountry(request.getCountry());
        vendor.setPaymentTerms(request.getPaymentTerms());
        vendor.setRating(request.getRating());
        vendor.setNotes(request.getNotes());
        vendor.setCreatedBy(createdBy);
        
        vendor = vendorRepository.save(vendor);
        VendorResponse response = vendorMapper.toResponse(vendor);
        
        Map<String, Object> data = new HashMap<>();
        data.put("vendor", response);
        webSocketNotificationService.notifyVendorCreated(vendor.getId(), data, createdById);
        
        return response;
    }
    
    @Transactional
    public void deleteVendor(UUID id, boolean hardDelete) {
        if (hardDelete) {
            vendorRepository.deleteById(id);
        } else {
            Vendor vendor = vendorRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));
            vendor.setDeletedAt(LocalDateTime.now());
            vendorRepository.save(vendor);
        }
    }
    
    private UUID getCurrentUserId() {
        try {
            org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof com.splitia.security.CustomUserDetails) {
                com.splitia.security.CustomUserDetails userDetails = 
                    (com.splitia.security.CustomUserDetails) authentication.getPrincipal();
                return userDetails.getUserId();
            }
        } catch (Exception e) {
        }
        return null;
    }
}

