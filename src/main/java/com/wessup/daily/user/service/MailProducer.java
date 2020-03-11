package com.wessup.daily.user.service;

import com.wessup.daily.configuration.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailProducer {

    private final RabbitTemplate rabbitTemplate;

    private String exchangeName;

    @Autowired
    public MailProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = RabbitMQConfig.mailExchange;
    }

    public void pushMessageTest() {
        this.rabbitTemplate.convertAndSend(exchangeName, "mail", "test");
    }

    public void MailTask(String email) {
        this.rabbitTemplate.convertAndSend(exchangeName, "mail", email);
    }


}
