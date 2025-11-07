package com.splitia.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Formatea una fecha y hora como string
     * @param dateTime Fecha y hora
     * @return String formateado
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }
    
    /**
     * Formatea una fecha como string
     * @param dateTime Fecha y hora
     * @return String formateado (solo fecha)
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATE_FORMATTER);
    }
    
    /**
     * Obtiene el inicio del mes para una fecha dada
     * @param dateTime Fecha
     * @return Inicio del mes
     */
    public static LocalDateTime getStartOfMonth(LocalDateTime dateTime) {
        return dateTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }
    
    /**
     * Obtiene el fin del mes para una fecha dada
     * @param dateTime Fecha
     * @return Fin del mes
     */
    public static LocalDateTime getEndOfMonth(LocalDateTime dateTime) {
        return dateTime.withDayOfMonth(dateTime.toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }
}

