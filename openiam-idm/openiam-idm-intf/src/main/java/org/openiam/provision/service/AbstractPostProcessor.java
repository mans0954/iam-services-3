package org.openiam.provision.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.openiam.provision.dto.PasswordSync;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public abstract class AbstractPostProcessor<T> implements PostProcessor<T> {
    protected ApplicationContext context;
    protected MuleContext muleContext;
    private static final Log log = LogFactory.getLog(AbstractPostProcessor.class);

    @Value("${openiam.service_base}")
    private String serviceHost;

    @Value("${openiam.idm.ws.path}")
    private String serviceContext;

    public void setMuleContext(MuleContext ctx) {
        muleContext = ctx;
    }
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public int add(T object, Map<String, Object> bindingMap, boolean success) {
        return ProvisioningConstants.SUCCESS;
    }

    @Override
    public int modify(T object, Map<String, Object> bindingMap, boolean success) {
        return ProvisioningConstants.SUCCESS;
    }

    @Override
    public int delete(T object, Map<String, Object> bindingMap, boolean success) {
        return ProvisioningConstants.SUCCESS;
    }

    @Override
    public int disable(T object, Map<String, Object> bindingMap, boolean success) {
        return ProvisioningConstants.SUCCESS;
    }

    @Override
    public int setPassword(PasswordSync passwordSync, Map<String, Object> bindingMap, boolean success) {
        return ProvisioningConstants.SUCCESS;
    }

    @Override
    public int resetPassword(PasswordSync passwordSync, Map<String, Object> bindingMap, boolean success) {
        return ProvisioningConstants.SUCCESS;
    }
}
