package com.example.samuraichat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.samuraichat.entity.Message;
import com.example.samuraichat.entity.User;
import com.example.samuraichat.repository.MessageRepository;

@Service
public class MessageService {
	private final MessageRepository messageRepository;
	
	public MessageService(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}
	
	public List<Message> findAllMessages(){
		return messageRepository.findAll();
	}
	
	public void saveMessage(String content, User user) {
		Message message = new Message();
		
		message.setContent(content);
		message.setUser(user);
		
		messageRepository.save(message);
	}

}
