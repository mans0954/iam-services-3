package org.openiam.config;

import org.openiam.mq.ActivitiMessageListener;
import org.openiam.mq.constants.queue.OpenIAMQueue;
import org.openiam.mq.utils.RabbitMQAdminUtils;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@Import(BaseConfiguration.class)
@ImportResource(value={"classpath:bpm-context.xml"})
public class BaseActivitiConfiguration {
//    @Autowired
//    private RabbitMQAdminUtils rabbitMQAdminUtils;

//    @Bean
//    @Autowired
//    public SimpleMessageListenerContainer activitiMessageListenerContainer(ActivitiMessageListener listener, ConnectionFactory connectionFactory) {
//        return rabbitMQAdminUtils.createMessageListenerContainer("activitiMessageListenerContainer",
//                OpenIAMQueue.ActivitiQueue,  listener, connectionFactory);
//    }

}
