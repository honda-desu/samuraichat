package com.example.samuraichat.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.samuraichat.entity.ChatGroup;
import com.example.samuraichat.repository.ChatGroupRepository;

@Service
public class ChatGroupService {
	private final ChatGroupRepository chatGroupRepository;
	
	public ChatGroupService(ChatGroupRepository chatGroupRepository) {
		this.chatGroupRepository = chatGroupRepository;
	}
	
	public Page<ChatGroup> findAllChatGroups(Pageable pageable){
		return chatGroupRepository.findAll(pageable);
	}
}
