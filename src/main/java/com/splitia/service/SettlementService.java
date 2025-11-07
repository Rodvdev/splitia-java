package com.splitia.service;

import com.splitia.dto.request.CreateSettlementRequest;
import com.splitia.dto.response.SettlementResponse;
import com.splitia.mapper.SettlementMapper;
import com.splitia.model.Settlement;
import com.splitia.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementService {
    
    private final SettlementRepository settlementRepository;
    private final SettlementMapper settlementMapper;
    
    @Transactional
    public SettlementResponse createSettlement(CreateSettlementRequest request) {
        // Implementation placeholder
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public SettlementResponse getSettlementById(UUID settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new RuntimeException("Settlement not found"));
        return settlementMapper.toResponse(settlement);
    }
}

