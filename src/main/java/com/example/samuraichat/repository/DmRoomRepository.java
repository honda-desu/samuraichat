package com.example.samuraichat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.samuraichat.entity.DmRoom;

public interface DmRoomRepository extends JpaRepository<DmRoom, Long>{
	
	// 2名のユーザーの組み合わせでDMルームを検索
	
    @Query("SELECT r FROM DmRoom r " +
           "WHERE (r.user1.id = :u1 AND r.user2.id = :u2) " +
           "   OR (r.user1.id = :u2 AND r.user2.id = :u1)")
    Optional<DmRoom> findRoomByUsers(Long u1, Long u2);

    // あるユーザーが参加している DMルーム一覧
    @Query("SELECT r FROM DmRoom r " +
           "WHERE r.user1.id = :userId OR r.user2.id = :userId")
    List<DmRoom> findByUser(Long userId);


}
