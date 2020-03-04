package com.wessup.daily.user.service.users;

// https://developer.github.com/v3/activity/events/

import com.wessup.daily.user.service.oauth.BasicAuth;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventsAPI extends BasicAuth {
    private String baseSuffix;

    public EventsAPI() {
        this.baseSuffix = "/users";
    }

    public List<Map<String, String>> allEvents(RestTemplate restTemplate,
                                                   String apiURL, String username, String token) {
        String suffix = this.baseSuffix + username + "/events";
        ResponseEntity<List> response = this.apiExchange(restTemplate, apiURL + suffix,
                HttpMethod.GET, HashMap.class, this.setHeaders(token), null);

        List<Map<String, String>> body = response.getBody();
        return body;
    }

    public List commitEvents(RestTemplate restTemplate, String apiURL, String username, String token) {
        List<Map<String, String>> events = this.allEvents(restTemplate, apiURL, username, token);
        if (events.isEmpty()) {
            // throws error
            return null;
        }
        List<Map<String, String>> todayCommits = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Map<String, String> event: events) {
            String dateString = event.get("created_at");
            // ISO8601
            LocalDate date = LocalDate.parse(dateString,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
            if (date.equals(today)) {
                todayCommits.add(event);
            }
        }
        return todayCommits;
    }
}
