package org.openiam.idm.srvc.synch.srcadapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.context.MuleContextAware;
import org.mule.module.client.MuleClient;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.LoginDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.synch.dto.SyncResponse;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.service.IdentitySynchService;
import org.openiam.idm.srvc.synch.service.SourceAdapter;
import org.openiam.idm.srvc.synch.service.SyncConstants;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.MuleContextProvider;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

/**
 * Abstract class which all Source System adapters must extend
 * User: suneetshah
 * Date: 3/10/11
 * Time: 6:18 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSrcAdapter implements SourceAdapter {

    protected final long SHUTDOWN_TIME = 5000;

    private static final Log log = LogFactory.getLog(AbstractSrcAdapter.class);

    @Autowired
    protected String systemAccount;
    @Autowired
    protected UserDataService userManager;
    @Autowired
    protected LoginDataService loginManager;
    @Autowired
    protected LoginDozerConverter loginDozerConverter;
    @Autowired
    protected RoleDataService roleDataService;
    @Autowired
    @Qualifier("defaultProvision")
    protected ProvisionService provService;
    @Autowired
    protected UserDozerConverter userDozerConverter;
    @Autowired
    protected MatchRuleFactory matchRuleFactory;
    @Autowired
    protected IdentitySynchService synchService;
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;

    @Value("${org.openiam.idm.system.user.id}")
    protected String systemUserId;

    @Value("${openiam.service_base}")
    private String serviceHost;
    
    @Value("${openiam.idm.ws.path}")
    private String serviceContext;

    public abstract SyncResponse startSynch(SynchConfig config, AuditLogBuilder auditLogBuilder);

    public void addUser(ProvisionUser pUser) {
        long startTime = System.currentTimeMillis();

        Map<String, String> msgPropMap = new HashMap<String, String>();
        msgPropMap.put("SERVICE_HOST", serviceHost);
        msgPropMap.put("SERVICE_CONTEXT", serviceContext);

        try {
            // Create the client with the context
            MuleClient client = new MuleClient(MuleContextProvider.getCtx());
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
            MuleClient client = new MuleClient(MuleContextProvider.getCtx());
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

    protected void setCurrentSuperiors(ProvisionUser pUser) {
        if (StringUtils.isNotEmpty(pUser.getUserId())) {
            List<UserEntity> entities = userManager.getSuperiors(pUser.getUserId(), -1, -1);
            List<User> superiors = userDozerConverter.convertToDTOList(entities, true);
            if (CollectionUtils.isNotEmpty(superiors)) {
                pUser.setSuperiors(new HashSet<User>(superiors));
            }
        }
    }

}
