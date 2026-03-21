package com.example.samuraichat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraichat.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository< VerificationToken, Integer> {
    public VerificationToken findByToken(String token);
}