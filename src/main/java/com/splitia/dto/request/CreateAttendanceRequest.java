package com.splitia.dto.request;

import com.splitia.model.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAttendanceRequest {
    @NotNull(message = "Employee ID is required")
    private UUID employeeId;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    private LocalDateTime checkIn;
    
    private LocalDateTime checkOut;
    
    private AttendanceStatus status = AttendanceStatus.PRESENT;
}

