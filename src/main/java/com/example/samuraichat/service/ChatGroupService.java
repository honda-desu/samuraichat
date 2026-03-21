package com.example.samuraichat.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraichat.entity.ChatGroup;
import com.example.samuraichat.form.ChatRegisterForm;
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
	
	public Optional<ChatGroup> findById(Long groupId){
		return chatGroupRepository.findById(groupId);
	}
	
	@Transactional
	public void create(ChatRegisterForm chatRegisterForm) {
		ChatGroup chatGroup = new ChatGroup();
		
		chatGroup.setName(chatRegisterForm.getName());
		
		chatGroupRepository.save(chatGroup);
	}
	
	@Transactional
	public void delete(ChatGroup chatGroup) {
		chatGroupRepository.delete(chatGroup);
	}
	
	//ホーム画面に新着メッセージが入った順
	public List<ChatGroup> findRecentGroups(int limit) {
	    Pageable pageable = PageRequest.of(0, limit);
	    return chatGroupRepository.findRecentGroups(pageable);
	}
	
}
