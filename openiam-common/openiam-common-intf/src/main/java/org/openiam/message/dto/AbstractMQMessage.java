package org.openiam.message.dto;

import java.io.Serializable;

/**
 * Created by alexander on 11/07/16.
 */
public abstract class AbstractMQMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private String correlationID;


    public String getCorrelationID() {
        return correlationID;
    }

    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }

}
