package com.example.samuraichat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraichat.entity.DmMessage;
import com.example.samuraichat.entity.DmRoom;

public interface DmMessageRepository extends JpaRepository<DmMessage, Long>{
    
    // DMルーム内のメッセージを時系列順に取得
    List<DmMessage> findByRoomOrderByCreatedAtAsc(DmRoom room);

    // ★ 相手のメッセージを既読にする
    @Modifying
    @Transactional
    @Query("UPDATE DmMessage m SET m.isRead = true WHERE m.room.id = :roomId AND m.sender.id <> :myId")
    void markAsRead(@Param("roomId") Long roomId, @Param("myId") Long myId);

    // ★ 未読メッセージ数を取得
    @Query("SELECT COUNT(m) FROM DmMessage m WHERE m.room.id = :roomId AND m.sender.id <> :myId AND m.isRead = false")
    int countUnread(@Param("roomId") Long roomId, @Param("myId") Long myId);
}