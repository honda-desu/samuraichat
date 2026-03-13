package com.example.samuraichat.entity;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="dm_room")
public class DmRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// user1
	@ManyToOne
	@JoinColumn(name = "user1_id", nullable = false)
	private User user1;
	
	// user2
	@ManyToOne
	@JoinColumn(name = "user2_id", nullable = false)
	private User user2;
	
	@OneToMany(mappedBy = "room")
	private List<DmMessage> messages;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;
	
	// ★ 追加：ブロック状態（DBには保存しない）
    @Transient
    private boolean blocked;
    
 // ★ 追加：未読メッセージ数（DBには保存しない）
    @Transient
    private int unreadCount;


}
