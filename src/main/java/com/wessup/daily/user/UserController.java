package com.wessup.daily.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/auth")
    public String testAuth(@RequestParam("id") String userId){
        this.userService.auth(userId);
        return "test";
    }

    @RequestMapping("/auth/github/login")
    public String testRedirect(@RequestParam("access_token") String token){
        logger.info("redirect!");
        logger.info(token);
        return "Test!";
    }
}
