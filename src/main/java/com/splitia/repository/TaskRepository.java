package com.splitia.repository;

import com.splitia.model.Task;
import com.splitia.model.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    
    @Query("SELECT t FROM Task t WHERE t.group.id = :groupId AND t.deletedAt IS NULL ORDER BY t.position ASC, t.createdAt DESC")
    List<Task> findByGroupId(@Param("groupId") UUID groupId);
    
    @Query("SELECT t FROM Task t WHERE t.group.id = :groupId AND t.deletedAt IS NULL ORDER BY t.position ASC, t.createdAt DESC")
    Page<Task> findByGroupId(@Param("groupId") UUID groupId, Pageable pageable);
    
    @Query("SELECT t FROM Task t WHERE t.group.id = :groupId AND t.status = :status AND t.deletedAt IS NULL ORDER BY t.position ASC, t.createdAt DESC")
    List<Task> findByGroupIdAndStatus(@Param("groupId") UUID groupId, @Param("status") TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId AND t.deletedAt IS NULL ORDER BY t.dueDate ASC, t.createdAt DESC")
    List<Task> findByAssignedToId(@Param("userId") UUID userId);
    
    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<Task> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.group.id = :groupId AND t.deletedAt IS NULL")
    Long countByGroupId(@Param("groupId") UUID groupId);
    
    @Query("SELECT MAX(t.position) FROM Task t WHERE t.group.id = :groupId AND t.status = :status AND t.deletedAt IS NULL")
    Integer findMaxPositionByGroupIdAndStatus(@Param("groupId") UUID groupId, @Param("status") TaskStatus status);
}

