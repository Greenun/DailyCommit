package com.wessup.daily.notice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
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
    public ListenableFuture<MimeMessage> sendMail(String userEmail) throws MessagingException, InterruptedException {

        MimeMessage message = this.emailSender.createMimeMessage();
        message.setSubject("Test Email Send");
        message.setFrom(new InternetAddress(this.smtpId));
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(userEmail));
        message.setText("Test Send");
        message.setSentDate(new Date());

        this.emailSender.send(message);
        return new AsyncResult<>(message);

    }

    public void sendMail(String userEmail, File attachments) throws MessagingException{
        MimeMessage message = this.emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
    }
}
