package com.example.samuraichat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraichat.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Integer>{

}
