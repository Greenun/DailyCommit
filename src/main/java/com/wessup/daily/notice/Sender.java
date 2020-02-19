package com.wessup.daily.notice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;

@Component
public class Sender {

    private JavaMailSender emailSender;

    @Autowired
    public Sender(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendMail(String userEmail) throws MessagingException {
        MimeMessage message = this.emailSender.createMimeMessage();
        message.setSubject("Test Email Send");
        message.setFrom(new InternetAddress("iopuy1234@naver.com"));
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(userEmail));
        message.setText("Test Send");
        message.setSentDate(new Date());

        this.emailSender.send(message);
    }

    public void sendMail(String userEmail, File attachments) throws MessagingException{
        MimeMessage message = this.emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
    }
}
