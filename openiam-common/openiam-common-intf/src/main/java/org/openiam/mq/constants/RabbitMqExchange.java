package org.openiam.mq.constants;

/**
 * @author Alexander Dukkardt
 * 
 */
public enum RabbitMqExchange {
    COMMON_EXCHANGE,
    METADATA_ELEMENT_EXCHANGE,
    CHECK_LISTENER_EXCHANGE(RabbitMqExchangeType.FANOUT);

    private final RabbitMqExchangeType type;

    /**
     * 
     */
    private RabbitMqExchange() {
        type = RabbitMqExchangeType.DIRECT;
    }

    /**
     * @return the type
     */
    public RabbitMqExchangeType getType() {
        return type;
    }

    /**
     * 
     */
    private RabbitMqExchange(RabbitMqExchangeType type) {
        this.type = type;
    }
}
