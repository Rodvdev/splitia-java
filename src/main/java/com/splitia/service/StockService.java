package com.splitia.service;

import com.splitia.dto.request.CreateStockMovementRequest;
import com.splitia.dto.response.StockMovementResponse;
import com.splitia.dto.response.StockResponse;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.ProductMapper;
import com.splitia.mapper.StockMovementMapper;
import com.splitia.model.enums.StockMovementType;
import com.splitia.model.inventory.Product;
import com.splitia.model.inventory.Stock;
import com.splitia.model.inventory.StockMovement;
import com.splitia.repository.*;
import com.splitia.service.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {
    
    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final StockMovementMapper stockMovementMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    
    @Transactional(readOnly = true)
    public StockResponse getStockByProductId(UUID productId) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock", "productId", productId));
        return productMapper.stockToResponse(stock);
    }
    
    @Transactional(readOnly = true)
    public List<StockResponse> getLowStock() {
        return stockRepository.findLowStock().stream()
                .map(productMapper::stockToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<StockMovementResponse> getStockMovements(Pageable pageable, UUID productId) {
        if (productId != null) {
            return stockMovementRepository.findByProductId(productId, pageable)
                    .map(stockMovementMapper::toResponse);
        }
        return stockMovementRepository.findAll(pageable)
                .map(stockMovementMapper::toResponse);
    }
    
    @Transactional
    public StockMovementResponse createStockMovement(CreateStockMovementRequest request) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));
        
        Stock stock = stockRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock", "productId", request.getProductId()));
        
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setType(request.getType());
        movement.setQuantity(request.getQuantity());
        movement.setReason(request.getReason());
        movement.setReference(request.getReference());
        movement.setDate(request.getDate());
        
        movement = stockMovementRepository.save(movement);
        
        // Update stock quantity based on movement type
        // Note: The database trigger handles this, but we update here for consistency
        int newQuantity = stock.getQuantity();
        switch (request.getType()) {
            case IN:
            case RETURN:
                newQuantity += request.getQuantity();
                break;
            case OUT:
                newQuantity -= request.getQuantity();
                if (newQuantity < 0) {
                    throw new com.splitia.exception.BadRequestException("Insufficient stock. Available: " + stock.getQuantity());
                }
                break;
            case ADJUSTMENT:
                newQuantity = request.getQuantity();
                break;
        }
        stock.setQuantity(newQuantity);
        stockRepository.save(stock);
        
        StockMovementResponse response = stockMovementMapper.toResponse(movement);
        
        // Check for low stock alert
        if (stock.getQuantity() <= stock.getMinQuantity()) {
            Map<String, Object> alertData = new HashMap<>();
            alertData.put("productId", product.getId());
            alertData.put("productName", product.getName());
            alertData.put("currentQuantity", stock.getQuantity());
            alertData.put("minQuantity", stock.getMinQuantity());
            webSocketNotificationService.notifyStockLow(product.getId(), alertData, getCurrentUserId());
        }
        
        // Emit WebSocket event
        Map<String, Object> data = new HashMap<>();
        data.put("movement", response);
        data.put("stock", productMapper.stockToResponse(stock));
        webSocketNotificationService.notifyStockMovement(movement.getId(), data, getCurrentUserId());
        
        return response;
    }
    
    @Transactional
    public void deleteStockMovement(UUID id, boolean hardDelete) {
        if (hardDelete) {
            stockMovementRepository.deleteById(id);
        } else {
            StockMovement movement = stockMovementRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("StockMovement", "id", id));
            movement.setDeletedAt(LocalDateTime.now());
            stockMovementRepository.save(movement);
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
            // If not authenticated, return null
        }
        return null;
    }
}

