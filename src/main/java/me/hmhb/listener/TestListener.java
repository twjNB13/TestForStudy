package me.hmhb.listener;

import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.hmhb.entity.dto.User;
import me.hmhb.service.TestService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class TestListener {

    private static final HashSet<String> set = new HashSet<>();

    private static final int MAX_RETRY_COUNT = 3;
    private static final String RETRY_COUNT_HEADER = "x-retry-count";

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private TestService testService;

    @RabbitListener(queues = "test-queue")
    public void process(User user, Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        int retryCount = getRetryCount(message);

        try {
            log.info("收到消息: {}, 重试次数: {}", user, retryCount);
            System.out.println(new Date());

            // 模拟业务处理
            // doSomething(user);
            testService.insertUser(user);

            channel.basicAck(deliveryTag, false);
            log.info("消息处理成功, deliveryTag: {}", deliveryTag);

        } catch (Exception e) {
            log.error("消息处理失败: {}", e.getMessage());
            channel.basicAck(deliveryTag, false);  // 先确认原消息

            if (retryCount < MAX_RETRY_COUNT) {
                // 未达到最大重试次数，重新发送到原队列
                retryMessage(user, retryCount + 1, "test-exchange", "test-routing-key", message.getMessageProperties().getCorrelationId());
                log.warn("消息重试中, 当前次数: {}", retryCount + 1);
            } else {
                // 达到最大重试次数，发送到死信队列
                sendToDeadLetter(user, retryCount, message.getMessageProperties().getCorrelationId());
                log.error("消息重试耗尽，进入死信队列");
            }
        }
    }

    private int getRetryCount(Message message) {
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        Object count = headers.get(RETRY_COUNT_HEADER);
        return count == null ? 0 : (int) count;
    }

    private void retryMessage(User user, int retryCount, String exchange, String routingKey, String id) {
        CorrelationData correlationData = new CorrelationData(id);

        rabbitTemplate.convertAndSend(exchange, routingKey, user, msg -> {
            msg.getMessageProperties().setHeader(RETRY_COUNT_HEADER, retryCount);
            msg.getMessageProperties().setCorrelationId(id);
            return msg;
        }, correlationData);
    }

    private void sendToDeadLetter(User user, int retryCount, String id) {
        CorrelationData correlationData = new CorrelationData(id);
        rabbitTemplate.convertAndSend("test-dead-exchange", "test-dead-routing-key", user, msg -> {
            msg.getMessageProperties().setHeader(RETRY_COUNT_HEADER, retryCount);
            msg.getMessageProperties().setHeader("x-failure-reason", "达到最大重试次数");
            msg.getMessageProperties().setCorrelationId(id);
            return msg;
        }, correlationData);
    }

    @RabbitListener(queues = "test-dead-queue")
    public void processDead(User user, Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        int retryCount = getRetryCount(message);

        log.warn("死信队列收到消息: {}, 已重试: {}次", user, retryCount);
        System.out.println(new Date());

        // 处理死信消息（记录日志、告警等）
        channel.basicAck(deliveryTag, false);
    }
}
