package com.splitia.dto.response;

import com.splitia.model.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private UUID id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
    private BigDecimal budget;
    private UUID managerId;
    private String managerName;
    private List<UUID> taskIds;
    private Integer totalTasks;
    private Double totalHours;
}

