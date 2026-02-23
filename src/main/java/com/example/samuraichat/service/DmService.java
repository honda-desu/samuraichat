package com.example.samuraichat.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraichat.entity.DmMessage;
import com.example.samuraichat.entity.DmRoom;
import com.example.samuraichat.entity.User;
import com.example.samuraichat.repository.DmMessageRepository;
import com.example.samuraichat.repository.DmRoomRepository;
import com.example.samuraichat.repository.UserRepository;

@Service
@Transactional
public class DmService {
	
	private final DmRoomRepository dmRoomRepository;
	private final DmMessageRepository dmMessageRepository;
	private final UserRepository userRepository;
	
	public DmService(DmRoomRepository dmRoomRepository, DmMessageRepository dmMessageRepository, UserRepository userRepository) {
		this.dmRoomRepository = dmRoomRepository;
		this.dmMessageRepository = dmMessageRepository;
		this.userRepository = userRepository;
	}
	
	//DmRoomを取得または作成
	public DmRoom getOrCreateRoom(Long userId1, Long userId2) {
		
		//既存Roomを検索
		Optional<DmRoom> existingRoom = dmRoomRepository.findRoomByUsers(userId1, userId2);
		
		if(existingRoom.isPresent()) {
			return existingRoom.get();
		}
		
		// なければ新規作成
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        DmRoom newRoom = new DmRoom();
        newRoom.setUser1(user1);
        newRoom.setUser2(user2);
        
        return dmRoomRepository.save(newRoom);

	}
	
	//DmRoom一覧
	public List<DmRoom> getRoomsForUser(Long userId){
		return dmRoomRepository.findByUser(userId);
	}
	
	//Message一覧
	public List<DmMessage> getMessages(Long roomId){
		DmRoom room = dmRoomRepository.findById(roomId)
				.orElseThrow(() -> new RuntimeException("Room not found"));
		
		return dmMessageRepository.findByRoomOrderByCreatedAtAsc(room);

	}
	
	//Message送信
	public void sendMessage(Long roomId, Long senderId, String content, String imagePath) {
		DmRoom room = dmRoomRepository.findById(roomId)
				.orElseThrow(() -> new RuntimeException("Room not found"));
		
		User sender = userRepository.findById(senderId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		DmMessage message  = new DmMessage();
		message.setRoom(room);
		message.setSender(sender);
		message.setContent(content);
		message.setImagePath(imagePath);
		
		dmMessageRepository.save(message);
	}
	
	// ★ 画像メッセージ保存（MessageService と同じ構造）
    public void saveImageMessage(DmRoom room, User sender, String imagePath) {
        DmMessage message = new DmMessage();
        message.setImagePath(imagePath);
        message.setSender(sender);
        message.setRoom(room);

        dmMessageRepository.save(message);
    }
    
    public DmRoom findById(Long roomId) {
        return dmRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたDMルームが存在しません"));
    }


}
