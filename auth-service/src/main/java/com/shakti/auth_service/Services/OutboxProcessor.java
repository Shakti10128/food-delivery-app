package com.shakti.auth_service.Services;

import java.util.List;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.shakti.auth_service.Entity.OutboxEvent;
import com.shakti.auth_service.Repository.OutboxRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OutboxProcessor {

    private final RabbitTemplate rabbitTemplate;
    private final OutboxRepository outboxRepository;

    // @Scheduled(fixedDelay = 5000) // runs every 5 sec
    public void processOutbox() {

        // all the events which are not publishing
        List<OutboxEvent> events = outboxRepository.findByPublished(false);

        // publishing the events
        for (OutboxEvent event : events) {
            try {
                rabbitTemplate.convertAndSend(
                    "create-profile-exchanger", // exchanger name
                    "create-profile", // routingKey of queue
                    event.getPayload() 
                );

                // mark as published
                event.setPublished(true);
                outboxRepository.save(event);

            } catch (AmqpException e) {
                // RabbitMQ still down, will retry next cycle
            }
        }
    }
}

