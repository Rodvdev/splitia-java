package com.splitia.mapper;

import com.splitia.dto.response.TimeEntryResponse;
import com.splitia.model.project.TimeEntry;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TimeEntryMapper {
    TimeEntryMapper INSTANCE = Mappers.getMapper(TimeEntryMapper.class);
    
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", ignore = true)
    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "taskTitle", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", ignore = true)
    TimeEntryResponse toResponse(TimeEntry timeEntry);
    
    @AfterMapping
    default void setRelatedData(@MappingTarget TimeEntryResponse response, TimeEntry timeEntry) {
        if (timeEntry.getProject() != null) {
            response.setProjectName(timeEntry.getProject().getName());
        }
        if (timeEntry.getTask() != null) {
            response.setTaskTitle(timeEntry.getTask().getTitle());
        }
        if (timeEntry.getUser() != null) {
            response.setUserName(timeEntry.getUser().getName());
        }
    }
    
    List<TimeEntryResponse> toResponseList(List<TimeEntry> timeEntries);
}

