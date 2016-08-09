package org.openiam.base.request;

import java.io.Serializable;

/**
 * Created by alexander on 08/08/16.
 */
public class BaseServiceRequest implements Serializable{
    private static final long serialVersionUID = 1L;

    private String requesterId;
    private String languageId;

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }
}
