package com.example.samuraichat.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.samuraichat.entity.ChatGroup;
import com.example.samuraichat.entity.Favorite;
import com.example.samuraichat.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer>{
	public Favorite findByChatGroupAndUser(ChatGroup chatGroup, User user);
	
	public Page<Favorite> findByUserOrderByFavoritedAtDesc(User user, Pageable pageable);
	
	//「お気に入りの中から、グループの最新メッセージ順に並べるクエリ
	@Query("""
		    SELECT f.chatGroup
		    FROM Favorite f
		    WHERE f.user.id = :userId
		    ORDER BY (
		        SELECT MAX(m.createdAt)
		        FROM Message m
		        WHERE m.chatGroup.id = f.chatGroup.id
		    ) DESC
		    """)
		List<ChatGroup> findRecentFavoriteGroups(@Param("userId") Long userId, Pageable pageable);
	
	
}
