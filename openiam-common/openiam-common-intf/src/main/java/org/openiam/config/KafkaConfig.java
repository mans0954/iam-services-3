package org.openiam.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.openiam.message.constants.OpenIAMQueue;
import org.openiam.message.consumer.AbstractMessageListener;
import org.openiam.message.dto.OpenIAMMQRequest;
import org.openiam.message.utils.KafkaAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexander on 21/07/16.
 */
@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${spring.kafka.host}")
    private String brokersAddress;
    @Value("${spring.kafka.concurrent.consumer}")
    private Integer concurrentConsumer;
    @Value("${spring.kafka.zookeeper.host}")
    private String zookeeperHosts;
    @Value("${spring.kafka.topic.replication.factor}")
    private Integer replicationFactor;

    @Bean
    public KafkaAdmin kafkaAdmin(){
        KafkaAdmin admin = new KafkaAdmin();
        admin.setZookeeperHosts(zookeeperHosts);
        admin.setReplicationFactor(replicationFactor);
        admin.setConcurrentConsumer(concurrentConsumer);
        admin.setBrokersAddress(brokersAddress);
        return  admin;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaAdmin().producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        KafkaTemplate<String, Object> template = new KafkaTemplate<String, Object>(producerFactory());
        template.setMessageConverter(new StringJsonMessageConverter());
        return template;
    }

}
