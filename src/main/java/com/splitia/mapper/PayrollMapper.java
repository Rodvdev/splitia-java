package com.splitia.mapper;

import com.splitia.dto.response.PayrollItemResponse;
import com.splitia.dto.response.PayrollResponse;
import com.splitia.model.hr.Payroll;
import com.splitia.model.hr.PayrollItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PayrollMapper {
    PayrollMapper INSTANCE = Mappers.getMapper(PayrollMapper.class);
    
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeName", ignore = true)
    @Mapping(target = "employeeNumber", ignore = true)
    @Mapping(target = "items", ignore = true)
    PayrollResponse toResponse(Payroll payroll);
    
    @AfterMapping
    default void setRelatedData(@MappingTarget PayrollResponse response, Payroll payroll) {
        if (payroll.getEmployee() != null) {
            response.setEmployeeNumber(payroll.getEmployee().getEmployeeNumber());
            if (payroll.getEmployee().getUser() != null) {
                response.setEmployeeName(payroll.getEmployee().getUser().getName());
            }
        }
        if (payroll.getItems() != null && !payroll.getItems().isEmpty()) {
            response.setItems(payroll.getItems().stream()
                    .map(this::itemToResponse)
                    .toList());
        }
    }
    
    PayrollItemResponse itemToResponse(PayrollItem item);
    
    List<PayrollResponse> toResponseList(List<Payroll> payrolls);
}

