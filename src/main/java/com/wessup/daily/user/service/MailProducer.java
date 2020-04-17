package com.wessup.daily.user.service;

import com.wessup.daily.notice.service.message.MultiMailMessage;
import com.wessup.daily.configuration.RabbitMQConfig;
import com.wessup.daily.notice.service.message.SingleMailMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MailProducer {

    private final RabbitTemplate rabbitTemplate;

    private String exchangeName;

    private final String mailKey = "mail";

    @Autowired
    public MailProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = RabbitMQConfig.mailExchange;
    }

    public void pushMessageTest() {
        this.rabbitTemplate.convertAndSend(exchangeName, mailKey, "test");
    }

    public void MailTask(String email) {
        SingleMailMessage message = new SingleMailMessage(email);
        this.rabbitTemplate.convertAndSend(exchangeName, mailKey, message);
    }

    public void MailTask(List<String> emails) {
        MultiMailMessage message = new MultiMailMessage(emails);
        this.rabbitTemplate.convertAndSend(exchangeName, mailKey, message);
    }


}
