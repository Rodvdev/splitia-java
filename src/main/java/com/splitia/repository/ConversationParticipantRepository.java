package com.splitia.repository;

import com.splitia.model.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, UUID> {
    @Query("SELECT cp FROM ConversationParticipant cp WHERE cp.conversation.id = :conversationId AND cp.user.id = :userId AND cp.deletedAt IS NULL")
    Optional<ConversationParticipant> findByConversationIdAndUserId(@Param("conversationId") UUID conversationId, @Param("userId") UUID userId);
}

