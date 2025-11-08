package com.splitia.service;

import com.splitia.dto.response.OpportunityResponse;
import com.splitia.mapper.OpportunityMapper;
import com.splitia.model.enums.OpportunityStage;
import com.splitia.model.sales.Opportunity;
import com.splitia.repository.OpportunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesForecastService {
    
    private final OpportunityRepository opportunityRepository;
    private final OpportunityMapper opportunityMapper;
    
    @Transactional(readOnly = true)
    public Map<OpportunityStage, List<OpportunityResponse>> getPipelineView() {
        Map<OpportunityStage, List<OpportunityResponse>> pipeline = new LinkedHashMap<>();
        
        // Initialize all stages
        for (OpportunityStage stage : OpportunityStage.values()) {
            List<Opportunity> opportunities = opportunityRepository.findAllByStage(stage);
            List<OpportunityResponse> responses = opportunities.stream()
                    .map(opportunityMapper::toResponse)
                    .collect(Collectors.toList());
            pipeline.put(stage, responses);
        }
        
        return pipeline;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getForecasting(String period, String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now();
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : calculateEndDate(start, period);
        
        List<Opportunity> opportunities = opportunityRepository.findAll()
                .stream()
                .filter(o -> o.getDeletedAt() == null)
                .filter(o -> o.getExpectedCloseDate() != null)
                .filter(o -> !o.getExpectedCloseDate().isBefore(start))
                .filter(o -> !o.getExpectedCloseDate().isAfter(end))
                .collect(Collectors.toList());
        
        // Calculate weighted revenue (estimatedValue * probability / 100)
        BigDecimal totalWeightedRevenue = opportunities.stream()
                .filter(o -> o.getEstimatedValue() != null)
                .map(o -> o.getEstimatedValue()
                        .multiply(BigDecimal.valueOf(o.getProbability()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Group by period
        Map<String, BigDecimal> revenueByPeriod = new LinkedHashMap<>();
        DateTimeFormatter formatter = getFormatterForPeriod(period);
        
        for (Opportunity opp : opportunities) {
            if (opp.getEstimatedValue() != null) {
                String periodKey = opp.getExpectedCloseDate().format(formatter);
                BigDecimal weightedValue = opp.getEstimatedValue()
                        .multiply(BigDecimal.valueOf(opp.getProbability()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                
                revenueByPeriod.merge(periodKey, weightedValue, BigDecimal::add);
            }
        }
        
        result.put("totalWeightedRevenue", totalWeightedRevenue);
        result.put("revenueByPeriod", revenueByPeriod);
        result.put("opportunityCount", opportunities.size());
        result.put("period", period);
        result.put("startDate", start.toString());
        result.put("endDate", end.toString());
        
        return result;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        List<Opportunity> allOpportunities = opportunityRepository.findAll()
                .stream()
                .filter(o -> o.getDeletedAt() == null)
                .collect(Collectors.toList());
        
        // Conversion rate (leads to qualified)
        long totalLeads = allOpportunities.stream()
                .filter(o -> o.getStage() == OpportunityStage.LEAD)
                .count();
        long qualified = allOpportunities.stream()
                .filter(o -> o.getStage() == OpportunityStage.QUALIFIED || 
                           o.getStage() == OpportunityStage.PROPOSAL ||
                           o.getStage() == OpportunityStage.NEGOTIATION ||
                           o.getStage() == OpportunityStage.CLOSED_WON)
                .count();
        double conversionRate = totalLeads > 0 ? (double) qualified / totalLeads * 100 : 0;
        
        // Average deal size
        BigDecimal totalValue = allOpportunities.stream()
                .filter(o -> o.getEstimatedValue() != null)
                .map(Opportunity::getEstimatedValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgDealSize = allOpportunities.stream()
                .filter(o -> o.getEstimatedValue() != null)
                .count() > 0 ?
                totalValue.divide(BigDecimal.valueOf(allOpportunities.stream()
                        .filter(o -> o.getEstimatedValue() != null)
                        .count()), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        // Win rate (closed won / total closed)
        long closedWon = allOpportunities.stream()
                .filter(o -> o.getStage() == OpportunityStage.CLOSED_WON)
                .count();
        long closedLost = allOpportunities.stream()
                .filter(o -> o.getStage() == OpportunityStage.CLOSED_LOST)
                .count();
        long totalClosed = closedWon + closedLost;
        double winRate = totalClosed > 0 ? (double) closedWon / totalClosed * 100 : 0;
        
        // Sales cycle length (average days from creation to close)
        double avgSalesCycle = allOpportunities.stream()
                .filter(o -> o.getStage() == OpportunityStage.CLOSED_WON && o.getActualCloseDate() != null)
                .mapToLong(o -> {
                    if (o.getCreatedAt() != null && o.getActualCloseDate() != null) {
                        return java.time.temporal.ChronoUnit.DAYS.between(
                                o.getCreatedAt().toLocalDate(),
                                o.getActualCloseDate()
                        );
                    }
                    return 0;
                })
                .average()
                .orElse(0);
        
        metrics.put("conversionRate", Math.round(conversionRate * 100.0) / 100.0);
        metrics.put("averageDealSize", avgDealSize);
        metrics.put("winRate", Math.round(winRate * 100.0) / 100.0);
        metrics.put("averageSalesCycleDays", Math.round(avgSalesCycle));
        metrics.put("totalOpportunities", allOpportunities.size());
        metrics.put("closedWon", closedWon);
        metrics.put("closedLost", closedLost);
        
        return metrics;
    }
    
    private LocalDate calculateEndDate(LocalDate start, String period) {
        switch (period.toUpperCase()) {
            case "MONTH":
                return start.plusMonths(1);
            case "QUARTER":
                return start.plusMonths(3);
            case "YEAR":
                return start.plusYears(1);
            default:
                return start.plusMonths(1);
        }
    }
    
    private DateTimeFormatter getFormatterForPeriod(String period) {
        switch (period.toUpperCase()) {
            case "MONTH":
                return DateTimeFormatter.ofPattern("yyyy-MM");
            case "QUARTER":
                return DateTimeFormatter.ofPattern("yyyy-Q");
            case "YEAR":
                return DateTimeFormatter.ofPattern("yyyy");
            default:
                return DateTimeFormatter.ofPattern("yyyy-MM");
        }
    }
}

