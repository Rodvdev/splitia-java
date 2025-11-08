package com.splitia.service;

import com.splitia.dto.response.StockResponse;
import com.splitia.mapper.ProductMapper;
import com.splitia.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockAlertService {
    
    private final StockRepository stockRepository;
    private final ProductMapper productMapper;
    private final com.splitia.service.websocket.WebSocketNotificationService webSocketNotificationService;
    
    @Transactional(readOnly = true)
    public List<StockResponse> checkLowStock() {
        return stockRepository.findLowStock().stream()
                .map(productMapper::stockToResponse)
                .collect(Collectors.toList());
    }
    
    @Scheduled(cron = "0 0 9 * * ?") // Run daily at 9 AM
    @Transactional(readOnly = true)
    public void checkAndNotifyLowStock() {
        List<com.splitia.model.inventory.Stock> lowStockItems = stockRepository.findLowStock();
        
        for (com.splitia.model.inventory.Stock stock : lowStockItems) {
            java.util.Map<String, Object> alertData = new java.util.HashMap<>();
            alertData.put("productId", stock.getProduct().getId());
            alertData.put("productName", stock.getProduct().getName());
            alertData.put("currentQuantity", stock.getQuantity());
            alertData.put("minQuantity", stock.getMinQuantity());
            
            webSocketNotificationService.notifyStockLow(
                stock.getProduct().getId(), 
                alertData, 
                null // System-generated alert
            );
        }
    }
}

