package com.splitia.repository;

import com.splitia.model.Conversation;
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
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    @Query("SELECT DISTINCT c FROM Conversation c JOIN c.participants p WHERE p.user.id = :userId AND p.deletedAt IS NULL AND c.deletedAt IS NULL")
    List<Conversation> findByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT DISTINCT c FROM Conversation c JOIN c.participants p WHERE p.user.id = :userId AND p.deletedAt IS NULL AND c.deletedAt IS NULL")
    Page<Conversation> findByUserId(@Param("userId") UUID userId, Pageable pageable);
    
    @Query("SELECT c FROM Conversation c WHERE c.group.id = :groupId AND c.deletedAt IS NULL")
    Optional<Conversation> findByGroupId(@Param("groupId") UUID groupId);
    
    @Query("SELECT c FROM Conversation c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<Conversation> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
}

