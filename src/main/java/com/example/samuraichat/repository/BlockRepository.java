package com.example.samuraichat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraichat.entity.Block;

public interface BlockRepository extends JpaRepository<Block, Long> {

    boolean existsByBlockerIdAndBlockedUserId(Long blockerId, Long blockedUserId);
    
 // ★ 追加：ブロック解除用
    void deleteByBlockerIdAndBlockedUserId(Long blockerId, Long blockedUserId);

}