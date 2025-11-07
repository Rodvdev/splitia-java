package com.splitia.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class CurrencyConverter {
    
    // Tasas de cambio aproximadas (en producción, usar un servicio de API)
    private static final Map<String, BigDecimal> EXCHANGE_RATES = new HashMap<>();
    
    static {
        EXCHANGE_RATES.put("USD", BigDecimal.ONE);
        EXCHANGE_RATES.put("EUR", new BigDecimal("0.85"));
        EXCHANGE_RATES.put("PEN", new BigDecimal("3.7"));
        EXCHANGE_RATES.put("GBP", new BigDecimal("0.73"));
        EXCHANGE_RATES.put("JPY", new BigDecimal("110.0"));
    }
    
    /**
     * Convierte un monto de una moneda a otra
     * @param amount Monto a convertir
     * @param fromCurrency Moneda origen
     * @param toCurrency Moneda destino
     * @return Monto convertido
     */
    public static BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        
        BigDecimal fromRate = EXCHANGE_RATES.getOrDefault(fromCurrency, BigDecimal.ONE);
        BigDecimal toRate = EXCHANGE_RATES.getOrDefault(toCurrency, BigDecimal.ONE);
        
        // Convertir a USD primero, luego a la moneda destino
        BigDecimal amountInUSD = amount.divide(fromRate, 4, RoundingMode.HALF_UP);
        return amountInUSD.multiply(toRate).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Formatea un monto según la moneda
     * @param amount Monto
     * @param currency Moneda
     * @return String formateado
     */
    public static String format(BigDecimal amount, String currency) {
        String symbol = getCurrencySymbol(currency);
        return symbol + amount.setScale(2, RoundingMode.HALF_UP).toString();
    }
    
    private static String getCurrencySymbol(String currency) {
        return switch (currency) {
            case "USD" -> "$";
            case "EUR" -> "€";
            case "PEN" -> "S/";
            case "GBP" -> "£";
            case "JPY" -> "¥";
            default -> currency + " ";
        };
    }
}

