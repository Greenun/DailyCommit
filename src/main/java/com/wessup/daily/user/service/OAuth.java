package com.wessup.daily.user.service;

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

    private final RestTemplate restTemplate;

    @Autowired
    public OAuth(RestTemplateBuilder restTemplateBuilder){
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

    public String githubConfirm(){
        String url = this.github + "?client_id=" + this.clientID;
        // scope setting
        url += "&scope=user%20repo";

        logger.info(url);

        return url;
//        String response = this.restTemplate.getForObject(url, String.class);
//        ClientHttpResponse response = this.restTemplate.getForObject(url, ClientHttpResponse.class);
//        HttpEntity<String> entity = new HttpEntity<String>("");
//        ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//        HttpHeaders headers = response.getHeaders();
//        Set<String> keys = headers.keySet();
//        for (String key: keys) {
//            logger.info(key + ": " + String.valueOf(headers.get(key)));
//        }
//        String body = response.getBody();
//        logger.info(body);
//        return body;
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
        String url = "https://api.github.com/user";
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "token " + token);
        HttpEntity<String> request = new HttpEntity("", headers);

        ResponseEntity<HashMap> response =
                this.restTemplate.exchange(url, HttpMethod.GET, request, HashMap.class);
        HashMap<String ,String> userInfo = response.getBody();
        logger.info(userInfo.get("access_token"));
        return userInfo;
    }
}

