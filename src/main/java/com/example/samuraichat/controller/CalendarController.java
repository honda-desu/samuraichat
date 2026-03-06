package com.example.samuraichat.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraichat.security.CustomOAuth2User;
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

        return "calendar/calendar-today";
    }
    
    @GetMapping("/calendar")
    public String calendar(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model,
            @AuthenticationPrincipal Object principal) throws Exception {

        // ★ Google ログインでなければ案内ページへ
        if (!(principal instanceof CustomOAuth2User oauthUser)) {
            model.addAttribute("errorMessage", "Google カレンダーを利用するには Google アカウントでログインしてください。");
            return "calendar/require-google";  // ← このページを作成
        }

        // ★ Google ログインユーザー
        OAuth2AuthenticationToken auth =
                (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();



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

        return "calendar/calendar";
    }
    
    //予定の詳細を記載する
    @GetMapping("/calendar/event")
    public String eventDetail(@RequestParam String id,
                              Model model,
                              OAuth2AuthenticationToken auth) throws Exception {

        if (auth == null) {
            return "redirect:/login";
        }

        Event event = calendarService.getEventById(auth, id);
        model.addAttribute("event", event);

        return "calendar/calendar-event-detail";
    }
    
    //予定の編集機能を追加する
    @GetMapping("/calendar/event/edit")
    public String editEvent(@RequestParam String id,
                            Model model,
                            OAuth2AuthenticationToken auth) throws Exception {

        if (auth == null) return "redirect:/login";

        Event event = calendarService.getEventById(auth, id);

        // HTML の datetime-local 用に整形
        String startValue = null;
        if (event.getStart().getDateTime() != null) {
            startValue = event.getStart().getDateTime().toStringRfc3339()
                    .substring(0, 16); // yyyy-MM-ddTHH:mm
        }

        String endValue = null;
        if (event.getEnd().getDateTime() != null) {
            endValue = event.getEnd().getDateTime().toStringRfc3339()
                    .substring(0, 16);
        }

        model.addAttribute("event", event);
        model.addAttribute("startValue", startValue);
        model.addAttribute("endValue", endValue);

        return "calendar/calendar-event-edit";
    }
    @PostMapping("/calendar/event/edit")
    public String updateEvent(@RequestParam String id,
                              @RequestParam String summary,
                              @RequestParam String description,
                              @RequestParam String start,
                              @RequestParam String end,
                              OAuth2AuthenticationToken auth) throws Exception {

        if (auth == null) return "redirect:/login";

        calendarService.updateEvent(auth, id, summary, description, start, end);

        return "redirect:/calendar/event?id=" + id;
    }
    
    //予定の新規作成
    @GetMapping("/calendar/event/new")
    public String newEventForm(Model model, OAuth2AuthenticationToken auth) {
        if (auth == null) return "redirect:/login";
        return "calendar/calendar-event-new";
    }
    
    @PostMapping("/calendar/event/new")
    public String createEvent(@RequestParam String summary,
                              @RequestParam String description,
                              @RequestParam String start,
                              @RequestParam String end,
                              OAuth2AuthenticationToken auth) throws Exception {

        if (auth == null) return "redirect:/login";

        String eventId = calendarService.createEvent(auth, summary, description, start, end);

        return "redirect:/calendar/event?id=" + eventId;
    }
    
    
    //予定を削除する
    @PostMapping("/calendar/event/delete")
    public String deleteEvent(@RequestParam String id,
                              OAuth2AuthenticationToken auth) throws Exception {

        if (auth == null) return "redirect:/login";

        calendarService.deleteEvent(auth, id);

        return "redirect:/calendar";
    }
}