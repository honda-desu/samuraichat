package com.example.samuraichat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraichat.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}