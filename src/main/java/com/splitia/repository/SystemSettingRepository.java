package com.splitia.repository;

import com.splitia.model.common.SystemSetting;
import com.splitia.model.enums.SettingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, UUID> {
    @Query("SELECT s FROM SystemSetting s WHERE s.key = :key AND s.deletedAt IS NULL")
    Optional<SystemSetting> findByKey(@Param("key") String key);
    
    @Query("SELECT s FROM SystemSetting s WHERE s.deletedAt IS NULL")
    List<SystemSetting> findAllActive();
    
    @Query("SELECT s FROM SystemSetting s WHERE s.type = :type AND s.deletedAt IS NULL")
    List<SystemSetting> findByType(@Param("type") SettingType type);
}

