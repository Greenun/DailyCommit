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
                        UserRepository userRepository, PushAllowedRepository paRepository,
                        EventsAPI eventsAPI, UsersAPI usersAPI) {
        this.restTemplate = restTemplateBuilder.build();
        this.logger = LoggerFactory.getLogger(UserActivity.class);
        this.userRepository = userRepository;
        this.paRepository = paRepository;
        this.eventsAPI = eventsAPI;
        this.usersAPI = usersAPI;
    }

    protected String getToken(String username) {
        User user = this.userRepository.findByUsername(username);
        return user.getToken();
    }

    public boolean getUser(String token) {
        try {
            Map<String, Object> userInfo = this.usersAPI.getUser(this.restTemplate, this.apiURL, token);
            this.saveUser(userInfo, token);
            return true;
        }
        catch (Exception e) {
            logger.error(e.toString());
            return false;
        }
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

    protected void saveUser(Map<String, Object> userInfo, String token) {
        User user = User.builder().userId(Long.valueOf((Integer)userInfo.get("id")))
                    .email(userInfo.get("email").toString())
                    .nodeId(userInfo.get("node_id").toString()).token(token)
                    .username(userInfo.get("login").toString()).build();
        this.userRepository.save(user);
    }

    public void savePush(String username) {
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

    public void saveDummyData(int count) {
        Random r = new Random();
        r.setSeed(System.nanoTime());
        StringBuffer buffer = new StringBuffer();
        String str = "";
        int temp = 0;
        List<User> dummies = new LinkedList<User>();
        List<PushAllowed> pDummies = new LinkedList<PushAllowed>();
        for (int c = 0; c < count; c++) {
            for (int i = 0; i < 8; i++) {
                int rint = r.nextInt(58) + 65;
                rint = (rint > 90 && rint < 97)? rint + (r.nextInt(10) + 6) : rint;
                buffer.append((char) rint);
                temp += rint;
                str = buffer.toString();
            }
            // save dummies to list
            User user = User.builder().username(str)
                    .email(String.format("%s@%s", str, "gmail.com"))
                    .userId(Long.valueOf(temp))
                    .nodeId(str)
                    .token(str)
                    .build();
            dummies.add(user);
            pDummies.add(PushAllowed.builder().user(user).build());
            // flush buffer
            buffer.delete(0, buffer.length());
            temp = 0;
        }
        this.userRepository.saveAll(dummies);
        // add to push allowed
        this.paRepository.saveAll(pDummies);
    }
}
