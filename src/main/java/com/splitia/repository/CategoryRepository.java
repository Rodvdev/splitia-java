package com.splitia.repository;

import com.splitia.model.CustomCategory;
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
public interface CategoryRepository extends JpaRepository<CustomCategory, UUID> {
    @Query("SELECT c FROM CustomCategory c WHERE c.user.id = :userId AND c.deletedAt IS NULL")
    List<CustomCategory> findByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT c FROM CustomCategory c WHERE c.user.id = :userId AND c.deletedAt IS NULL")
    Page<CustomCategory> findByUserId(@Param("userId") UUID userId, Pageable pageable);
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CustomCategory c WHERE c.user.id = :userId AND c.name = :name AND c.deletedAt IS NULL")
    boolean existsByUserIdAndName(@Param("userId") UUID userId, @Param("name") String name);
    
    @Query("SELECT c FROM CustomCategory c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<CustomCategory> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
}

