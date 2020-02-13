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

        return response.getBody();
    }

    public MultiValueMap<Object, Object> commitEvents(String username, String token) {
        String events = this.allEvents(username, token);
        ObjectMapper mapper = new ObjectMapper();

        MultiValueMap<Object, Object> json = new LinkedMultiValueMap();
        try {
            json = mapper.readValue(events, LinkedMultiValueMap.class);
        }
        catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (json.isEmpty()) {
            this.logger.error("Parsing Json Response failed");
        }
        return json;
    }
}