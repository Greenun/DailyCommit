package com.wessup.daily.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/auth/github/login")
    public String testPost(@RequestBody String body){
        logger.info(body);
        return "Post Test";
    }

    @GetMapping("/commits")
    @ResponseBody
    public String testCommit(@RequestParam("username") String username, @RequestParam("token") String token) {
        // param --> temp
        // token from database..
//        String response = this.userService.commits(username, token);
//        return response;
        this.userService.testCommit(username, token);
        return "test commit";
    }

}
