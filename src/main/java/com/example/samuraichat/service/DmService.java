package com.example.samuraichat.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraichat.entity.DmMessage;
import com.example.samuraichat.entity.DmRoom;
import com.example.samuraichat.entity.User;
import com.example.samuraichat.repository.BlockRepository;
import com.example.samuraichat.repository.DmMessageRepository;
import com.example.samuraichat.repository.DmRoomRepository;
import com.example.samuraichat.repository.UserRepository;

@Service
@Transactional
public class DmService {
	
	private final DmRoomRepository dmRoomRepository;
	private final DmMessageRepository dmMessageRepository;
	private final UserRepository userRepository;
	private final BlockRepository blockRepository;
	
	public DmService(DmRoomRepository dmRoomRepository, DmMessageRepository dmMessageRepository, UserRepository userRepository, BlockRepository blockRepository) {
		this.dmRoomRepository = dmRoomRepository;
		this.dmMessageRepository = dmMessageRepository;
		this.userRepository = userRepository;
		this.blockRepository = blockRepository;
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
		
		// ★ 相手ユーザーを特定
        Long user1 = room.getUser1().getId();
        Long user2 = room.getUser2().getId();
        Long receiverId = user1.equals(senderId) ? user2 : user1;

        // ★ ブロックチェック（相手 → 自分）
        if (blockRepository.existsByBlockerIdAndBlockedUserId(receiverId, senderId)) {
            throw new IllegalStateException("相手にブロックされているため、メッセージを送信できません。");
        }

        // ★ ブロックチェック（自分 → 相手）
        if (blockRepository.existsByBlockerIdAndBlockedUserId(senderId, receiverId)) {
            throw new IllegalStateException("ブロック中のユーザーにはメッセージを送信できません。");
        }

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
    
    public User getPartnerUser(Long roomId, Long myId) {

        DmRoom room = dmRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // 自分が user1 の場合 → 相手は user2
        if (room.getUser1().getId().equals(myId)) {
            return room.getUser2();
        }

        // 自分が user2 の場合 → 相手は user1
        return room.getUser1();
    }
    
    public List<DmRoom> getRecentRoomsForUser(Long userId, int limit) {
        return dmRoomRepository.findRecentRooms(userId, PageRequest.of(0, limit));
    }



}
