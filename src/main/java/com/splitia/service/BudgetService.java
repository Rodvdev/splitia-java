package com.splitia.service;

import com.splitia.dto.request.CreateBudgetRequest;
import com.splitia.dto.request.UpdateBudgetRequest;
import com.splitia.dto.response.BudgetResponse;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.BudgetMapper;
import com.splitia.model.Budget;
import com.splitia.model.User;
import com.splitia.repository.BudgetRepository;
import com.splitia.repository.UserRepository;
import com.splitia.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetService {
    
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final BudgetMapper budgetMapper;
    
    @Transactional
    public BudgetResponse createBudget(CreateBudgetRequest request) {
        User currentUser = getCurrentUser();
        
        Budget budget = new Budget();
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        budget.setCurrency(request.getCurrency());
        budget.setUser(currentUser);
        
        budget = budgetRepository.save(budget);
        return budgetMapper.toResponse(budget);
    }
    
    public BudgetResponse getBudgetById(UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        return budgetMapper.toResponse(budget);
    }
    
    @Transactional
    public BudgetResponse updateBudget(UUID budgetId, UpdateBudgetRequest request) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        
        if (request.getAmount() != null) {
            budget.setAmount(request.getAmount());
        }
        if (request.getCurrency() != null) {
            budget.setCurrency(request.getCurrency());
        }
        
        budget = budgetRepository.save(budget);
        return budgetMapper.toResponse(budget);
    }
    
    @Transactional
    public void deleteBudget(UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        budgetRepository.delete(budget);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        return userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));
    }
}

