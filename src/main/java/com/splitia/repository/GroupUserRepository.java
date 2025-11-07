package com.splitia.repository;

import com.splitia.model.GroupUser;
import com.splitia.model.enums.GroupRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, UUID> {
    @Query("SELECT gu FROM GroupUser gu WHERE gu.user.id = :userId AND gu.group.id = :groupId AND gu.deletedAt IS NULL")
    Optional<GroupUser> findByUserIdAndGroupId(@Param("userId") UUID userId, @Param("groupId") UUID groupId);
    
    @Query("SELECT CASE WHEN COUNT(gu) > 0 THEN true ELSE false END FROM GroupUser gu WHERE gu.user.id = :userId AND gu.group.id = :groupId AND gu.deletedAt IS NULL")
    boolean existsByUserIdAndGroupId(@Param("userId") UUID userId, @Param("groupId") UUID groupId);
    
    @Query("SELECT gu FROM GroupUser gu WHERE gu.group.id = :groupId AND gu.deletedAt IS NULL")
    List<GroupUser> findByGroupId(@Param("groupId") UUID groupId);
    
    @Query("SELECT gu FROM GroupUser gu WHERE gu.id = :id AND gu.deletedAt IS NULL")
    Optional<GroupUser> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
}

