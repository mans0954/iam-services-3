package org.openiam.idm.srvc.recon.service;

import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;

public abstract class ReconciliationObjectAbstractCommand<T> implements ReconciliationObjectCommand<T>, ApplicationContextAware {
    protected ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public abstract boolean execute(ReconciliationSituation config, String principal, String managedSysId, T object, List<ExtensibleAttribute> attributes);


}
