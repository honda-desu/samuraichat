package com.example.samuraichat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.samuraichat.entity.GroupMessageRead;

public interface GroupMessageReadRepository extends JpaRepository<GroupMessageRead, Long> {

    // ★ 既に既読登録されているか確認
    boolean existsByMessageIdAndUserId(Long messageId, Long userId);

    // ★ 未読メッセージ数を数える
    @Query("""
        SELECT COUNT(m)
        FROM Message m
        WHERE m.chatGroup.id = :groupId
          AND m.user.id <> :userId
          AND NOT EXISTS (
                SELECT r FROM GroupMessageRead r
                WHERE r.message.id = m.id
                  AND r.user.id = :userId
          )
    """)
    int countUnread(@Param("groupId") Long groupId, @Param("userId") Long userId);
}