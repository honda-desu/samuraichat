package com.example.samuraichat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraichat.entity.ChatGroupMember;

public interface ChatGroupMemberRepository extends JpaRepository<ChatGroupMember, Integer>{

}
