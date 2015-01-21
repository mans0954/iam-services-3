package org.openiam.provision.service;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.dozer.converter.LoginDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.pswd.service.PasswordService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Base class that will be extended by all the helper classes that will be used by the DefaultProvisioningService
 */
public class BaseProvisioningHelper {
    @Autowired
    protected UserDataService userMgr;
    @Autowired
    protected LoginDataService loginManager;
    @Autowired
    protected LoginDAO loginDao;
    @Autowired
    protected ManagedSystemWebService managedSysService;
    @Autowired
    protected ManagedSystemService managedSysDaoService;
    @Autowired
    protected RoleDataService roleDataService;
    @Autowired
    protected GroupDataService groupManager;
    @Autowired
    @Qualifier("connectorWsdl")
    protected String connectorWsdl;
    @Autowired
    @Qualifier("defaultProvisioningModel")
    protected String defaultProvisioningModel;
    @Autowired
    protected SysConfiguration sysConfiguration;
    @Autowired
    protected ResourceDataService resourceDataService;
    @Autowired
    protected OrganizationDataService orgManager;
    @Autowired
    protected PasswordService passwordDS;
    @Autowired
    protected ConnectorAdapter connectorAdapter;
    @Autowired
    protected ProvisionQueueService provQueueService;

    @Autowired
    protected AuditLogService auditLogService;

    @Autowired
    @Qualifier("transactionManager")
    protected PlatformTransactionManager platformTransactionManager;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;
    
    @Autowired
    protected ProvisionConnectorWebService connectorService;

    @Autowired
    protected LoginDozerConverter loginDozerConverter;

    @Autowired
    protected UserDozerConverter userDozerConverter;

    @Value("${openiam.service_base}")
    private String serviceHost;
    
    @Value("${openiam.idm.ws.path}")
    private String serviceContext;

    protected static final Log log = LogFactory
            .getLog(BaseProvisioningHelper.class);

    public void setCurrentSuperiors(ProvisionUser pUser) {
        if (StringUtils.isNotEmpty(pUser.getId())) {
            List<UserEntity> entities = userMgr.getSuperiors(pUser.getId(), 0, Integer.MAX_VALUE);
            if (CollectionUtils.isNotEmpty(entities)) {
                List<User> superiors = userDozerConverter.convertToDTOList(entities, false);
                pUser.setSuperiors(new HashSet<User>(superiors));
            }
        }
    }

    protected String getResProperty(Set<ResourceProp> resPropSet,
            String propertyName) {
        String value = null;

        if (resPropSet == null) {
            return null;
        }
        Iterator<ResourceProp> propIt = resPropSet.iterator();
        while (propIt.hasNext()) {
            ResourceProp prop = propIt.next();
            if (prop.getName().equalsIgnoreCase(propertyName)) {
                return prop.getValue();
            }
        }

        return value;
    }

    /* Helper methods for Pre and Post processing scripts */
    protected PreProcessor createPreProcessScript(String scriptName) {
        try {
            return (PreProcessor) scriptRunner.instantiateClass(null, scriptName);
        } catch (Exception ce) {
            log.error(ce);
            return null;

        }

    }

    protected PostProcessor createPostProcessScript(String scriptName) {
        try {
            return (PostProcessor) scriptRunner.instantiateClass(null, scriptName);
        } catch (Exception ce) {
            log.error(ce);
            return null;

        }

    }

    protected int executePreProcess(PreProcessor ppScript,
            Map<String, Object> bindingMap, ProvisionUser user, String operation) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(user, bindingMap);
        }
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(user, bindingMap);
        }
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(user, bindingMap);
        }
        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(bindingMap);
        }

        return 0;

    }

    protected int executePostProcess(PostProcessor ppScript,
            Map<String, Object> bindingMap, ProvisionUser user,
            String operation, boolean success) {
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(user, bindingMap, success);
        }
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(user, bindingMap, success);

        }
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(user, bindingMap, success);

        }

        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(bindingMap, success);
        }
        return 0;

    }

    protected ObjectResponse delete(Login mLg, String requestId,
            ManagedSysDto mSys, ManagedSystemObjectMatch matchObj) {

        CrudRequest<ExtensibleUser> request = new CrudRequest<ExtensibleUser>();

        request.setObjectIdentity(mLg.getLogin());
        request.setRequestID(requestId);
        request.setTargetID(mLg.getManagedSysId());
        request.setHostLoginId(mSys.getUserId());
        request.setHostLoginPassword(mSys.getDecryptPassword());
        request.setHostUrl(mSys.getHostUrl());
        if (matchObj != null) {
            request.setBaseDN(matchObj.getBaseDn());
        }
        request.setOperation("DELETE");

        request.setScriptHandler(mSys.getDeleteHandler());

        ObjectResponse resp = connectorAdapter.deleteRequest(mSys, request);

        return resp;


    }


}
