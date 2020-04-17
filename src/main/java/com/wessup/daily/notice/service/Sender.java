package com.wessup.daily.notice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@Component
public class Sender {

    private JavaMailSender emailSender;

    @Value("${smtp.naver.id}")
    private String smtpId;

    @Autowired
    public Sender(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Async
    public void sendMail(String userEmail) throws MessagingException, InterruptedException {
        System.out.println("Thread: " + Thread.currentThread().getId());
//        MimeMessage message = this.emailSender.createMimeMessage();
//        message.setSubject("Test Email Send");
//        message.setFrom(new InternetAddress(this.smtpId));
//        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(userEmail));
//        message.setText("Test Send");
//        message.setSentDate(new Date());


//        this.emailSender.send(message);
    }

    // multipart support
//    public void sendMail(String userEmail, File attachments) throws MessagingException{
//        MimeMessage message = this.emailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true);
//    }
}
