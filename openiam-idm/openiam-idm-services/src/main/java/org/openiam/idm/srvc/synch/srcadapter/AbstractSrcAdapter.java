package org.openiam.idm.srvc.synch.srcadapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.context.MuleContextAware;
import org.mule.module.client.MuleClient;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.srvc.audit.service.AuditHelper;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.synch.dto.SyncResponse;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.service.SourceAdapter;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Abstract class which all Source System adapters must extend
 * User: suneetshah
 * Date: 3/10/11
 * Time: 6:18 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSrcAdapter implements SourceAdapter, MuleContextAware, ApplicationContextAware {

    protected final long SHUTDOWN_TIME = 5000;

    private static final Log log = LogFactory.getLog(AbstractSrcAdapter.class);

    static protected ResourceBundle res = ResourceBundle.getBundle("datasource");

    static protected ApplicationContext applicationContext;

    protected MuleContext muleContext;

    @Autowired
    protected String systemAccount;
    @Autowired
    protected AuditHelper auditHelper;
    @Autowired
    protected UserDataService userManager;
    @Autowired
    protected LoginDataService loginManager;
    @Autowired
    protected RoleDataService roleDataService;
    @Autowired
    @Qualifier("defaultProvision")
    protected ProvisionService provService;
    @Autowired
    protected UserDozerConverter userDozerConverter;
    @Autowired
    protected MatchRuleFactory matchRuleFactory;

    @Value("${openiam.service_base}")
    private String serviceHost;
    
    @Value("${openiam.idm.ws.path}")
    private String serviceContext;

    public abstract SyncResponse startSynch(SynchConfig config);

    public void addUser(ProvisionUser pUser) {
        long startTime = System.currentTimeMillis();

        Map<String, String> msgPropMap = new HashMap<String, String>();
        msgPropMap.put("SERVICE_HOST", serviceHost);
        msgPropMap.put("SERVICE_CONTEXT", serviceContext);

        try {
            // Create the client with the context
            MuleClient client = new MuleClient(muleContext);
            client.sendAsync("vm://provisionServiceAddMessage",
                    (ProvisionUser) pUser, msgPropMap);

        } catch (MuleException me) {

            log.error(me.getMessage());
        }
        long endTime = System.currentTimeMillis();
        log.debug("--AddUser:SynchAdapter execution time="
                + (endTime - startTime));
    }

    public void modifyUser(ProvisionUser pUser) {

        long startTime = System.currentTimeMillis();

        Map<String, String> msgPropMap = new HashMap<String, String>();
        msgPropMap.put("SERVICE_HOST", serviceHost);
        msgPropMap.put("SERVICE_CONTEXT", serviceContext);

        try {
            // Create the client with the context
            MuleClient client = new MuleClient(muleContext);
            client.sendAsync("vm://provisionServiceModifyMessage",
                    (ProvisionUser) pUser, msgPropMap);

        } catch (MuleException me) {

            log.error(me.getMessage());
        }
        long endTime = System.currentTimeMillis();
        log.debug("--ModifyUser:SynchAdapter execution time="
                + (endTime - startTime));
    }

    /**
     * This method used for awaiting while all threads will be finished.
     * Used in multithreading
     *
     * @param results
     * @throws InterruptedException
     */
    protected void waitUntilWorkDone(List<Future> results) throws InterruptedException {
        int successCounter = 0;
        while(successCounter != results.size()) {
            successCounter = 0;
            for(Future future : results) {
                if(future.isDone()) {
                    successCounter ++;
                }
            }
            Thread.sleep(500);
        }
    }

    public void setMuleContext(MuleContext muleContext) {
        this.muleContext = muleContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
