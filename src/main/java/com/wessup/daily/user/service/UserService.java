package com.wessup.daily.user.service;

import com.wessup.daily.user.repository.PushAllowedRepository;
import com.wessup.daily.user.service.oauth.OAuth;
import com.wessup.daily.user.service.users.UserActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    private OAuth auth;

    private UserActivity userActivity;

    private PushAllowedRepository paRepository;

    private MailProducer mailProducer;

    @Autowired
    public UserService(OAuth auth, UserActivity userActivity,
                       PushAllowedRepository paRepository, MailProducer mailProducer){
        this.auth = auth;
        this.userActivity = userActivity;
        this.paRepository = paRepository;
        this.mailProducer = mailProducer;
    }

    public String oAuthRedirect(){
        return this.auth.githubConfirm();
    }

    public String oAuthLogin(String code) {
        String token = this.auth.githubAccess(code);
        boolean success = this.userActivity.getUser(token);
        if (!success) {
            // handle fail
            return null;
        }
        return token;
    }

    public void todayCommit(String username) {
        boolean check = this.userActivity.checkCommits(username);
        if (!check) {
            // add to mailing list
        }
        // pass
    }

    @Scheduled(fixedDelay = 1000)
    public void testSchedule() {
        this.mailProducer.pushMessageTest();
    }

    @Scheduled(cron = "${cron.mail.rate}")
    public void MailSchedule() {

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

    public void allowPush(String username) {
        // set return
    }


}
