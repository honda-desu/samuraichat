package com.example.samuraichat.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dm_message")
public class DmMessage {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//DMroom
	@ManyToOne
	@JoinColumn(name ="room_id", nullable = false)
	private DmRoom room;
	
	//sender
	@ManyToOne
	@JoinColumn(name = "sender_id", nullable = false)
	private User sender;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	@Column(name = "image_path")
	private String imagePath;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;

}
