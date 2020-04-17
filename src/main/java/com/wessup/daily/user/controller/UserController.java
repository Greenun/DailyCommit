package com.wessup.daily.user.controller;

import com.wessup.daily.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/auth")
    public String testAuth(){
        String body = this.userService.oAuthRedirect();
        return "redirect:"+body;
    }

    @GetMapping("/auth/github/login")
    @ResponseBody
    public String oauthRedirect(@RequestParam("code") String code){
        String response = this.userService.oAuthLogin(code);
        if (response == null) {
            return "Failed";
        }
        return response;
    }

    // for test
    @GetMapping("/test/commits")
    @ResponseBody
    public String testCommit(@RequestParam("username") String username, @RequestParam("token") String token) {
        // param --> temp
        // token from database..
//        String response = this.userService.commits(username, token);
//        return response;
        this.userService.testCommit(username, token);
        return "test commit";
    }

    // for test
    @GetMapping("/mail/send")
    @ResponseBody
    public String getMailingList() {
        this.userService.mailSchedule();
        return "test";
    }

    @GetMapping("/generate")
    @ResponseBody
    public String generateDummies() {
        this.userService.saveDummyData();
        return "test";
    }

    @GetMapping("/allow/push/{username}")
    @ResponseBody
    public String allowPush(@PathVariable("username") String username) {
        this.userService.allowPush(username);
        return "saved";
    }

}
