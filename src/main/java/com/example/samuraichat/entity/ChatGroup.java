package com.example.samuraichat.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "chat_groups")
@Data
public class ChatGroup {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@OneToMany(mappedBy = "chatGroup")
	private List<Message> messages;
	
	@OneToMany(mappedBy = "chatGroup")
	private List<ChatGroupMember> members;
	
	@Transient
	private Integer unreadCount = 0;

}
