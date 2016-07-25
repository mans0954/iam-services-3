package org.openiam.message.utils;

import kafka.admin.AdminOperationException;
import kafka.admin.AdminUtils;
import kafka.common.TopicAndPartition;
import kafka.common.TopicExistsException;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.openiam.message.constants.OpenIAMQueue;
import org.openiam.message.consumer.AbstractKafkaMessageListener;
import org.openiam.message.consumer.AbstractMessageListener;
import org.openiam.message.dto.OpenIAMMQRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.TopicPartitionInitialOffset;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by alexander on 22/07/16.
 */
public class KafkaAdmin {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private String zookeeperHosts;
    private Integer replicationFactor;
    private String brokersAddress;
    private Integer concurrentConsumer;

    public String getZookeeperHosts() {
        return zookeeperHosts;
    }

    public void setZookeeperHosts(String zookeeperHosts) {
        this.zookeeperHosts = zookeeperHosts;
    }

    public Integer getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor(Integer replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    public String getBrokersAddress() {
        return brokersAddress;
    }

    public void setBrokersAddress(String brokersAddress) {
        this.brokersAddress = brokersAddress;
    }

    public Integer getConcurrentConsumer() {
        return concurrentConsumer;
    }

    public void setConcurrentConsumer(Integer concurrentConsumer) {
        this.concurrentConsumer = concurrentConsumer;
    }

    public <Listener extends AbstractKafkaMessageListener> ConcurrentMessageListenerContainer createMessageListenerContainer(OpenIAMQueue topic, Listener listener){
        declareTopic(topic);
        TopicPartitionInitialOffset[] topicPartitions = new TopicPartitionInitialOffset[topic.getPartitionNumber()];
        for(int i=0; i< topic.getPartitionNumber();i++){
            topicPartitions[i]=new TopicPartitionInitialOffset(topic.getQueueName(), i);
        }
        ContainerProperties containerProperties = new ContainerProperties(topicPartitions);
        containerProperties.setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE);
        containerProperties.setMessageListener(listener);
        ConcurrentMessageListenerContainer container = new ConcurrentMessageListenerContainer(getConsumerFactory(getConsumerConfigs(topic.getQueueName()+"Listener")), containerProperties);
        container.setConcurrency(concurrentConsumer);
        container.setAutoStartup(true);
        return container;
    }

    public void declareTopic(OpenIAMQueue topic) {
        this.createTopic(topic.getQueueName(), topic.getPartitionNumber());
    }

    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokersAddress);
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    public Map<String, Object> getConsumerConfigs(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokersAddress);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return props;
    }

    private ConsumerFactory<String, OpenIAMMQRequest> getConsumerFactory(Map<String, Object> props) {
        DefaultKafkaConsumerFactory<String, OpenIAMMQRequest> factory = new DefaultKafkaConsumerFactory<String, OpenIAMMQRequest>(props);
        factory.setValueDeserializer(new JsonDeserializer(OpenIAMMQRequest.class));
        return factory;
    }

    public void createTopic(String topicName, int partitionNum){
        ZkUtils zkUtils = createZKUtils();

        if (!AdminUtils.topicExists(zkUtils, topicName)) {
            try {
                AdminUtils.createTopic(zkUtils, topicName, partitionNum, replicationFactor, new Properties());
                log.info("Topic created. name: {}, partitions: {}, replFactor: {}", topicName,
                        partitionNum, replicationFactor);
            } catch (TopicExistsException ignore) {
                log.info("Topic exists. name: {}", topicName);
            }
        } else {
            log.info("Topic exists. name: {}", topicName);
            if (partitionNum > getNumPartitions(zkUtils, topicName)) {
                try {
                    AdminUtils.addPartitions(zkUtils, topicName, partitionNum, "", true);
                    log.info("Topic altered. name: {}, partitions: {}", topicName, partitionNum);
                } catch (AdminOperationException e) {
                    log.error("Failed to add partitions", e);
                }
            }
        }
    }

    public void deleteTopic(String topicName){
        ZkUtils zkUtils = createZKUtils();
        if (AdminUtils.topicExists(zkUtils, topicName)) {
            log.info("Topic exists. name: {}. Deleting... ", topicName);
            AdminUtils.deleteTopic(zkUtils, topicName);
        }
    }

    private int getNumPartitions(ZkUtils zkUtils, String topicName) {

        Map<TopicAndPartition, Seq<Object>> existingPartitionsReplicaList =
                JavaConverters.mapAsJavaMapConverter(
                        zkUtils.getReplicaAssignmentForTopics(
                                JavaConverters.asScalaBufferConverter(Arrays.asList(topicName)).asScala()
                                        .toSeq())).asJava();
        return existingPartitionsReplicaList.size();
    }
    private ZkUtils createZKUtils(){
        return new ZkUtils(new ZkClient(this.zookeeperHosts, 6000, 6000,
                ZKStringSerializer$.MODULE$), null, false);
    }
}
