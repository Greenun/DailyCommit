package com.wessup.daily.notice.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.mail.internet.MimeMessage;

// https://velog.io/@hellozin/Spring-Boot%EC%99%80-RabbitMQ-%EC%B4%88%EA%B0%84%EB%8B%A8-%EC%84%A4%EB%AA%85%EC%84%9C

@Service
public class MailService {
    private Logger logger;

    private Sender Sender;

    @Autowired
    public MailService(Sender Sender) {
        this.Sender = Sender;
        this.logger = LoggerFactory.getLogger(MailService.class);
    }

    public boolean send(String userEmail) {
        try {
            ListenableFuture<MimeMessage> result = this.Sender.sendMail(userEmail);
            result.addCallback(m -> logger.info("success"), e -> logger.error(e.getMessage()));
            logger.info("Mail Sent");
        }
        catch (Exception e){
            logger.error("Mail Send Error " + e.getMessage());
        }
        return true;
    }
}
