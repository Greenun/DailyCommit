package com.wessup.daily.user.controller;

import com.wessup.daily.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

// response type - json needs.

//@RestController
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
        String body = this.userService.auth();
        return "redirect:"+body;
    }

    @GetMapping("/auth/github/login")
    @ResponseBody
    public String testRedirect(@RequestParam("code") String code){
        logger.info("redirect!");
        String response = this.userService.access(code);
        return response;
    }

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

    @GetMapping("/test/info")
    @ResponseBody
    public String testInfo(@RequestParam("username") String username, @RequestParam("token") String token) {
        this.userService.testInfo(username, token);
        return "test info";
    }

    @GetMapping("/commit")
    @ResponseBody
    public String getCommit(@RequestParam("username") String username) {
        this.userService.todayCommit(username);
        return "";
    }

    @GetMapping("/allow/push/{username}")
    @ResponseBody
    public String allowPush(@PathVariable("username") String username) {
        this.userService.allowPush(username);
        return "save";
    }

}
