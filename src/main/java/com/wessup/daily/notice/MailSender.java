package com.wessup.daily.notice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class MailSender {

    private JavaMailSender emailSender;

    @Autowired
    public MailSender(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendMail(String userEmail) {

    }
}
