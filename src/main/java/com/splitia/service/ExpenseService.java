package com.splitia.service;

import com.splitia.dto.request.CreateExpenseRequest;
import com.splitia.dto.request.ExpenseShareRequest;
import com.splitia.dto.request.UpdateExpenseRequest;
import com.splitia.dto.response.ExpenseResponse;
import com.splitia.exception.BadRequestException;
import com.splitia.exception.ForbiddenException;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.ExpenseMapper;
import com.splitia.model.Expense;
import com.splitia.model.ExpenseShare;
import com.splitia.model.Group;
import com.splitia.model.User;
import com.splitia.repository.ExpenseRepository;
import com.splitia.repository.ExpenseShareRepository;
import com.splitia.repository.GroupRepository;
import com.splitia.repository.GroupUserRepository;
import com.splitia.repository.UserRepository;
import com.splitia.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    private final ExpenseShareRepository expenseShareRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupUserRepository groupUserRepository;
    private final ExpenseMapper expenseMapper;
    
    @Transactional
    public ExpenseResponse createExpense(CreateExpenseRequest request) {
        User currentUser = getCurrentUser();
        User paidBy = userRepository.findById(request.getPaidById())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getPaidById()));
        
        Expense expense = new Expense();
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setDate(request.getDate());
        expense.setCurrency(request.getCurrency());
        expense.setLocation(request.getLocation());
        expense.setNotes(request.getNotes());
        expense.setPaidBy(paidBy);
        
        if (request.getGroupId() != null) {
            Group group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Group", "id", request.getGroupId()));
            
            // Verificar que el usuario es miembro del grupo
            if (!groupUserRepository.existsByUserIdAndGroupId(currentUser.getId(), request.getGroupId())) {
                throw new ForbiddenException("You are not a member of this group");
            }
            
            expense.setGroup(group);
        }
        
        expense = expenseRepository.save(expense);
        
        // Crear shares
        BigDecimal totalShares = request.getShares().stream()
                .map(ExpenseShareRequest::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalShares.compareTo(request.getAmount()) != 0) {
            throw new BadRequestException("Sum of shares must equal the expense amount");
        }
        
        for (ExpenseShareRequest shareRequest : request.getShares()) {
            ExpenseShare share = new ExpenseShare();
            share.setExpense(expense);
            share.setUser(userRepository.findById(shareRequest.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", shareRequest.getUserId())));
            share.setAmount(shareRequest.getAmount());
            share.setType(shareRequest.getType());
            expenseShareRepository.save(share);
        }
        
        return expenseMapper.toResponse(expense);
    }
    
    public Page<ExpenseResponse> getExpenses(UUID groupId, Pageable pageable) {
        User currentUser = getCurrentUser();
        
        if (groupId != null) {
            // Verificar membresía
            if (!groupUserRepository.existsByUserIdAndGroupId(currentUser.getId(), groupId)) {
                throw new ForbiddenException("You are not a member of this group");
            }
            return expenseRepository.findByGroupId(groupId, pageable)
                    .map(expenseMapper::toResponse);
        } else {
            return expenseRepository.findByUserId(currentUser.getId(), pageable)
                    .map(expenseMapper::toResponse);
        }
    }
    
    public ExpenseResponse getExpenseById(UUID expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        
        User currentUser = getCurrentUser();
        
        // Verificar acceso
        if (expense.getGroup() != null) {
            if (!groupUserRepository.existsByUserIdAndGroupId(currentUser.getId(), expense.getGroup().getId())) {
                throw new ForbiddenException("You do not have access to this expense");
            }
        } else if (!expense.getPaidBy().getId().equals(currentUser.getId())) {
            // Verificar si el usuario tiene un share
            boolean hasShare = expense.getShares().stream()
                    .anyMatch(share -> share.getUser().getId().equals(currentUser.getId()));
            if (!hasShare) {
                throw new ForbiddenException("You do not have access to this expense");
            }
        }
        
        return expenseMapper.toResponse(expense);
    }
    
    @Transactional
    public ExpenseResponse updateExpense(UUID expenseId, UpdateExpenseRequest request) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        
        User currentUser = getCurrentUser();
        
        // Verificar permisos
        boolean isPaidBy = expense.getPaidBy().getId().equals(currentUser.getId());
        boolean isAdmin = expense.getGroup() != null &&
                groupUserRepository.findByUserIdAndGroupId(currentUser.getId(), expense.getGroup().getId())
                        .map(groupUser -> groupUser.getRole().name().equals("ADMIN"))
                        .orElse(false);
        
        if (!isPaidBy && !isAdmin) {
            throw new ForbiddenException("You do not have permission to update this expense");
        }
        
        if (request.getAmount() != null) {
            expense.setAmount(request.getAmount());
        }
        if (request.getDescription() != null) {
            expense.setDescription(request.getDescription());
        }
        if (request.getDate() != null) {
            expense.setDate(request.getDate());
        }
        if (request.getCurrency() != null) {
            expense.setCurrency(request.getCurrency());
        }
        if (request.getLocation() != null) {
            expense.setLocation(request.getLocation());
        }
        if (request.getNotes() != null) {
            expense.setNotes(request.getNotes());
        }
        
        expense = expenseRepository.save(expense);
        
        // Actualizar shares si se proporcionan
        if (request.getShares() != null && !request.getShares().isEmpty()) {
            expenseShareRepository.deleteByExpenseId(expenseId);
            
            BigDecimal totalShares = request.getShares().stream()
                    .map(ExpenseShareRequest::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            if (totalShares.compareTo(expense.getAmount()) != 0) {
                throw new BadRequestException("Sum of shares must equal the expense amount");
            }
            
            for (ExpenseShareRequest shareRequest : request.getShares()) {
                ExpenseShare share = new ExpenseShare();
                share.setExpense(expense);
                share.setUser(userRepository.findById(shareRequest.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", shareRequest.getUserId())));
                share.setAmount(shareRequest.getAmount());
                share.setType(shareRequest.getType());
                expenseShareRepository.save(share);
            }
        }
        
        return expenseMapper.toResponse(expense);
    }
    
    @Transactional
    public void deleteExpense(UUID expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        
        User currentUser = getCurrentUser();
        
        boolean isPaidBy = expense.getPaidBy().getId().equals(currentUser.getId());
        boolean isAdmin = expense.getGroup() != null &&
                groupUserRepository.findByUserIdAndGroupId(currentUser.getId(), expense.getGroup().getId())
                        .map(groupUser -> groupUser.getRole().name().equals("ADMIN"))
                        .orElse(false);
        
        if (!isPaidBy && !isAdmin) {
            throw new ForbiddenException("You do not have permission to delete this expense");
        }
        
        expenseRepository.delete(expense);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        return userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));
    }
}

