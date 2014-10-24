package org.openiam.provision.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.Executors;

/**
 * The pre-processor allows us to define a set of rules for provisioning users that can be used regardless of how provisioning
 * is triggered. For example, if an attribute just as a job code is supposed to indicate Role Membership, then these rules
 * can be defined in the PreProcessor script. These rules would then be in place for user created through the webconsole, selfserivce,
 * and synchronization
 * <p/>
 * User: suneetshah
 * Date: 5/10/12
 * Time: 10:00 PM
 *
 * @version 2.2
 */
public abstract class AbstractPreProcessor<T> implements ProvisionServicePreProcessor<T> {

    protected ApplicationContext context;

    private static final Log log = LogFactory.getLog(AbstractPostProcessor.class);

    @Value("${openiam.service_base}")
    private String serviceHost;

    @Value("${openiam.idm.ws.path}")
    private String serviceContext;

    private MailService mailService;

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    public void sendEmailNotification(final NotificationRequest request) {

        if (mailService == null) {
            mailService = (MailService) context.getBean("mailService");
        }

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            public void run() {
                mailService.sendNotification(request);
            }
        });
    }
}
