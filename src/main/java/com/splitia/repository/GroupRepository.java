package com.splitia.repository;

import com.splitia.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    @Query("SELECT g FROM Group g JOIN g.members m WHERE m.user.id = :userId")
    List<Group> findByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT g FROM Group g WHERE g.createdBy.id = :userId")
    List<Group> findByCreatedById(@Param("userId") UUID userId);
}

