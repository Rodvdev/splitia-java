package com.splitia.controller;

import com.splitia.dto.request.CreateInvoiceRequest;
import com.splitia.dto.request.CreatePaymentRequest;
import com.splitia.dto.request.UpdateInvoiceRequest;
import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.InvoiceResponse;
import com.splitia.dto.response.PaymentResponse;
import com.splitia.model.enums.InvoiceStatus;
import com.splitia.service.FinanceReportService;
import com.splitia.service.InvoiceService;
import com.splitia.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/finance")
@RequiredArgsConstructor
@Tag(name = "Finance", description = "Finance management endpoints")
public class FinanceController {
    
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final FinanceReportService financeReportService;
    
    // Invoices
    @GetMapping("/invoices")
    @Operation(summary = "Get all invoices with filters")
    public ResponseEntity<ApiResponse<Page<InvoiceResponse>>> getAllInvoices(
            Pageable pageable,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) UUID contactId,
            @RequestParam(required = false) UUID companyId) {
        Page<InvoiceResponse> invoices = invoiceService.getAllInvoices(pageable, status, contactId, companyId);
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }
    
    @GetMapping("/invoices/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceById(@PathVariable UUID id) {
        InvoiceResponse invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.success(invoice));
    }
    
    @PostMapping("/invoices")
    @Operation(summary = "Create a new invoice")
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        UUID createdById = getCurrentUserId();
        InvoiceResponse invoice = invoiceService.createInvoice(request, createdById);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(invoice, "Invoice created successfully"));
    }
    
    @PutMapping("/invoices/{id}")
    @Operation(summary = "Update invoice")
    public ResponseEntity<ApiResponse<InvoiceResponse>> updateInvoice(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateInvoiceRequest request) {
        InvoiceResponse invoice = invoiceService.updateInvoice(id, request);
        return ResponseEntity.ok(ApiResponse.success(invoice, "Invoice updated successfully"));
    }
    
    @PostMapping("/invoices/{id}/payments")
    @Operation(summary = "Add payment to invoice")
    public ResponseEntity<ApiResponse<PaymentResponse>> addPaymentToInvoice(
            @PathVariable UUID id,
            @Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse payment = invoiceService.addPayment(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(payment, "Payment added successfully"));
    }
    
    @DeleteMapping("/invoices/{id}")
    @Operation(summary = "Delete invoice. Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteInvoice(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        invoiceService.deleteInvoice(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Invoice permanently deleted" : "Invoice soft deleted"));
    }
    
    // Payments
    @GetMapping("/payments")
    @Operation(summary = "Get all payments")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getAllPayments(
            Pageable pageable,
            @RequestParam(required = false) UUID invoiceId) {
        Page<PaymentResponse> payments = paymentService.getAllPayments(pageable, invoiceId);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }
    
    @GetMapping("/payments/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable UUID id) {
        PaymentResponse payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }
    
    @PostMapping("/payments")
    @Operation(summary = "Create a new payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse payment = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(payment, "Payment created successfully"));
    }
    
    @PutMapping("/payments/{id}/reconcile")
    @Operation(summary = "Reconcile payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> reconcilePayment(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "true") Boolean isReconciled) {
        PaymentResponse payment = paymentService.reconcilePayment(id, isReconciled);
        return ResponseEntity.ok(ApiResponse.success(payment, "Payment reconciled successfully"));
    }
    
    @DeleteMapping("/payments/{id}")
    @Operation(summary = "Delete payment. Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deletePayment(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        paymentService.deletePayment(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Payment permanently deleted" : "Payment soft deleted"));
    }
    
    // Reports
    @GetMapping("/reports/balance-sheet")
    @Operation(summary = "Get balance sheet")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBalanceSheet(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        Map<String, Object> balanceSheet = financeReportService.getBalanceSheet(asOfDate);
        return ResponseEntity.ok(ApiResponse.success(balanceSheet));
    }
    
    @GetMapping("/reports/income-statement")
    @Operation(summary = "Get income statement")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getIncomeStatement(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> incomeStatement = financeReportService.getIncomeStatement(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(incomeStatement));
    }
    
    @GetMapping("/reports/cash-flow")
    @Operation(summary = "Get cash flow statement")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCashFlow(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> cashFlow = financeReportService.getCashFlow(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(cashFlow));
    }
    
    @GetMapping("/reports/profitability")
    @Operation(summary = "Get profitability analysis")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfitabilityAnalysis(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> analysis = financeReportService.getProfitabilityAnalysis(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(analysis));
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

