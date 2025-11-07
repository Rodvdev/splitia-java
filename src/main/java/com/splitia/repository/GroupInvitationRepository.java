package com.splitia.repository;

import com.splitia.model.GroupInvitation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, UUID> {
    @Query("SELECT gi FROM GroupInvitation gi WHERE gi.token = :token AND gi.deletedAt IS NULL")
    Optional<GroupInvitation> findByToken(@Param("token") String token);
    
    @Query("SELECT gi FROM GroupInvitation gi WHERE gi.group.id = :groupId AND gi.deletedAt IS NULL")
    Page<GroupInvitation> findByGroupId(@Param("groupId") UUID groupId, Pageable pageable);
    
    @Query("SELECT gi FROM GroupInvitation gi WHERE gi.id = :id AND gi.deletedAt IS NULL")
    Optional<GroupInvitation> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
}

