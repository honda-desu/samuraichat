package com.example.samuraichat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.samuraichat.entity.ChatGroup;
import com.example.samuraichat.repository.ChatGroupRepository;

@Service
public class ChatGroupService {
	private final ChatGroupRepository chatGroupRepository;
	
	public ChatGroupService(ChatGroupRepository chatGroupRepository) {
		this.chatGroupRepository = chatGroupRepository;
	}
	
	public List<ChatGroup> findAllChatGroups(){
		return chatGroupRepository.findAll();
	}
}
