package com.wessup.daily.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OAuth {
    private static final Logger logger = LoggerFactory.getLogger(OAuth.class);

    @Value("${github.client.id}")
    private String clientID;

    @Value("${github.client.secret}")
    private String clientSecret;

    public void githubOAuth(){
        logger.info("test");

    }
}

