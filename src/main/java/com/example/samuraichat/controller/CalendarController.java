package com.example.samuraichat.controller;

import java.util.List;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.samuraichat.service.GoogleCalendarService;
import com.google.api.services.calendar.model.Event;

@Controller
public class CalendarController {

    private final GoogleCalendarService calendarService;

    public CalendarController(GoogleCalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping("/calendar")
    public String calendar(Model model, OAuth2AuthenticationToken auth) throws Exception {

        if (auth != null) {
            List<Event> events = calendarService.getTodayEvents(auth);
            model.addAttribute("events", events);
        }

        return "calendar";
    }
}