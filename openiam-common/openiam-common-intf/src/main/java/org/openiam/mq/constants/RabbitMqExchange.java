package org.openiam.mq.constants;

import org.springframework.amqp.core.ExchangeTypes;

/**
 * @author Alexander Dukkardt
 * 
 */
public enum RabbitMqExchange {
    /*Exchanges for COMMON vhost*/
    COMMON_EXCHANGE,
    /*Exchanges for AM vhost*/
    AM_EXCHANGE,
    REFRESH_OAUTH_CACHE_EXCHANGE(ExchangeTypes.FANOUT),
    URI_FEDERATION_CACHE_EXCHANGE(ExchangeTypes.FANOUT),
    AM_CACHE_EXCHANGE(ExchangeTypes.FANOUT),

    /*Exchanges for ACTIVITI vhost*/
    ACTIVITI_EXCHANGE,

    /*Exchanges for AUDIT vhost*/
    AUDIT_EXCHANGE,
    /*Exchanges for IDM vhost*/
    IDM_EXCHANGE,

    /*Exchanges for CONNECTOR vhost*/
    CONNECTOR_EXCHANGE,
    /*Exchanges for USER vhost*/
    USER_EXCHANGE
    ;


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
