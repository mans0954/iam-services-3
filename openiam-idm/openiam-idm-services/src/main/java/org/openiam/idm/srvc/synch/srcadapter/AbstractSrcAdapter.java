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
import org.mule.api.MuleException;
import org.mule.module.client.MuleClient;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.LoginDozerConverter;
import org.openiam.dozer.converter.SynchReviewDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.file.ws.FileWebService;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;
import org.openiam.idm.srvc.synch.domain.SynchReviewRecordEntity;
import org.openiam.idm.srvc.synch.domain.SynchReviewRecordValueEntity;
import org.openiam.idm.srvc.synch.dto.*;
import org.openiam.idm.srvc.synch.service.*;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.MuleContextProvider;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

/**
 * Abstract class which all Source System adapters must extend
 * User: suneetshah
 * Date: 3/10/11
 * Time: 6:18 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSrcAdapter implements SourceAdapter {

    protected final Object mutex = new Object();

    protected final long SHUTDOWN_TIME = 5000;

    private static final Log log = LogFactory.getLog(AbstractSrcAdapter.class);

    @Autowired
    protected String systemAccount;
    @Autowired
    protected UserDataWebService userDataWebService;
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
    protected SynchReviewDozerConverter synchReviewDozerConverter;

    @Autowired
    protected FileWebService fileWebService;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;

    @Value("${org.openiam.idm.system.user.id}")
    protected String systemUserId;

    @Value("${openiam.service_base}")
    private String serviceHost;

    @Value("${openiam.idm.ws.path}")
    private String serviceContext;

    public abstract SyncResponse startSynch(SynchConfig config);

    /**
     * Starts synchronization from SynchReview as data source
     */
    protected SyncResponse startSynchReview(
            SynchConfig config,
            SynchReviewEntity sourceReview,
            SynchReviewEntity resultReview,
            ValidationScript validationScript,
            List<TransformScript> transformScripts,
            MatchObjectRule matchRule) {

    	if(log.isDebugEnabled()) {
    		log.debug("SynchReview startSynch CALLED.^^^^^^^^");
    	}
        final SynchReviewService synchReviewService = (SynchReviewService)SpringContextProvider.getBean("synchReviewService");
        final LineObject rowHeader = genHeaderFromRecord(synchReviewService.getHeaderReviewRecord(sourceReview.getSynchReviewId()));
        try {
            for (SynchReviewRecordEntity record : sourceReview.getReviewRecords()) {
                if (!record.isHeader()) {
                    final LineObject rowObj = genLineObjectFromRecord(record, rowHeader);
                    processLineObject(rowObj, config, resultReview, validationScript, transformScripts, matchRule);
                }
            }

        } finally {
            if (resultReview != null) {
                if (CollectionUtils.isNotEmpty(resultReview.getReviewRecords())) { // add header row
                    resultReview.addRecord(generateSynchReviewRecord(rowHeader, true));
                }
            }
        }
        if(log.isDebugEnabled()) {
        	log.debug("SYNCH REVIEW SYNCHRONIZATION COMPLETE^^^^^^^^");
        }
        return new SyncResponse(ResponseStatus.SUCCESS);
    }

    public abstract SyncResponse startSynch(SynchConfig config, SynchReviewEntity sourceReview, SynchReviewEntity resultReview);

    public void addUser(ProvisionUser pUser) {
        long startTime = System.currentTimeMillis();

        Map<String, Object> msgPropMap = new HashMap<String, Object>();
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
        if(log.isDebugEnabled()) {
        	log.debug("--AddUser:SynchAdapter execution time="+ (endTime - startTime));
        }
    }

    public void modifyUser(ProvisionUser pUser) {

        long startTime = System.currentTimeMillis();

        Map<String, Object> msgPropMap = new HashMap<String, Object>();
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
        if(log.isDebugEnabled()) {
        	log.debug("--ModifyUser:SynchAdapter execution time=" + (endTime - startTime));
        }
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
        if (StringUtils.isNotEmpty(pUser.getId())) {
            List<User> superiors = userDataWebService.getSuperiors(pUser.getId(), -1, -1);
            if (CollectionUtils.isNotEmpty(superiors)) {
                pUser.setSuperiors(new HashSet<User>(superiors));
            }
        }
    }

     protected void processLineObject(
            LineObject rowObj,
            SynchConfig config,
            SynchReviewEntity resultReview,
            ValidationScript validationScript,
            List<TransformScript> transformScripts,
            MatchObjectRule matchRule) {

        if (validationScript != null) {
            synchronized (mutex) {
                int retval = validationScript.isValid(rowObj);
                if (retval == ValidationScript.NOT_VALID) {
                    if(log.isDebugEnabled()) {
                        log.debug(" - Validation failed...transformation will not be called.");
                    }
                    return;
                }
                if (retval == ValidationScript.SKIP) {
                    return;

                } else if (retval == ValidationScript.SKIP_TO_REVIEW) {
                    if (resultReview != null) {
                        resultReview.addRecord(generateSynchReviewRecord(rowObj));
                    }
                    return;
                }
            }
        }

        Map<String, Attribute> rowAttr = rowObj.getColumnMap();
         if(log.isDebugEnabled()) {
             log.debug(" - Row Attr..." + rowAttr);
         }
        User usr = null;
        boolean skipUser = false;
        synchronized (mutex) {
            try {
                usr = matchRule.lookup(config, rowAttr);
            }
            catch (IllegalArgumentException e)
            {
                log.error("matchAttrName and matchAttrValue can not be blank");
                skipUser=true;
            }
            catch (NullPointerException npe){
                log.error("Wrong matchAttrName in sync configuration or empty matchAttrValue ");
                skipUser=true;
            }
        }

        long startTime = System.currentTimeMillis();

        int retval = -1;
        ProvisionUser pUser = (usr != null)? new ProvisionUser(usr) : new ProvisionUser();
        pUser.setRequestorUserId(systemUserId);
        pUser.setRequestorLogin("sysadmin");
        pUser.setParentAuditLogId(config.getParentAuditLogId());
        if (transformScripts != null && transformScripts.size() > 0) {
            for (TransformScript transformScript : transformScripts) {
                synchronized (mutex) {
                    if(!skipUser){
                    transformScript.init();
                    // initialize the transform script
                    if (usr != null) {
                        transformScript.setNewUser(false);
                        setCurrentSuperiors(pUser);
                        transformScript.setUser(usr);
                        transformScript.setPrincipalList(loginDozerConverter.convertToDTOList(loginManager.getLoginByUser(usr.getId()), false));
//                        transformScript.setUserRoleList(roleDataService.getUserRolesAsFlatList(usr.getId()));
                        transformScript.setUserRoleList(null);
                    } else {
                        transformScript.setNewUser(true);
                        transformScript.setUser(null);
                        transformScript.setPrincipalList(null);
                        transformScript.setUserRoleList(null);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug(" - Execute transform script");
                    }
                    //Disable PRE and POST processors/performance optimizations
                    pUser.setSkipPreprocessor(true);
                    pUser.setSkipPostProcessor(true);
                    retval = transformScript.execute(rowObj, pUser);
                    if (log.isDebugEnabled()) {
                        log.debug("Transform result=" + retval);
                    }
                }else // skipping user
                {
                    String matchAttrName = config.getCustomMatchAttr();
                    log.error("User was skipped during sync. Attribute name=["+matchAttrName +"] is not presented in target system");
                }
                }
                if (log.isInfoEnabled()) {
                    log.debug(" - Execute complete transform script");
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("================ After Transformation => " + (System.currentTimeMillis() - startTime));
            }

            if (retval != -1) {
                if (retval == TransformScript.SKIP_TO_REVIEW) {
                    if (resultReview != null) {
                        resultReview.addRecord(generateSynchReviewRecord(rowObj));
                    }

                } else if (retval == TransformScript.DELETE && pUser.getUser() != null) {
                    provService.deleteByUserId(pUser.getId(), UserStatusEnum.REMOVE, systemAccount);

                } else {
                    // call prov service
                    if (retval != TransformScript.DELETE) {
                        if (usr != null) {
                            if (log.isDebugEnabled()) {
                                log.debug(" - Updating existing user");
                            }
                            pUser.setId(usr.getId());
                            try {

                                provService.modifyUser(pUser);
                                if (log.isDebugEnabled()) {
                                    log.debug("================ After Modify => " + (System.currentTimeMillis() - startTime));
                                }
                            } catch (Throwable e) {
                                log.error(e);
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug(" - New user is being provisioned");
                            }
                            pUser.setId(null);
                            try {
                                provService.addUser(pUser);
                                if (log.isDebugEnabled()) {
                                    log.debug("================ After Add => " + (System.currentTimeMillis() - startTime));
                                }
                            } catch (Exception e) {
                                log.error(e);
                            }
                        }
                    }
                }
            }
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            log.error("The thread was interrupted when sleep paused after row [" + rowObj + "] execution.", e);
        }
    }

    protected SynchReviewRecordEntity generateSynchReviewRecord(LineObject rowObj) {
        return generateSynchReviewRecord(rowObj, false);
    }

    protected SynchReviewRecordEntity generateSynchReviewRecord(LineObject rowObj, boolean isHeader) {
        if (rowObj != null) {
            SynchReviewRecordEntity record = new SynchReviewRecordEntity();
            record.setHeader(isHeader);
            Map<String, Attribute> columnsMap = rowObj.getColumnMap();
            for (String key : columnsMap.keySet()) {
                SynchReviewRecordValueEntity reviewValue = new SynchReviewRecordValueEntity();
                if (!isHeader) {
                    Attribute attribute = columnsMap.get(key);
                    if (attribute != null) {
                        reviewValue.setValue(attribute.getValue());
                    }
                } else {
                    reviewValue.setValue(key);
                }
                record.addValue(reviewValue);
            }
            return record;
        }
        return null;
    }

    protected LineObject genHeaderFromRecord(SynchReviewRecord record) {
        LineObject lineObject = new LineObject();
        int ctr =0;
        if (record != null && CollectionUtils.isNotEmpty(record.getReviewValues())) {
            for (SynchReviewRecordValue v : record.getReviewValues()) {
                Attribute a = new Attribute(v.getValue(), null);
                a.setType("STRING");
                a.setColumnNbr(ctr);
                lineObject.put(a.getName(), a);
                ctr++;
            }
        }
        return lineObject;
    }

    protected LineObject genLineObjectFromRecord(SynchReviewRecordEntity record, LineObject lineHeader) {
        LineObject lineObject = new LineObject();
        int ctr = 0;
        List<SynchReviewRecordValueEntity> values = record.getReviewValues();
        for (String name : lineHeader.getColumnMap().keySet()) {
            String v = values.get(ctr).getValue();
            Attribute a = new Attribute(name, (v != null) ? v : "");
            a.setType("STRING");
            a.setColumnNbr(ctr);
            lineObject.put(a.getName(), a);
            ctr++;
        }
        return lineObject;
    }

}
