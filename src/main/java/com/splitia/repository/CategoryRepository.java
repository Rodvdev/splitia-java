package com.splitia.repository;

import com.splitia.model.CustomCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<CustomCategory, UUID> {
    List<CustomCategory> findByUserId(UUID userId);
    boolean existsByUserIdAndName(UUID userId, String name);
}

