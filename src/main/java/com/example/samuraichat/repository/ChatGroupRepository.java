package com.example.samuraichat.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.samuraichat.entity.ChatGroup;

public interface ChatGroupRepository extends JpaRepository<ChatGroup, Integer>{
	
	//Home画面に必要な新着メッセージが入った順のクエリ
	@Query("""
			SELECT g FROM ChatGroup g
			ORDER BY (
			    SELECT MAX(m.createdAt)
			    FROM Message m
			    WHERE m.chatGroup.id = g.id
			) DESC
			""")
			List<ChatGroup> findRecentGroups(Pageable pageable);

}
