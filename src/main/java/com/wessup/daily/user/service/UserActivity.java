package com.wessup.daily.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wessup.daily.user.entity.User;
import com.wessup.daily.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class UserActivity {

    private Logger logger;

    @Value("${github.api}")
    private String apiURI;

    private RestTemplate restTemplate;

    private UserRepository userRepository;

    @Autowired
    public UserActivity(RestTemplateBuilder restTemplateBuilder, UserRepository userRepository) {
        this.restTemplate = restTemplateBuilder.build();
        this.logger = LoggerFactory.getLogger(UserActivity.class);
        this.userRepository = userRepository;
    }

    protected String getToken(String username) {
        User user = this.userRepository.findByUsername(username);
        return user.getToken();
    }

    public String allEvents(String username, String token) {
        String suffix = "/users/" + username + "/events";
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "token " + token);
        HttpEntity<String> request = new HttpEntity<String>("", headers);

        ResponseEntity<String> response =
                this.restTemplate.exchange((this.apiURI + suffix), HttpMethod.GET, request, String.class);
        // this.logger.info(response.getBody());
        return response.getBody();
    }

    public List<HashMap<String, String>> commitEvents(String username) {
        String token = this.getToken(username);
        String events = this.allEvents(username, token);
        ObjectMapper mapper = new ObjectMapper();
        List<HashMap<String, String>> todayCommits = new ArrayList<HashMap<String, String>>();
        if (events == "[]") {
            this.logger.error("Unavailable Response");
        }
        LocalDate today = LocalDate.now();
        this.logger.info(today.toString());
//        MultiValueMap<Object, Object> json = new LinkedMultiValueMap();
        // MultiValueMap - jackson parsing error
        try {
            // extract commit info
            List<HashMap<String, String>> jsonList = Arrays.asList(mapper.readValue(events, HashMap[].class));
            for (HashMap<String, String> json: jsonList) {
                String dateString = json.get("created_at").toString();
                // Compare date with today
                this.logger.info(dateString);
                // ISO8601
                LocalDate date = LocalDate.parse(dateString,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
                if (date.equals(today)) {
                    todayCommits.add(json);
                }
            }
        }
        catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } finally {
            return todayCommits;
        }
    }
}
