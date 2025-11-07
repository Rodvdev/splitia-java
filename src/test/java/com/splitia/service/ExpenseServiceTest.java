package com.splitia.service;

import com.splitia.dto.request.CreateExpenseRequest;
import com.splitia.dto.request.ExpenseShareRequest;
import com.splitia.mapper.ExpenseMapper;
import com.splitia.model.Expense;
import com.splitia.model.User;
import com.splitia.repository.ExpenseRepository;
import com.splitia.repository.ExpenseShareRepository;
import com.splitia.repository.GroupUserRepository;
import com.splitia.repository.UserRepository;
import com.splitia.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {
    
    @Mock
    private ExpenseRepository expenseRepository;
    
    @Mock
    private ExpenseShareRepository expenseShareRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private GroupUserRepository groupUserRepository;
    
    @Mock
    private ExpenseMapper expenseMapper;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;
    
    @InjectMocks
    private ExpenseService expenseService;
    
    private User currentUser;
    private CreateExpenseRequest createExpenseRequest;
    
    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(UUID.randomUUID());
        currentUser.setEmail("test@example.com");
        
        createExpenseRequest = new CreateExpenseRequest();
        createExpenseRequest.setAmount(new BigDecimal("100.00"));
        createExpenseRequest.setDescription("Test Expense");
        createExpenseRequest.setDate(LocalDateTime.now());
        createExpenseRequest.setPaidById(currentUser.getId());
        
        ExpenseShareRequest shareRequest = new ExpenseShareRequest();
        shareRequest.setUserId(currentUser.getId());
        shareRequest.setAmount(new BigDecimal("100.00"));
        
        List<ExpenseShareRequest> shares = new ArrayList<>();
        shares.add(shareRequest);
        createExpenseRequest.setShares(shares);
        
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        CustomUserDetails userDetails = new CustomUserDetails(currentUser);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.of(currentUser));
    }
    
    @Test
    void testCreateExpense_Success() {
        // Given
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> {
            Expense expense = invocation.getArgument(0);
            expense.setId(UUID.randomUUID());
            return expense;
        });
        
        // When & Then
        assertDoesNotThrow(() -> expenseService.createExpense(createExpenseRequest));
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }
}

