package com.wessup.daily.notice.service;

import com.wessup.daily.notice.service.message.CustomMessage;
import com.wessup.daily.configuration.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
public class MailConsumer {

    private MailService mailSender;

    @Autowired
    public MailConsumer(MailService mailService) {
        this.mailSender = mailService;
    }

    @RabbitListener(queues = RabbitMQConfig.queueName)
    public void consumeMessage(final CustomMessage message) {
        Object body = message.getBody();
        if (body instanceof List) {
            this.sendMail((List)body);
        }
        else {
            this.sendMail((String)body);
        }
    }

    protected void sendMail(String email) {
        Future<String> success = this.mailSender.send(email);
        try {
            String result = success.get();
        }
        catch (InterruptedException | ExecutionException e) {
            // handle future error
        }
    }

    protected void sendMail(List<String> emailList) {
        // need more inv for completable future
        List<Future> futures = new LinkedList<>();
        List<String> succeed = new LinkedList<>(); // temp
        for (String email: emailList) {
            futures.add(this.mailSender.send(email));
        }

        for (Future<String> success: futures) {
            try {
                String email = success.get();
                succeed.add(email);
            }
            catch (InterruptedException | ExecutionException e) {
                // handle error
            }
        }

    }

}
