package org.openiam.idm.srvc.synch.service;

import org.openiam.idm.srvc.synch.dto.LineObject;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.dto.SynchReview;
import org.springframework.context.ApplicationContext;

public abstract class AbstractValidationScript implements ValidationScript {

    protected ApplicationContext context;
    protected String synchConfigId;
    protected SynchConfig config;
    protected SynchReview review;

    public abstract int isValid(LineObject rowObj);

    public ApplicationContext getContext() {
        return context;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    public String getSynchConfigId() {
        return synchConfigId;
    }

    public void setSynchConfigId(String synchConfigId) {
        this.synchConfigId = synchConfigId;
    }

    public SynchConfig getConfig() {
        return config;
    }

    public void setConfig(SynchConfig config) {
        this.config = config;
    }

    public SynchReview getReview() {
        return review;
    }

    public void setReview(SynchReview review) {
        this.review = review;
    }
}
