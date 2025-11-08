package com.splitia.service;

import com.splitia.dto.response.InvoiceResponse;
import com.splitia.model.enums.AccountType;
import com.splitia.model.finance.Account;
import com.splitia.model.finance.JournalEntry;
import com.splitia.model.finance.JournalTransaction;
import com.splitia.repository.AccountRepository;
import com.splitia.repository.InvoiceRepository;
import com.splitia.repository.JournalEntryRepository;
import com.splitia.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FinanceReportService {
    
    private final AccountRepository accountRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final JournalEntryRepository journalEntryRepository;
    
    @Transactional(readOnly = true)
    public Map<String, Object> getBalanceSheet(LocalDate asOfDate) {
        if (asOfDate == null) {
            asOfDate = LocalDate.now();
        }
        
        Map<String, Object> balanceSheet = new HashMap<>();
        
        // Assets
        List<Account> assets = accountRepository.findByType(AccountType.ASSET);
        BigDecimal totalAssets = calculateAccountBalance(assets, asOfDate);
        
        // Liabilities
        List<Account> liabilities = accountRepository.findByType(AccountType.LIABILITY);
        BigDecimal totalLiabilities = calculateAccountBalance(liabilities, asOfDate);
        
        // Equity
        List<Account> equity = accountRepository.findByType(AccountType.EQUITY);
        BigDecimal totalEquity = calculateAccountBalance(equity, asOfDate);
        
        balanceSheet.put("asOfDate", asOfDate.toString());
        balanceSheet.put("assets", totalAssets);
        balanceSheet.put("liabilities", totalLiabilities);
        balanceSheet.put("equity", totalEquity);
        balanceSheet.put("totalLiabilitiesAndEquity", totalLiabilities.add(totalEquity));
        
        return balanceSheet;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getIncomeStatement(LocalDate startDate, LocalDate endDate) {
        final LocalDate finalStartDate = startDate != null ? startDate : LocalDate.now().withDayOfYear(1); // Start of year
        final LocalDate finalEndDate = endDate != null ? endDate : LocalDate.now();
        
        Map<String, Object> incomeStatement = new HashMap<>();
        
        // Revenue
        List<Account> revenue = accountRepository.findByType(AccountType.REVENUE);
        BigDecimal totalRevenue = calculateAccountBalanceForPeriod(revenue, finalStartDate, finalEndDate);
        
        // Expenses
        List<Account> expenses = accountRepository.findByType(AccountType.EXPENSE);
        BigDecimal totalExpenses = calculateAccountBalanceForPeriod(expenses, finalStartDate, finalEndDate);
        
        BigDecimal netIncome = totalRevenue.subtract(totalExpenses);
        
        incomeStatement.put("startDate", finalStartDate.toString());
        incomeStatement.put("endDate", finalEndDate.toString());
        incomeStatement.put("revenue", totalRevenue);
        incomeStatement.put("expenses", totalExpenses);
        incomeStatement.put("netIncome", netIncome);
        
        return incomeStatement;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getCashFlow(LocalDate startDate, LocalDate endDate) {
        final LocalDate finalStartDate = startDate != null ? startDate : LocalDate.now().withDayOfYear(1);
        final LocalDate finalEndDate = endDate != null ? endDate : LocalDate.now();
        
        Map<String, Object> cashFlow = new HashMap<>();
        
        // Cash from operations (payments received)
        BigDecimal cashFromOperations = paymentRepository.findAll()
                .stream()
                .filter(p -> p.getDeletedAt() == null)
                .filter(p -> !p.getDate().isBefore(finalStartDate) && !p.getDate().isAfter(finalEndDate))
                .map(p -> p.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Cash from sales (invoices paid)
        BigDecimal cashFromSales = invoiceRepository.findAll()
                .stream()
                .filter(i -> i.getDeletedAt() == null)
                .filter(i -> i.getStatus() == com.splitia.model.enums.InvoiceStatus.PAID)
                .filter(i -> i.getIssueDate() != null && !i.getIssueDate().isBefore(finalStartDate) && !i.getIssueDate().isAfter(finalEndDate))
                .map(i -> i.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        cashFlow.put("startDate", finalStartDate.toString());
        cashFlow.put("endDate", finalEndDate.toString());
        cashFlow.put("cashFromOperations", cashFromOperations);
        cashFlow.put("cashFromSales", cashFromSales);
        cashFlow.put("netCashFlow", cashFromOperations.add(cashFromSales));
        
        return cashFlow;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getProfitabilityAnalysis(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> analysis = new HashMap<>();
        
        Map<String, Object> incomeStatement = getIncomeStatement(startDate, endDate);
        BigDecimal revenue = (BigDecimal) incomeStatement.get("revenue");
        BigDecimal expenses = (BigDecimal) incomeStatement.get("expenses");
        BigDecimal netIncome = (BigDecimal) incomeStatement.get("netIncome");
        
        // Profit margin
        BigDecimal profitMargin = revenue.compareTo(BigDecimal.ZERO) > 0 ?
                netIncome.divide(revenue, 4, java.math.RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) :
                BigDecimal.ZERO;
        
        analysis.put("revenue", revenue);
        analysis.put("expenses", expenses);
        analysis.put("netIncome", netIncome);
        analysis.put("profitMargin", profitMargin);
        analysis.put("startDate", startDate.toString());
        analysis.put("endDate", endDate.toString());
        
        return analysis;
    }
    
    private BigDecimal calculateAccountBalance(List<Account> accounts, LocalDate asOfDate) {
        // This would need to calculate balance from journal transactions
        // For now, return sum of account balances
        return accounts.stream()
                .filter(a -> a.getDeletedAt() == null && a.getIsActive())
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal calculateAccountBalanceForPeriod(List<Account> accounts, LocalDate startDate, LocalDate endDate) {
        // This would need to calculate balance from journal transactions for the period
        // For now, return sum of account balances
        return accounts.stream()
                .filter(a -> a.getDeletedAt() == null && a.getIsActive())
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

