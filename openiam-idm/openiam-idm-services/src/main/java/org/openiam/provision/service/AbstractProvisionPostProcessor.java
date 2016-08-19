package org.openiam.provision.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.NotificationRequest;
import org.openiam.provision.dto.PasswordSync;
import org.openiam.srvc.common.MailService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.Executors;

/**
 * The pre-processor allows us to define a set of rules for provisioning users that can be used regardless of how provisioning
 * is triggered. For example, if an attribute just as a job code is supposed to indicate Role Membership, then these rules
 * can be defined in the PreProcessor script. These rules would then be in place for user created through the webconsole, selfserivce,
 * and synchronization
 *
 * User: suneetshah
 * Date: 5/10/12
 * Time: 10:00 PM
 * @version 2.2
 */
public abstract class AbstractProvisionPostProcessor<T> implements ProvisionServicePostProcessor<T> {
    protected ApplicationContext context;

    protected static final Log log = LogFactory.getLog(AbstractProvisionPostProcessor.class);

    @Value("${openiam.service_base}")
    private String serviceHost;
    
    @Value("${openiam.idm.ws.path}")
    private String serviceContext;

    private MailService mailService;

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    public void sendEmailNotification(final NotificationRequest request) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            public void run() {
                mailService.sendNotification(request);
            }
        });
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
    public int setPassword(PasswordSync passwordSync, Map<String, Object> bindingMap) {
        return ProvisioningConstants.SUCCESS;
    }

    @Override
    public int resetPassword(PasswordSync passwordSync, Map<String, Object> bindingMap) {
        return ProvisioningConstants.SUCCESS;
    }

    @Override
    public int disable(T object, Map<String, Object> bindingMap) {
        return ProvisioningConstants.SUCCESS;
    }
}
