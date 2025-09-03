package com.example.samuraichat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraichat.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Integer>{	
	List<Message> findByChatGroupIdOrderByCreatedAtAsc(Integer chatGroupId);
}
