package com.jolydrone.flightmonitor.configurations;

import com.jolydrone.flightmonitor.services.MayDayMonitorBean;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroneMayDayListenerConfiguration {

    @Value("${topic.exchange.drone.flightmonitor}")
    private String topicExchangeName;

    @Value("${queue.drone.update.mayday.flightmonitor.name}")
    private String queueName;

    @Bean("exchangeForMayDay")
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName, true, false);
    }

    @Bean("queueForMayDay")
    @Qualifier("queueForMayDay")
    Queue queueForMayDay() {
        return new Queue(queueName, false);
    }

    @Bean("bindingForUpdateMayDay")
    Binding binding(Queue queueForMayDay, @Qualifier("exchangeForMayDay") TopicExchange exchange) {
        return BindingBuilder.bind(queueForMayDay).to(exchange).with("drone.special.may.day");
    }

    @Bean("listenerAdapterForMayDay")
    MessageListenerAdapter listenerAdapter(MayDayMonitorBean mayDayMonitorBean) {
        return new MessageListenerAdapter(mayDayMonitorBean);
    }

    @Bean("containerForMayDay")
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             @Qualifier("listenerAdapterForMayDay") MessageListenerAdapter listenerAdapter, Queue queueForMayDay) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueForMayDay.getActualName());

        container.setMessageListener(listenerAdapter);
        return container;
    }

}
