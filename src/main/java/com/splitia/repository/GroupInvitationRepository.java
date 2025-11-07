package com.splitia.repository;

import com.splitia.model.GroupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, UUID> {
    Optional<GroupInvitation> findByToken(String token);
    void deleteByGroupId(UUID groupId);
}

