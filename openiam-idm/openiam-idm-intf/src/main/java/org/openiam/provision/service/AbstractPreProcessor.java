package org.openiam.provision.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.provision.request.LookupRequest;
import org.openiam.provision.PreProcessor;
import org.openiam.provision.dto.PasswordSync;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public abstract class AbstractPreProcessor<T> implements PreProcessor<T> {
    protected ApplicationContext context;

    protected static final Log log = LogFactory.getLog(AbstractPreProcessor.class);

    @Value("${openiam.service_base}")
    private String serviceHost;

    @Value("${openiam.idm.ws.path}")
    private String serviceContext;

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public int add(T object, Map<String, Object> bindingMap) {
        return ProvisioningConstants.SUCCESS;
    }

    @Override
    public int modify(T object, Map<String, Object> bindingMap) {
        return ProvisioningConstants.SUCCESS;
    }

    @Override
    public int delete(T object, Map<String, Object> bindingMap) {
        return ProvisioningConstants.SUCCESS;
    }

    @Override
    public int disable(T object, Map<String, Object> bindingMap) {
        return ProvisioningConstants.SUCCESS;
    }

    @Override
    public int setPassword(PasswordSync passwordSync, Map<String, Object> bindingMap) {
        return ProvisioningConstants.SUCCESS;
    }

    @Override
    public int resetPassword(PasswordSync passwordSync, Map<String, Object> bindingMap) {
        return ProvisioningConstants.SUCCESS;
    }

    @Override
    public int lookupRequest(LookupRequest request) {
        return ProvisioningConstants.SUCCESS;
    }
}
