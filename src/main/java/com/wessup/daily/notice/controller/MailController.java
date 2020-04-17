package com.wessup.daily.notice.controller;

import com.wessup.daily.notice.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/x")
public class MailController {

//    private MailService mailService;
//
//    @Autowired
//    public MailController(MailService mailService) {
//        this.mailService = mailService;
//    }
//
//    @GetMapping("/test")
//    public String sendMail(@RequestParam("email") String email) {
//        boolean result = this.mailService.send(email);
//        if (result) {
//            return "Email Sent";
//        }
//        else{
//            return "Email Sending Failed";
//        }
//    }
}
