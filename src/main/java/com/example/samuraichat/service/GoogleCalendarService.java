package com.example.samuraichat.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

@Service
public class GoogleCalendarService {

    private final OAuth2AuthorizedClientService clientService;

    public GoogleCalendarService(OAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    public List<Event> getTodayEvents(OAuth2AuthenticationToken authentication) throws Exception {

        OAuth2AuthorizedClient client =
                clientService.loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());

        String accessToken = client.getAccessToken().getTokenValue();

        Calendar service = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                request -> request.getHeaders().setAuthorization("Bearer " + accessToken)
        ).setApplicationName("SamuraiChat").build();

        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime endOfDay = new DateTime(System.currentTimeMillis() + 1000L * 60 * 60 * 24);

        Events events = service.events().list("primary")
                .setTimeMin(now)
                .setTimeMax(endOfDay)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems();
    }
    
    public List<Event> getMonthlyEvents(OAuth2AuthenticationToken authentication, LocalDate month) throws Exception {

        OAuth2AuthorizedClient client =
                clientService.loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());

        String accessToken = client.getAccessToken().getTokenValue();

        Calendar service = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                request -> request.getHeaders().setAuthorization("Bearer " + accessToken)
        ).setApplicationName("SamuraiChat").build();

        LocalDate firstDay = month.withDayOfMonth(1);
        LocalDate lastDay = month.withDayOfMonth(month.lengthOfMonth());

        DateTime timeMin = new DateTime(firstDay.toString() + "T00:00:00+09:00");
        DateTime timeMax = new DateTime(lastDay.toString() + "T23:59:59+09:00");

        Events events = service.events().list("primary")
                .setTimeMin(timeMin)
                .setTimeMax(timeMax)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        
        List<Event> items = events.getItems();
     // ★ ここで startDateString をセットする

        for (Event event : items) {
            String startDate = null;

            if (event.getStart().getDateTime() != null) {
                startDate = event.getStart().getDateTime().toString().substring(0, 10);
            } else if (event.getStart().getDate() != null) {
                startDate = event.getStart().getDate().toString();
            }

            event.set("startDateString", startDate);
        }

        return items;

        
    }
}