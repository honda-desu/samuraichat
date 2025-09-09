package com.example.samuraichat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraichat.entity.ChatGroup;
import com.example.samuraichat.entity.Favorite;
import com.example.samuraichat.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer>{
	public Favorite findByChatGroupAndUser(ChatGroup chatGroup, User user);
	
	public Page<Favorite> findByUserOrderByFavoritedAtDesc(User user, Pageable pageable);
	
}
