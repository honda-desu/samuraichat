package com.example.samuraichat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.samuraichat.entity.ChatGroup;
import com.example.samuraichat.entity.Message;
import com.example.samuraichat.entity.User;
import com.example.samuraichat.enums.MessageType;
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
	
	public void saveTextMessage(String content, User user, ChatGroup group) {
		Message message = new Message();
		
		message.setContent(content);
		message.setUser(user);
		message.setChatGroup(group);
		message.setMessageType(MessageType.TEXT);		
		
		messageRepository.save(message);
	}
	
	public void saveImageMessage(String imagePath, User user, ChatGroup group) {
        Message message = new Message();
        
        message.setImagePath(imagePath);
        message.setUser(user);
        message.setChatGroup(group);
        message.setMessageType(MessageType.IMAGE); 
        
        messageRepository.save(message);
        
    }

	
	public List<Message> getMessagesByGroupId(Integer groupId){
		return messageRepository.findByChatGroupIdOrderByCreatedAtAsc(groupId);
	}
	

}
