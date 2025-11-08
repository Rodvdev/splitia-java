package com.splitia.service;

import com.splitia.dto.request.CreatePaymentRequest;
import com.splitia.dto.response.PaymentResponse;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.PaymentMapper;
import com.splitia.model.finance.Invoice;
import com.splitia.model.finance.Payment;
import com.splitia.repository.InvoiceRepository;
import com.splitia.repository.PaymentRepository;
import com.splitia.service.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentMapper paymentMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getAllPayments(Pageable pageable, UUID invoiceId) {
        if (invoiceId != null) {
            return paymentRepository.findByInvoiceId(invoiceId, pageable)
                    .map(paymentMapper::toResponse);
        }
        return paymentRepository.findAllActive(pageable)
                .map(paymentMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID id) {
        Payment payment = paymentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return paymentMapper.toResponse(payment);
    }
    
    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setDate(request.getDate());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setReference(request.getReference());
        payment.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        payment.setNotes(request.getNotes());
        
        if (request.getInvoiceId() != null) {
            Invoice invoice = invoiceRepository.findByIdAndDeletedAtIsNull(request.getInvoiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", request.getInvoiceId()));
            payment.setInvoice(invoice);
            
            // Update invoice status if fully paid
            BigDecimal totalPaid = paymentRepository.getTotalPaidForInvoice(request.getInvoiceId())
                    .add(request.getAmount());
            if (totalPaid.compareTo(invoice.getTotal()) >= 0) {
                invoice.setStatus(com.splitia.model.enums.InvoiceStatus.PAID);
                invoiceRepository.save(invoice);
            }
        }
        
        payment = paymentRepository.save(payment);
        PaymentResponse response = paymentMapper.toResponse(payment);
        
        // Emit WebSocket event
        Map<String, Object> data = new HashMap<>();
        data.put("payment", response);
        webSocketNotificationService.notifyPaymentReceived(payment.getId(), data, getCurrentUserId());
        
        return response;
    }
    
    @Transactional
    public PaymentResponse reconcilePayment(UUID id, Boolean isReconciled) {
        Payment payment = paymentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        
        payment.setIsReconciled(isReconciled != null ? isReconciled : true);
        payment = paymentRepository.save(payment);
        
        return paymentMapper.toResponse(payment);
    }
    
    @Transactional
    public void deletePayment(UUID id, boolean hardDelete) {
        if (hardDelete) {
            paymentRepository.deleteById(id);
        } else {
            Payment payment = paymentRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
            payment.setDeletedAt(LocalDateTime.now());
            paymentRepository.save(payment);
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

