package org.openiam.idm.srvc.meta.service;

import org.openiam.util.SpringContextProvider;
import org.springframework.context.ApplicationContext;

/**
 * Created by alexander on 11/01/16.
 */
public abstract class AbstractTemplateGlobalDataModel implements  TemplateGlobalDataModel{
    protected ApplicationContext context;

    protected AbstractTemplateGlobalDataModel() {
        this.context=SpringContextProvider.getApplicationContext();
    }
}
