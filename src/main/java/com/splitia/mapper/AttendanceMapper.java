package com.splitia.mapper;

import com.splitia.dto.response.AttendanceResponse;
import com.splitia.model.hr.Attendance;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {
    AttendanceMapper INSTANCE = Mappers.getMapper(AttendanceMapper.class);
    
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeName", ignore = true)
    @Mapping(target = "employeeNumber", ignore = true)
    AttendanceResponse toResponse(Attendance attendance);
    
    @AfterMapping
    default void setEmployeeInfo(@MappingTarget AttendanceResponse response, Attendance attendance) {
        if (attendance.getEmployee() != null) {
            response.setEmployeeNumber(attendance.getEmployee().getEmployeeNumber());
            if (attendance.getEmployee().getUser() != null) {
                response.setEmployeeName(attendance.getEmployee().getUser().getName());
            }
        }
    }
    
    List<AttendanceResponse> toResponseList(List<Attendance> attendances);
}

