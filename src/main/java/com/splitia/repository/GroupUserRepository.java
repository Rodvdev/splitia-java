package com.splitia.repository;

import com.splitia.model.GroupUser;
import com.splitia.model.enums.GroupRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, UUID> {
    Optional<GroupUser> findByUserIdAndGroupId(UUID userId, UUID groupId);
    boolean existsByUserIdAndGroupId(UUID userId, UUID groupId);
    void deleteByUserIdAndGroupId(UUID userId, UUID groupId);
}

