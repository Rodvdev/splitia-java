package com.splitia.repository;

import com.splitia.model.GroupInvitation;
import com.splitia.model.enums.InvitationStatus;
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
public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, UUID> {
    @Query("SELECT gi FROM GroupInvitation gi WHERE gi.token = :token AND gi.deletedAt IS NULL")
    Optional<GroupInvitation> findByToken(@Param("token") String token);
    
    @Query("SELECT gi FROM GroupInvitation gi WHERE gi.group.id = :groupId AND gi.deletedAt IS NULL")
    Page<GroupInvitation> findByGroupId(@Param("groupId") UUID groupId, Pageable pageable);
    
    @Query("SELECT gi FROM GroupInvitation gi WHERE gi.id = :id AND gi.deletedAt IS NULL")
    Optional<GroupInvitation> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @Query("SELECT CASE WHEN COUNT(gi) > 0 THEN true ELSE false END FROM GroupInvitation gi WHERE gi.group.id = :groupId AND LOWER(gi.email) = LOWER(:email) AND gi.deletedAt IS NULL AND gi.status = :status")
    boolean existsByGroupIdAndEmailAndStatus(@Param("groupId") UUID groupId, @Param("email") String email, @Param("status") InvitationStatus status);
    
    @Query("SELECT CASE WHEN COUNT(gi) > 0 THEN true ELSE false END FROM GroupInvitation gi WHERE gi.group.id = :groupId AND gi.invitedUser.id = :userId AND gi.deletedAt IS NULL AND gi.status = :status")
    boolean existsByGroupIdAndInvitedUserIdAndStatus(@Param("groupId") UUID groupId, @Param("userId") UUID userId, @Param("status") InvitationStatus status);
    
    @Query("SELECT gi FROM GroupInvitation gi WHERE gi.deletedAt IS NULL AND gi.status = :status AND (gi.invitedUser.id = :userId OR LOWER(gi.email) = LOWER(:email))")
    List<GroupInvitation> findForUser(@Param("userId") UUID userId, @Param("email") String email, @Param("status") InvitationStatus status);
}
