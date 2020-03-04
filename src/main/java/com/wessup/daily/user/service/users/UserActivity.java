package com.wessup.daily.user.service.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wessup.daily.user.entity.PushAllowed;
import com.wessup.daily.user.entity.User;
import com.wessup.daily.user.repository.PushAllowedRepository;
import com.wessup.daily.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class UserActivity {

    private Logger logger;

    @Value("${github.api}")
    private String apiURL;

    private RestTemplate restTemplate;

    private PushAllowedRepository paRepository;

    private UserRepository userRepository;

    private EventsAPI eventsAPI;

    private UsersAPI usersAPI;

    @Autowired
    public UserActivity(RestTemplateBuilder restTemplateBuilder,
                        UserRepository userRepository, PushAllowedRepository paRepository) {
        this.restTemplate = restTemplateBuilder.build();
        this.logger = LoggerFactory.getLogger(UserActivity.class);
        this.userRepository = userRepository;
        this.paRepository = paRepository;
    }

    protected String getToken(String username) {
        User user = this.userRepository.findByUsername(username);
        return user.getToken();
    }

    public boolean getUser(String token) {
        try {
            Map<String, String> userInfo = this.usersAPI.getUser(this.restTemplate, this.apiURL, token);
            this.saveUser(userInfo, token);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public void allowPush(String username) {
        this.savePush(username);
    }

    public boolean checkCommits(String username) {
        String token = this.getToken(username);
        List<Map<String, String>> todayCommits
                = this.eventsAPI.commitEvents(this.restTemplate, this.apiURL, username, token);
        if (todayCommits.isEmpty()) {
            return false;
        }
        return true;
    }

    protected void saveUser(Map<String, String> userInfo, String token) {
        User user = User.builder().userId(Long.parseLong(userInfo.get("id")))
                    .email(userInfo.get("email"))
                    .nodeId(userInfo.get("node_id")).token(token)
                    .username(userInfo.get("login")).build();
        this.userRepository.save(user);
    }

    protected void savePush(String username) {
        User user = this.userRepository.findByUsername(username);
        PushAllowed pa = PushAllowed.builder()
                .user(user).build();
        this.paRepository.save(pa);
    }

    public String allEvents(String username, String token) {
        String suffix = "/users/" + username + "/events";
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "token " + token);
        HttpEntity<String> request = new HttpEntity<String>("", headers);

        ResponseEntity<String> response =
                this.restTemplate.exchange((this.apiURL + suffix), HttpMethod.GET, request, String.class);
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
