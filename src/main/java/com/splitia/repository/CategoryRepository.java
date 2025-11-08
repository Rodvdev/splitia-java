package com.splitia.repository;

import com.splitia.model.CustomCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<CustomCategory, UUID> {
    @EntityGraph(attributePaths = {"group", "createdBy"})
    @Query("SELECT c FROM CustomCategory c WHERE c.group.id = :groupId AND c.deletedAt IS NULL")
    List<CustomCategory> findByGroupId(@Param("groupId") UUID groupId);
    
    @EntityGraph(attributePaths = {"group", "createdBy"})
    @Query("SELECT c FROM CustomCategory c WHERE c.group.id = :groupId AND c.deletedAt IS NULL")
    Page<CustomCategory> findByGroupId(@Param("groupId") UUID groupId, Pageable pageable);
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CustomCategory c WHERE c.group.id = :groupId AND c.name = :name AND c.deletedAt IS NULL")
    boolean existsByGroupIdAndName(@Param("groupId") UUID groupId, @Param("name") String name);
    
    @EntityGraph(attributePaths = {"group", "createdBy"})
    @Query("SELECT c FROM CustomCategory c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<CustomCategory> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
}

