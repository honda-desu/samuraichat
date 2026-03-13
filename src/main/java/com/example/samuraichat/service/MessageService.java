package com.example.samuraichat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.samuraichat.entity.ChatGroup;
import com.example.samuraichat.entity.GroupMessageRead;
import com.example.samuraichat.entity.Message;
import com.example.samuraichat.entity.User;
import com.example.samuraichat.enums.MessageType;
import com.example.samuraichat.repository.GroupMessageReadRepository;
import com.example.samuraichat.repository.MessageRepository;
import com.example.samuraichat.repository.UserRepository;

@Service
public class MessageService {
	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	private final GroupMessageReadRepository groupMessageReadRepository; 
	
	public MessageService(MessageRepository messageRepository, UserRepository userRepository, GroupMessageReadRepository groupMessageReadRepository) {
		this.messageRepository = messageRepository;
		this.userRepository = userRepository;
		this.groupMessageReadRepository = groupMessageReadRepository;
		
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

	public List<Message> getMessagesByGroupId(Long groupId){
		return messageRepository.findByChatGroupIdOrderByCreatedAtAsc(groupId);
	}
	
	// ★★★★★ 追加：グループチャットの既読処理 ★★★★★
    public void markGroupMessagesAsRead(Long groupId, Long userId) {

        List<Message> messages = messageRepository.findByChatGroupIdOrderByCreatedAtAsc(groupId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        for (Message msg : messages) {

            // 自分のメッセージは既読にしない
            if (msg.getUser().getId().equals(userId)) {
                continue;
            }

            // 既に既読ならスキップ
            if (groupMessageReadRepository.existsByMessageIdAndUserId(msg.getId(), userId)) {
                continue;
            }

            // 未読 → 既読登録
            GroupMessageRead read = new GroupMessageRead();
            read.setMessage(msg);
            read.setUser(user);

            groupMessageReadRepository.save(read);
        }
    }

    // ★★★★★ 追加：未読数を取得する ★★★★★
    public int getUnreadCount(Long groupId, Long userId) {
        return groupMessageReadRepository.countUnread(groupId, userId);
    }

	

}
