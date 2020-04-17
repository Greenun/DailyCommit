package com.wessup.daily.notice.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.mail.MessagingException;
import java.util.concurrent.Future;

@Service
public class MailService {
    private Logger logger;

    private Sender Sender;

    @Autowired
    public MailService(Sender Sender) {
        this.Sender = Sender;
        this.logger = LoggerFactory.getLogger(MailService.class);
    }

    @Async
    public Future<String> send(String userEmail) {
        try {
            this.Sender.sendMail(userEmail);
            logger.info("Mail Sent");
        }
        catch (InterruptedException | MessagingException e){
            logger.error("Mail Send Error " + e.getMessage());
            return new AsyncResult<String>(userEmail);
        }
        return new AsyncResult<String>(userEmail);
    }
}
