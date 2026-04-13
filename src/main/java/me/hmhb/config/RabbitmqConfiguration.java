package me.hmhb.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class RabbitmqConfiguration {

    @Bean
    public Exchange exchange() {
        return ExchangeBuilder
                .directExchange("test-exchange")
                .build();
    }

    @Bean
    public Exchange deadLetterExchange() {
        return ExchangeBuilder
                .directExchange("test-dead-exchange")
                .build();
    }

    @Bean
    public Queue queue() {
        return QueueBuilder
                .durable("test-queue")
                .deadLetterExchange("test-dead-exchange")
                .deadLetterRoutingKey("test-dead-routing-key")
                .maxPriority(10)
                .ttl(10000)
                .build();
    }

    @Bean
    public Queue deadQueue() {
        return QueueBuilder
                .durable("test-dead-queue")
                .build();
    }

    @Bean
    public Binding binding(Queue queue, Exchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("test-routing-key")
                .noargs();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadQueue, Exchange deadLetterExchange) {
        return BindingBuilder
                .bind(deadQueue)
                .to(deadLetterExchange)
                .with("test-dead-routing-key")
                .noargs();
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setMandatory(true);

        // 1. 确认消息是否到达 Exchange（ConfirmCallback）
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            String msgId = correlationData != null ? correlationData.getId() : "unknown";
            if (ack) {
                log.info("消息成功到达Exchange, msgId: {}", msgId);
            } else {
                log.error("消息未到达Exchange, msgId: {}, cause: {}", msgId, cause);
                // 这里可以做重发或记录到数据库等补偿操作
            }
        });

        // 2. 确认消息是否正确路由到 Queue（ReturnsCallback）
        // 只有消息无法路由到队列时才会触发
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("消息路由失败! exchange: {}, routingKey: {}, replyCode: {}, replyText: {}, message: {}",
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    returned.getReplyCode(),
                    returned.getReplyText(),
                    returned.getMessage());
            // 这里可以做重发、记录日志或告警等处理
        });

        return rabbitTemplate;
    }
}
