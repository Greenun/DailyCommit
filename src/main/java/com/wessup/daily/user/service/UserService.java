package com.wessup.daily.user.service;

import com.wessup.daily.user.entity.PushAllowed;
import com.wessup.daily.user.repository.PushAllowedRepository;
import com.wessup.daily.user.service.oauth.OAuth;
import com.wessup.daily.user.service.users.UserActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Service
public class UserService {

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    private OAuth auth;

    private UserActivity userActivity;

    private PushAllowedRepository paRepository;

    private MailProducer mailProducer;

    @Value("${jpa.page.size}")
    private Integer pageSize;

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

    public List chooseUser(List users) {
        List<String> mailList = new ArrayList<String>();
        for (Object user: users) {
            if (!this.userActivity.checkCommits(user.toString())) {
                // add to mail list
                mailList.add(user.toString());
            }
        }
        return mailList;
    }

    @Scheduled(fixedDelay = 1000)
    public void fixedTask() {
//        this.mailProducer.pushMessageTest();
        return;
    }

//    removed scheduled job for test
//    @Scheduled(cron = "${cron.mail.rate}")
    public void mailSchedule() {
        long count = this.paRepository.count();
        long iteration = (count % this.pageSize == 0) ? count / this.pageSize: (count / this.pageSize) + 1;
        List<String> usernameList = new LinkedList<String>();
        for (int i = 0; i < (int) iteration; i++) {
            PageRequest page = PageRequest.of(i, this.pageSize); // Sort.by("bka")
            List<PushAllowed> userList = this.paRepository.findAll(page).getContent();
            for (PushAllowed temp: userList) {
                usernameList.add(temp.getUser().getUsername());
            }
            this.mailProducer.MailTask(usernameList);
            usernameList.clear();
        }
    }

    public String commits(String username, String token) {
        return this.userActivity.allEvents(username, token);
    }

    public void test() {
        mailSchedule();
    }

    public void testCommit(String username, String token) {
        this.userActivity.commitEvents(username);
    }


    public void allowPush(String username) {
        logger.debug(String.format("Push Allowed User: %s", username));
        this.userActivity.savePush(username);
    }

    public void saveDummyData() {
        Random r = new Random();
        r.setSeed(System.nanoTime());
        this.userActivity.saveDummyData(r.nextInt(32));
    }

}
