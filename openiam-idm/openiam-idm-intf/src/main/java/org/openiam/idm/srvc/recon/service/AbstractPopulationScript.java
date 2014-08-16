package org.openiam.idm.srvc.recon.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractPopulationScript<T> implements PopulationScript<T>, ApplicationContextAware {
    protected String managedSysId;
    protected ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

}
