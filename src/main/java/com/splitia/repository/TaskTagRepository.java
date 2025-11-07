package com.splitia.repository;

import com.splitia.model.TaskTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskTagRepository extends JpaRepository<TaskTag, UUID> {
    
    @Query("SELECT tt FROM TaskTag tt WHERE tt.group.id = :groupId AND tt.deletedAt IS NULL ORDER BY tt.name ASC")
    List<TaskTag> findByGroupId(@Param("groupId") UUID groupId);
    
    @Query("SELECT tt FROM TaskTag tt WHERE tt.group.id = :groupId AND tt.name = :name AND tt.deletedAt IS NULL")
    Optional<TaskTag> findByGroupIdAndName(@Param("groupId") UUID groupId, @Param("name") String name);
    
    @Query("SELECT CASE WHEN COUNT(tt) > 0 THEN true ELSE false END FROM TaskTag tt WHERE tt.group.id = :groupId AND tt.name = :name AND tt.deletedAt IS NULL")
    boolean existsByGroupIdAndName(@Param("groupId") UUID groupId, @Param("name") String name);
    
    @Query("SELECT tt FROM TaskTag tt WHERE tt.id = :id AND tt.deletedAt IS NULL")
    Optional<TaskTag> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
}

