package org.openiam.mq.dto;

import java.io.Serializable;

/**
 * Created by alexander on 11/07/16.
 */
public abstract class AbstractMQMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private  byte[] correlationId;


    public byte[] getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(byte[] correlationId) {
        this.correlationId = correlationId;
    }

}
