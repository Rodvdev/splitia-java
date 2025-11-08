package com.splitia.mapper;

import com.splitia.dto.response.ProjectResponse;
import com.splitia.dto.response.TimeEntryResponse;
import com.splitia.model.project.Project;
import com.splitia.model.project.TimeEntry;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);
    
    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(target = "managerName", ignore = true)
    @Mapping(target = "taskIds", ignore = true)
    @Mapping(target = "totalTasks", ignore = true)
    @Mapping(target = "totalHours", ignore = true)
    ProjectResponse toResponse(Project project);
    
    @AfterMapping
    default void setRelatedData(@MappingTarget ProjectResponse response, Project project) {
        if (project.getManager() != null) {
            response.setManagerName(project.getManager().getName());
        }
        if (project.getTasks() != null && !project.getTasks().isEmpty()) {
            response.setTaskIds(project.getTasks().stream().map(t -> t.getId()).toList());
            response.setTotalTasks(project.getTasks().size());
        }
        if (project.getTimeEntries() != null && !project.getTimeEntries().isEmpty()) {
            response.setTotalHours(project.getTimeEntries().stream()
                    .mapToDouble(te -> te.getHours() != null ? te.getHours() : 0.0)
                    .sum());
        }
    }
    
    List<ProjectResponse> toResponseList(List<Project> projects);
}

