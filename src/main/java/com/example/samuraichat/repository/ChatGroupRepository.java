package com.example.samuraichat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraichat.entity.ChatGroup;

public interface ChatGroupRepository extends JpaRepository<ChatGroup, Integer>{
	

}
