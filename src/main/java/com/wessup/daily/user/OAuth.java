package com.wessup.daily.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    public void githubOAuth(String userId){
        String url = this.github + "?client_id=" + this.clientID;
        logger.info(url);
        String response = this.restTemplate.getForObject(url, String.class);
        logger.info(response);
    }
}

