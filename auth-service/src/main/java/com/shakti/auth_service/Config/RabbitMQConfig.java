package com.shakti.auth_service.Config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    
    @Bean
    public DirectExchange createProfileExchnage(){
        return new DirectExchange("create-profile-exchanger");
    }

    @Bean
    public Queue createProfilQueue() {
        return new Queue("create-profile-Queue", true);
    }

    @Bean
    public Binding bindingCreateProfileQueue(DirectExchange exchange, Queue createProfilQueue) {
        return BindingBuilder.bind(createProfilQueue).to(exchange).with("create-profile");
    }

    // we will use dead letter queue to handle worst case when the RabbitMQ server is down or some
    // other issue occur
}
