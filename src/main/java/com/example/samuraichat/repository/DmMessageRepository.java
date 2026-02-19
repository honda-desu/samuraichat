package com.example.samuraichat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraichat.entity.DmMessage;
import com.example.samuraichat.entity.DmRoom;

public interface DmMessageRepository extends JpaRepository<DmMessage, Long>{
	
	//DMルーム内のメッセージを時系列順に取得
	List<DmMessage> findByRoomOrderByCreatedAtAsc(DmRoom room);

}
