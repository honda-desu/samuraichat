package com.example.samuraichat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraichat.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	public User findByEmail(String email);
}
