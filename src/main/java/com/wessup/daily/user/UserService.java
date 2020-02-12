package com.wessup.daily.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private OAuth auth;

    @Autowired
    public UserService(OAuth auth){
        this.auth = auth;
    }

    public void auth(String userId){
        this.auth.githubOAuth(userId);
    }
}
