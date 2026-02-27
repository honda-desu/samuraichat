package com.example.samuraichat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.samuraichat.entity.DmRoom;

public interface DmRoomRepository extends JpaRepository<DmRoom, Long> {

    // ① 2人の組み合わせで DM ルームを検索（既存ルームの再利用）
    @Query("SELECT r FROM DmRoom r " +
           "WHERE (r.user1.id = :userId1 AND r.user2.id = :userId2) " +
           "   OR (r.user1.id = :userId2 AND r.user2.id = :userId1)")
    Optional<DmRoom> findRoomByUsers(@Param("userId1") Long userId1,
                                     @Param("userId2") Long userId2);

    // ② 自分が参加している DM ルーム一覧を取得
    @Query("SELECT r FROM DmRoom r " +
           "WHERE r.user1.id = :userId OR r.user2.id = :userId " +
           "ORDER BY r.createdAt DESC")
    List<DmRoom> findByUser(@Param("userId") Long userId);
}