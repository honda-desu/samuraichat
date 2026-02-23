package com.example.samuraichat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.samuraichat.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Integer>{	
	@Query("SELECT m FROM Message m JOIN FETCH m.user WHERE m.chatGroup.id = :chatGroupId ORDER BY m.createdAt ASC")
	List<Message> findByChatGroupIdOrderByCreatedAtAsc(@Param("chatGroupId") Integer chatGroupId);
}
