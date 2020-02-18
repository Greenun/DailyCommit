package com.wessup.daily.notice;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
public class MailController {

    public String sendMail(String email) {
        return "Email Send";
    }
}
