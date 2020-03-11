package com.wessup.daily.notice.service;

import com.wessup.daily.configuration.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailConsumer {

    private MailService mailSender;

    @Autowired
    public MailConsumer(MailService mailService) {
        this.mailSender = mailService;
    }

    @RabbitListener(queues = RabbitMQConfig.queueName)
    public void consumeMessage(final Message message) {
//        System.out.println(message);
        String email = message.getBody().toString();
        this.sendMail(email);
    }

    protected void sendMail(String email) {
        boolean success = this.mailSender.send(email);
//        if (!success) {
//
//        }
    }
}
