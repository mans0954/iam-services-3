package org.openiam.mq.listener;

import com.rabbitmq.client.Channel;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.queue.MqQueue;
import org.openiam.mq.constants.queue.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.gateway.RequestServiceGateway;
import org.openiam.mq.gateway.impl.RequestServiceGatewayImpl;
import org.openiam.util.OpenIAMUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by alexander on 27/07/16.
 */
public abstract class AbstractRabbitMQListener<API extends OpenIAMAPI> extends AbstractMessageListener<BaseServiceRequest, API> implements ChannelAwareMessageListener{
    @Autowired
    @Qualifier("rabbitRequestServiceGateway")
    private RequestServiceGateway requestServiceGateway;

    public AbstractRabbitMQListener(MqQueue queueToListen) {
        super(queueToListen);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            log.debug("{} caught message", this.getClass().getSimpleName());
            log.debug("Message : {}", message);
            log.debug("Channel : {}", channel);
            MQRequest<BaseServiceRequest, API> request = (MQRequest<BaseServiceRequest, API>) ((RequestServiceGatewayImpl)requestServiceGateway).getRabbitTemplate().getMessageConverter().fromMessage(message);

            byte[] correlationId = message.getMessageProperties().getCorrelationId();
            log.info("Caught request in listener: {} correlationId: {}", request, OpenIAMUtils.byteArrayToString(correlationId));
            boolean isAsync = StringUtils.isBlank(request.getReplyTo());
            doOnMessage(request, correlationId,isAsync);

        } catch (RejectMessageException ex) {
            log.warn(ex.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}
