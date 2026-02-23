package com.example.samuraichat.entity;


import java.sql.Timestamp;

import com.example.samuraichat.enums.MessageType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "messages")
@Data
public class Message { 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "chat_group_id")
	private ChatGroup chatGroup;
	
	@Column(name = "content")
	private String content;
	
	@Column(name = "image_path")
    private String imagePath;

	@Enumerated(EnumType.STRING)
	@Column(name = "message_type")
	private MessageType messageType;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;
}
