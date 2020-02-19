package com.wessup.daily.notice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public class MailService {
    private Logger logger;

    private Sender Sender;

    @Autowired
    public MailService(Sender Sender) {
        this.Sender = Sender;
        this.logger = LoggerFactory.getLogger(Sender.class);
    }

    public boolean send(String userEmail) {
        try {
            this.Sender.sendMail(userEmail);
            logger.info("Mail Sent");
        }
        catch (MessagingException e){
            logger.error("Mail Send Error");
            return false;
        }
        return true;
    }
}
