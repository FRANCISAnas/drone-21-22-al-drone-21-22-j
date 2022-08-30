package com.jolydrone.flightmonitor.configurations;

import com.jolydrone.flightmonitor.services.UpdatePositionMonitorBean;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroneLocationUpdateListenerConfiguration {

    @Value("${regular.update.route}")
    private String routingKey;

    @Value("${topic.exchange.drone.flightmonitor}")
    private String topicExchangeName;


    @Bean("exchangeForUpdateLocation")
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName, true, false);
    }

    @Bean("queueForUpdateLocation")
    @Qualifier("queueForUpdateLocation")
    Queue queueForUpdateLocation() {
        return new AnonymousQueue();
    }

    @Bean("bindingForUpdateLocation")
    Binding binding(Queue queueForUpdateLocation, @Qualifier("exchangeForUpdateLocation") TopicExchange exchange) {

        System.out.println("Binding to routing Key" + this.routingKey);

        return BindingBuilder.bind(queueForUpdateLocation).to(exchange).with(routingKey);
    }

    @Bean("listenerAdapterForUpdateLocation")
    MessageListenerAdapter listenerAdapter(UpdatePositionMonitorBean positionUpdateMonitorBean) {
        return new MessageListenerAdapter(positionUpdateMonitorBean);
    }

    @Bean("containerForUpdateLocation")
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             @Qualifier("listenerAdapterForUpdateLocation") MessageListenerAdapter listenerAdapter,
                                             Queue queueForUpdateLocation) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConcurrency("1-10");
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueForUpdateLocation.getActualName());
        container.setMessageListener(listenerAdapter);
        return container;
    }


}
