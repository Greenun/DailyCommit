package com.wessup.daily.user.service;

import com.wessup.daily.user.entity.User;
import com.wessup.daily.user.repository.UserRepository;
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
import java.util.HashMap;
import java.util.Map;

// rest template http request
// handshake everytime...
// https://a1010100z.tistory.com/entry/SpringBoot-RestTemplate-vs-Webclient%EC%9E%91%EC%84%B1%EC%A4%91

@Component
public class OAuth {
    private static final Logger logger = LoggerFactory.getLogger(OAuth.class);

    private UserRepository userRepository;

    private final RestTemplate restTemplate;

    @Autowired
    public OAuth(RestTemplateBuilder restTemplateBuilder, UserRepository userRepository){
        this.userRepository = userRepository;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Value("${github.client.id}")
    private String clientID;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.url}")
    private String github;

    @Value("${github.oauth.access}")
    private String tokenURL;

    @Value("${github.api}")
    private String apiURL;

    public String githubConfirm(){
        String url = this.github + "?client_id=" + this.clientID;
        // scope setting
        url += "&scope=user%20repo";

        logger.info(url);

        return url;
    }

    protected HttpHeaders setHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "token " + token);
        return headers;
    }

    protected ResponseEntity<HashMap> apiExchange(String url, String token, HttpMethod method) {
        HttpHeaders headers = this.setHeaders(token);
        HttpEntity<String> request = new HttpEntity("", headers);
        ResponseEntity<HashMap> response = this.restTemplate.exchange(url, method, request, HashMap.class);

        return response;
    }

    public String githubAccess(String code){
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("code", code);
        body.add("client_id", this.clientID);
        body.add("client_secret", this.clientSecret);
        RequestEntity<MultiValueMap> request = RequestEntity
                .post(URI.create(this.tokenURL))
                .accept(MediaType.APPLICATION_JSON)
                .body(body);
        HashMap<String, String> info = this.restTemplate.postForObject(this.tokenURL, request, HashMap.class);

        logger.info(info.get("access_token"));
        this.getUserInfo(info.get("access_token"));
        return info.get("access_token");
    }

    public Map<String ,String> getUserInfo(String token) {
        String suffix = "/user";
        ResponseEntity<HashMap> response = this.apiExchange(this.apiURL + suffix, token, HttpMethod.GET);
//        HttpHeaders headers = this.setHeaders(token);
//
//        HttpEntity<String> request = new HttpEntity("", headers);
//
//        ResponseEntity<HashMap> response =
//                this.restTemplate.exchange(this.apiURL + suffix, HttpMethod.GET, request, HashMap.class);
        HashMap<String ,String> userInfo = response.getBody();
        logger.info(userInfo.get("access_token"));
        userInfo.put("token", token);
        // one more request with username (for email)
        return userInfo;
    }

    public Map<String, String> getInfoByName(String username, String token) {
        String suffix = "/users/" + username;
        ResponseEntity<HashMap> response = this.apiExchange(this.apiURL + suffix, token, HttpMethod.GET);

        logger.info(response.getBody().toString()); // temp (for test)
        return response.getBody();
    }

    public void saveUser(Map<String, String> info) {
        String username = info.get("login");
        String token = info.get("token");
        User user = User.builder().email(info.get("")).username(username).token(token).build();

    }
}

