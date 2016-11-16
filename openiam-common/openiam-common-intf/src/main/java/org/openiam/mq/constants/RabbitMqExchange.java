package org.openiam.mq.constants;

import org.springframework.amqp.core.ExchangeTypes;

/**
 * @author Alexander Dukkardt
 * 
 */
public enum RabbitMqExchange {
    COMMON_EXCHANGE,
    METADATA_ELEMENT_EXCHANGE,

    CHECK_LISTENER_EXCHANGE(ExchangeTypes.FANOUT),


    /*Exchanges for AM vhost*/
    AM_EXCHANGE,
    REFRESH_OAUTH_CACHE_EXCHANGE(ExchangeTypes.FANOUT),
    URI_FEDERATION_CACHE_EXCHANGE(ExchangeTypes.FANOUT),
    AM_CACHE_EXCHANGE(ExchangeTypes.FANOUT);

    private final String type;

    /**
     * 
     */
    private RabbitMqExchange() {
        type = ExchangeTypes.DIRECT;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     */
    private RabbitMqExchange(String type) {
        this.type = type;
    }
}
