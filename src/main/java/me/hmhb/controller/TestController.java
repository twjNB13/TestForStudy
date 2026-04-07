package me.hmhb.controller;

import jakarta.annotation.Resource;
import me.hmhb.entity.dto.User;
import me.hmhb.service.TestService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private TestService testService;

    @GetMapping("/getUsers")
    public List<User> getUsers() {
        return testService.getUsers();
    }

    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/t")
    public void test() {
        User user1 = new User();
        user1.setId(1);
        user1.setName("test1");
        user1.setAge(18);

        User user2 = new User();
        user2.setId(2);
        user2.setName("test2");
        user2.setAge(18);

        String msgId2 = UUID.randomUUID().toString();
        CorrelationData correlationData2 = new CorrelationData(msgId2);


        rabbitTemplate.convertAndSend("test-exchange", "test-routing-key", user1, message -> {
            message.getMessageProperties().setPriority(5);
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            message.getMessageProperties().setCorrelationId(msgId2);
            return message;
        }, correlationData2);

        String msgId1 = UUID.randomUUID().toString();
        CorrelationData correlationData1 = new CorrelationData(msgId1);

        rabbitTemplate.convertAndSend("test-exchange", "test-routing-key", user2, message -> {
            message.getMessageProperties().setPriority(10);
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            message.getMessageProperties().setCorrelationId(msgId1);
            return message;
        }, correlationData1);
        System.out.println(new Date());
    }
}
