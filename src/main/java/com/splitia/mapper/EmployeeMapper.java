package com.splitia.mapper;

import com.splitia.dto.response.AttendanceResponse;
import com.splitia.dto.response.EmployeeResponse;
import com.splitia.dto.response.PayrollItemResponse;
import com.splitia.dto.response.PayrollResponse;
import com.splitia.model.hr.Attendance;
import com.splitia.model.hr.Employee;
import com.splitia.model.hr.Payroll;
import com.splitia.model.hr.PayrollItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);
    
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "userEmail", ignore = true)
    @Mapping(target = "managerName", ignore = true)
    EmployeeResponse toResponse(Employee employee);
    
    @AfterMapping
    default void setRelatedData(@MappingTarget EmployeeResponse response, Employee employee) {
        if (employee.getUser() != null) {
            response.setUserName(employee.getUser().getName());
            response.setUserEmail(employee.getUser().getEmail());
        }
    }
    
    List<EmployeeResponse> toResponseList(List<Employee> employees);
}

