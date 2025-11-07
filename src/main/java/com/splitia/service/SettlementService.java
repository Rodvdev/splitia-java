package com.splitia.service;

import com.splitia.dto.request.CreateSettlementRequest;
import com.splitia.dto.request.UpdateSettlementRequest;
import com.splitia.dto.response.SettlementResponse;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.SettlementMapper;
import com.splitia.model.Group;
import com.splitia.model.Settlement;
import com.splitia.model.User;
import com.splitia.repository.GroupRepository;
import com.splitia.repository.SettlementRepository;
import com.splitia.repository.UserRepository;
import com.splitia.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementService {
    
    private final SettlementRepository settlementRepository;
    private final SettlementMapper settlementMapper;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    
    public Page<SettlementResponse> getSettlements(UUID groupId, Pageable pageable) {
        User currentUser = getCurrentUser();
        // Users can see settlements for groups they belong to
        return settlementRepository.findByGroupId(groupId, pageable)
                .map(settlementMapper::toResponse);
    }
    
    @Transactional
    public SettlementResponse createSettlement(CreateSettlementRequest request) {
        User currentUser = getCurrentUser();
        User settledWithUser = userRepository.findByIdAndDeletedAtIsNull(request.getSettledWithUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getSettledWithUserId()));
        Group group = groupRepository.findByIdAndDeletedAtIsNull(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", request.getGroupId()));
        
        Settlement settlement = new Settlement();
        settlement.setAmount(request.getAmount());
        settlement.setCurrency(request.getCurrency());
        settlement.setDescription(request.getDescription());
        settlement.setDate(request.getDate());
        settlement.setType(request.getType());
        settlement.setInitiatedBy(currentUser);
        settlement.setSettledWithUser(settledWithUser);
        settlement.setGroup(group);
        
        settlement = settlementRepository.save(settlement);
        return settlementMapper.toResponse(settlement);
    }
    
    public SettlementResponse getSettlementById(UUID settlementId) {
        Settlement settlement = settlementRepository.findByIdAndDeletedAtIsNull(settlementId)
                .orElseThrow(() -> new ResourceNotFoundException("Settlement", "id", settlementId));
        return settlementMapper.toResponse(settlement);
    }
    
    @Transactional
    public SettlementResponse updateSettlement(UUID settlementId, UpdateSettlementRequest request) {
        // Only admins can update settlements
        Settlement settlement = settlementRepository.findByIdAndDeletedAtIsNull(settlementId)
                .orElseThrow(() -> new ResourceNotFoundException("Settlement", "id", settlementId));
        
        if (request.getAmount() != null) {
            settlement.setAmount(request.getAmount());
        }
        if (request.getDescription() != null) {
            settlement.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            settlement.setStatus(request.getStatus());
        }
        
        settlement = settlementRepository.save(settlement);
        return settlementMapper.toResponse(settlement);
    }
    
    @Transactional
    public void softDeleteSettlement(UUID settlementId) {
        // Only admins can delete settlements
        Settlement settlement = settlementRepository.findByIdAndDeletedAtIsNull(settlementId)
                .orElseThrow(() -> new ResourceNotFoundException("Settlement", "id", settlementId));
        
        settlement.setDeletedAt(LocalDateTime.now());
        settlementRepository.save(settlement);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        return userRepository.findByIdAndDeletedAtIsNull(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));
    }
}

