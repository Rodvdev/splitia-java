package com.splitia.controller;

import com.splitia.dto.request.CreatePurchaseOrderRequest;
import com.splitia.dto.request.CreateVendorRequest;
import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.PurchaseOrderResponse;
import com.splitia.dto.response.VendorResponse;
import com.splitia.model.enums.PurchaseOrderStatus;
import com.splitia.service.PurchaseOrderService;
import com.splitia.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/procurement")
@RequiredArgsConstructor
@Tag(name = "Procurement", description = "Procurement management endpoints")
public class ProcurementController {
    
    private final VendorService vendorService;
    private final PurchaseOrderService purchaseOrderService;
    
    // Vendors
    @GetMapping("/vendors")
    @Operation(summary = "Get all vendors")
    public ResponseEntity<ApiResponse<Page<VendorResponse>>> getAllVendors(Pageable pageable) {
        Page<VendorResponse> vendors = vendorService.getAllVendors(pageable);
        return ResponseEntity.ok(ApiResponse.success(vendors));
    }
    
    @GetMapping("/vendors/{id}")
    @Operation(summary = "Get vendor by ID")
    public ResponseEntity<ApiResponse<VendorResponse>> getVendorById(@PathVariable UUID id) {
        VendorResponse vendor = vendorService.getVendorById(id);
        return ResponseEntity.ok(ApiResponse.success(vendor));
    }
    
    @PostMapping("/vendors")
    @Operation(summary = "Create a new vendor")
    public ResponseEntity<ApiResponse<VendorResponse>> createVendor(@Valid @RequestBody CreateVendorRequest request) {
        UUID createdById = getCurrentUserId();
        VendorResponse vendor = vendorService.createVendor(request, createdById);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(vendor, "Vendor created successfully"));
    }
    
    @DeleteMapping("/vendors/{id}")
    @Operation(summary = "Delete vendor. Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteVendor(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        vendorService.deleteVendor(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Vendor permanently deleted" : "Vendor soft deleted"));
    }
    
    // Purchase Orders
    @GetMapping("/purchase-orders")
    @Operation(summary = "Get all purchase orders with filters")
    public ResponseEntity<ApiResponse<Page<PurchaseOrderResponse>>> getAllPurchaseOrders(
            Pageable pageable,
            @RequestParam(required = false) PurchaseOrderStatus status,
            @RequestParam(required = false) UUID vendorId) {
        Page<PurchaseOrderResponse> purchaseOrders = purchaseOrderService.getAllPurchaseOrders(pageable, status, vendorId);
        return ResponseEntity.ok(ApiResponse.success(purchaseOrders));
    }
    
    @GetMapping("/purchase-orders/{id}")
    @Operation(summary = "Get purchase order by ID")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> getPurchaseOrderById(@PathVariable UUID id) {
        PurchaseOrderResponse purchaseOrder = purchaseOrderService.getPurchaseOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(purchaseOrder));
    }
    
    @PostMapping("/purchase-orders")
    @Operation(summary = "Create a new purchase order")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> createPurchaseOrder(
            @Valid @RequestBody CreatePurchaseOrderRequest request) {
        UUID createdById = getCurrentUserId();
        PurchaseOrderResponse purchaseOrder = purchaseOrderService.createPurchaseOrder(request, createdById);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(purchaseOrder, "Purchase order created successfully"));
    }
    
    @PostMapping("/purchase-orders/{id}/receive")
    @Operation(summary = "Receive purchase order items")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> receivePurchaseOrder(
            @PathVariable UUID id,
            @RequestBody Map<UUID, Integer> receivedQuantities) {
        PurchaseOrderResponse purchaseOrder = purchaseOrderService.receivePurchaseOrder(id, receivedQuantities);
        return ResponseEntity.ok(ApiResponse.success(purchaseOrder, "Purchase order received successfully"));
    }
    
    @PutMapping("/purchase-orders/{id}/status")
    @Operation(summary = "Update purchase order status")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> updatePurchaseOrderStatus(
            @PathVariable UUID id,
            @RequestParam PurchaseOrderStatus status) {
        PurchaseOrderResponse purchaseOrder = purchaseOrderService.updatePurchaseOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(purchaseOrder, "Purchase order status updated successfully"));
    }
    
    @DeleteMapping("/purchase-orders/{id}")
    @Operation(summary = "Delete purchase order. Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deletePurchaseOrder(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        purchaseOrderService.deletePurchaseOrder(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Purchase order permanently deleted" : "Purchase order soft deleted"));
    }
    
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof com.splitia.security.CustomUserDetails) {
            com.splitia.security.CustomUserDetails userDetails = (com.splitia.security.CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUserId();
        }
        return null;
    }
}

