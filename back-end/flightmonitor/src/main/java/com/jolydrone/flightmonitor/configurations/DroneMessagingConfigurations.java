package com.jolydrone.flightmonitor.configurations;

import com.jolydrone.flightmonitor.services.SpecialEventMonitorWS;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroneMessagingConfigurations {


    @Value("${special.update.route}")
    private String routingKey;

    @Value("${topic.exchange.drone.flightmonitor}")
    private String topicExchangeName;

    @Bean("exchangeForSpecialEvent")
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName, true, false);
    }

    @Bean("specialEventQueue")
    @Qualifier("specialEventQueue")
    Queue specialEventQueue() {
        return new Queue(this.routingKey);
    }

    @Bean("bindingForSpecialEvent")
    Binding binding(@Qualifier("specialEventQueue") Queue specialEventQueue, @Qualifier("exchangeForSpecialEvent") TopicExchange exchange) {
        System.out.println("Binding to routing Key bindingForSpecialEvent:" + this.routingKey);
        return BindingBuilder.bind(specialEventQueue).to(exchange).with(this.routingKey);
    }

    @Bean("listenerAdapterForSpecialEvent")
    MessageListenerAdapter listenerAdapter(SpecialEventMonitorWS monitorWS) {
        return new MessageListenerAdapter(monitorWS);
    }

    @Bean("containerForSpecialEvent")
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             @Qualifier("listenerAdapterForSpecialEvent") MessageListenerAdapter listenerAdapter,
                                             @Qualifier("specialEventQueue") Queue specialEventQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(specialEventQueue.getActualName());
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(listenerAdapter);
        return container;
    }
}
