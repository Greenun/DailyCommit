package com.wessup.daily.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserActivity {

    private Logger logger;

    @Value("${github.api}")
    private String apiURI;

    private RestTemplate restTemplate;

    @Autowired
    public UserActivity(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        this.logger = LoggerFactory.getLogger(UserActivity.class);
    }

    public String allEvents(String username, String token) {
        // commits 분류 필요
        String suffix = "/users/" + username + "/events";
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "token " + token);
        HttpEntity<String> request = new HttpEntity<String>("", headers);

        ResponseEntity<String> response =
                this.restTemplate.exchange((this.apiURI + suffix), HttpMethod.GET, request, String.class);
        this.logger.info(response.getBody());
        return response.getBody();
    }

    public void commitEvents(String username, String token) {
        // MultiValueMap<Object, Object>
        String events = this.allEvents(username, token);
        ObjectMapper mapper = new ObjectMapper();
        if (events == "[]") {
            this.logger.error("Unavailable Response");
        }
        LocalDateTime today = LocalDateTime.now();
        this.logger.info(today.toString());
//        MultiValueMap<Object, Object> json = new LinkedMultiValueMap();
        // MultiValueMap - jackson parsing error
        try {
            // json = mapper.readValue(events, LinkedMultiValueMap.class);
            // System.out.println(json);
            List<HashMap<Object, Object>> jsonList = Arrays.asList(mapper.readValue(events, HashMap[].class));
            for (HashMap<Object, Object> json: jsonList) {
                String dateString = json.get("created_at").toString();
                // Compare date with today
                this.logger.info(dateString);
                // ISO8601
                LocalDateTime temp = LocalDateTime.parse(dateString,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

                System.out.println(temp);
            }
            // extract commit info
            List<HashMap<String, String>> temp;
        }
        catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

//        return json;
    }
}
