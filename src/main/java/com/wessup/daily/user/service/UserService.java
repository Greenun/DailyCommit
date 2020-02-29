package com.wessup.daily.user.service;

import com.wessup.daily.user.service.oauth.OAuth;
import com.wessup.daily.user.service.users.UserActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private OAuth auth;

    private UserActivity userActivity;

    @Autowired
    public UserService(OAuth auth, UserActivity userActivity){
        this.auth = auth;
        this.userActivity = userActivity;
    }

    public String auth(){
        return this.auth.githubConfirm();
    }

    public String access(String code) {
        return this.auth.githubAccess(code);
    }

    public String commits(String username, String token) {
        return this.userActivity.allEvents(username, token);
    }

    public void testCommit(String username, String token) {
        this.userActivity.commitEvents(username);
    }

    public void testInfo(String username, String token) {
        this.auth.getEmailByName(username, token);
    }

    public void todayCommit(String username) {
        this.userActivity.commitEvents(username);
    }

    public void allowPush(String username) {
        // set return
    }


}
