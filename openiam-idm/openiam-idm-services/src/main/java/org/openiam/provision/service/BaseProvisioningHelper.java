package org.openiam.provision.service;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.openiam.base.SysConfiguration;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.dozer.converter.LoginDozerConverter;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditHelper;
import org.openiam.idm.srvc.audit.service.IdmAuditLogDataService;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.pswd.service.PasswordService;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.MuleContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

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
    protected IdmAuditLogDataService auditDataService;
    @Autowired
    protected ManagedSystemWebService managedSysService;
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
    protected AuditHelper auditHelper;
    @Autowired
    protected ConnectorAdapter connectorAdapter;
    @Autowired
    protected RemoteConnectorAdapter remoteConnectorAdapter;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;
    
    @Autowired
    protected ProvisionConnectorWebService connectorService;

    @Autowired
    protected LoginDozerConverter loginDozerConverter;

    @Value("${openiam.service_base}")
    private String serviceHost;
    
    @Value("${openiam.idm.ws.path}")
    private String serviceContext;

    protected static final Log log = LogFactory
            .getLog(BaseProvisioningHelper.class);

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
                return prop.getPropValue();
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
            return ppScript.addUser(user, bindingMap);
        }
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modifyUser(user, bindingMap);
        }
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.deleteUser(user, bindingMap);
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
            return ppScript.addUser(user, bindingMap, success);
        }
        if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modifyUser(user, bindingMap, success);

        }
        if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.deleteUser(user, bindingMap, success);

        }

        if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(bindingMap, success);
        }
        return 0;

    }

    protected ResponseType localDelete(Login l, String requestId, ManagedSysDto mSys, ProvisionUser user,
            IdmAuditLog auditLog) {

        log.debug("Local delete for=" + l);

        CrudRequest reqType = new CrudRequest();
        reqType.setRequestID(requestId);

        ResponseType resp = connectorAdapter.deleteRequest(mSys, reqType,
                MuleContextProvider.getCtx());

        String logid = null;
        String status = null;

        if (resp.getStatus() != null) {
            status = resp.getStatus().toString();
        }

        if (auditLog != null) {
            logid = auditLog.getLogId();
        }

        auditHelper.addLog("DELETE IDENTITY", user.getRequestorDomain(), user.getRequestorLogin(),
                "IDM SERVICE", user.getCreatedBy(), l.getManagedSysId(),
                "IDENTITY", user.getUserId(),
                logid, status, logid,
                "IDENTITY_STATUS", "DELETED",
                requestId, resp.getErrorCodeAsStr(), user.getSessionId(), resp.getErrorMsgAsStr(),
                user.getRequestClientIP(), l.getLogin(), l.getDomainId());

        return resp;


    }

    protected ObjectResponse remoteDelete(Login mLg, String requestId,
            ManagedSysDto mSys, ProvisionConnectorDto connector,
            ManagedSystemObjectMatch matchObj, ProvisionUser user,
            IdmAuditLog auditLog) {

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

        ObjectResponse resp = remoteConnectorAdapter.deleteRequest(mSys, request, connector, MuleContextProvider.getCtx());

        auditHelper.addLog("DELETE IDENTITY", auditLog.getDomainId(), auditLog.getPrincipal(),
                "IDM SERVICE", user.getCreatedBy(), mLg.getManagedSysId(),
                "IDENTITY", user.getUserId(),
                auditLog.getLogId(), resp.getStatus().toString(), auditLog.getLogId(), "IDENTITY_STATUS",
                "DELETED",
                requestId, resp.getErrorCodeAsStr(), user.getSessionId(), resp.getErrorMsgAsStr(),
                user.getRequestClientIP(), mLg.getLogin(), mLg.getDomainId());

        return resp;


    }

}
