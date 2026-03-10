package com.example.samuraichat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.samuraichat.entity.Report;
import com.example.samuraichat.repository.ReportRepository;

@Controller
public class AdminReportController {

    private final ReportRepository reportRepository;

    public AdminReportController(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @GetMapping("/admin/reports")
    public String showReports(Model model) {
        model.addAttribute("reports", reportRepository.findAll());
        return "admin/report-list";
    }
    
    @GetMapping("/admin/reports/{id}/resolve")
    public String resolveReport(@PathVariable Long id) {

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        report.setStatus("RESOLVED");
        reportRepository.save(report);

        return "redirect:/admin/reports";
    }
}