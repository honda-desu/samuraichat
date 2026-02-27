package com.example.samuraichat.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraichat.service.GoogleCalendarService;
import com.google.api.services.calendar.model.Event;

@Controller
public class CalendarController {

    private final GoogleCalendarService calendarService;

    public CalendarController(GoogleCalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping("/calendar/today")
    public String today(Model model, OAuth2AuthenticationToken auth) throws Exception {
    	
    	if (auth == null) {
            return "redirect:/login";
        }


        if (auth != null) {
            List<Event> events = calendarService.getTodayEvents(auth);
            model.addAttribute("events", events);
        }

        return "calendar-today";
    }
    
    @GetMapping("/calendar")
    public String calendar(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model,
            OAuth2AuthenticationToken auth) throws Exception {

        if (auth == null) {
            return "redirect:/login";
        }

        // 表示する年月を決定（指定がなければ今月）
        LocalDate today = LocalDate.now();
        LocalDate targetMonth = (year != null && month != null)
                ? LocalDate.of(year, month, 1)
                : today.withDayOfMonth(1);

        // 月間予定を取得
        List<Event> events = calendarService.getMonthlyEvents(auth, targetMonth);

        // カレンダー行列を作成
        LocalDate firstDay = targetMonth.withDayOfMonth(1);
        List<List<LocalDate>> calendarMatrix = new ArrayList<>();

        LocalDate start = firstDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = firstDay.withDayOfMonth(firstDay.lengthOfMonth())
                                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        LocalDate date = start;
        while (!date.isAfter(end)) {
            List<LocalDate> week = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                week.add(date);
                date = date.plusDays(1);
            }
            calendarMatrix.add(week);
        }

        // 前月・次月の計算
        LocalDate prevMonth = targetMonth.minusMonths(1);
        LocalDate nextMonth = targetMonth.plusMonths(1);

        model.addAttribute("calendarMatrix", calendarMatrix);
        model.addAttribute("events", events);
        model.addAttribute("today", today);
        model.addAttribute("targetMonth", targetMonth);
        model.addAttribute("prevMonth", prevMonth);
        model.addAttribute("nextMonth", nextMonth);

        return "calendar";
    }
}