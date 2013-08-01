package org.openiam.spml2.spi.gapps;

import com.google.gdata.client.appsforyourdomain.UserService;
import com.google.gdata.data.appsforyourdomain.AppsForYourDomainException;
import com.google.gdata.data.appsforyourdomain.Login;
import com.google.gdata.data.appsforyourdomain.Name;
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.LookupAttributeResponse;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.request.*;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.dozer.converter.ManagedSystemObjectMatchDozerConverter;
import org.openiam.idm.srvc.audit.service.IdmAuditLogDataService;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemObjectMatchDAO;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.jws.WebParam;
import javax.naming.directory.ModificationItem;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Provisioning connector for Google Apps
 * @author suneet
 *
 */
@Deprecated
//@WebService(endpointInterface = "org.openiam.spml2.interf.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector", portName = "GoogleAppsConnectorServicePort", serviceName = "GoogleAppsConnectorService")
public class GoogleAppsConnectorImpl  {

    private static final Log log = LogFactory
            .getLog(GoogleAppsConnectorImpl.class);

    protected ManagedSystemWebService managedSysService;
    protected ManagedSystemObjectMatchDAO managedSysObjectMatchDao;
    protected ResourceDataService resourceDataService;
    @Autowired
    protected IdmAuditLogDataService auditDataService;
    protected LoginDataService loginManager;
    @Autowired
    protected PolicyDataService policyDataService;
    protected UserDataService userManager;
    @Autowired
    protected ManagedSystemObjectMatchDozerConverter managedSystemObjectMatchDozerConverter;

    private static final String APPS_FEEDS_URL_BASE = "https://apps-apis.google.com/a/feeds/";

    public boolean testConnection(String targetID) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Value("${KEYSTORE}")
    private String trustStore;
    
    @Value("${KEYSTORE_PSWD}")
    private String trustStorePassword;

    public void init() {
        String filename = System.getProperty("java.home")
                + "/lib/security/cacerts".replace('/', File.separatorChar);
        System.out.println("filenname=" + filename);
        String password = "changeit";
        System.setProperty("javax.net.ssl.trustStore", trustStore);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
    }

    public ObjectResponse add(CrudRequest reqType) {
        String userName = null;
        String password = null;
        String givenName = null;
        String lastName = null;

        init();

        log.debug("Google Apps: add request called..");

        String requestID = reqType.getRequestID();
         userName = reqType.getUserIdentity();

        /* targetID - */
        String targetID = reqType.getTargetID();

        /*
         * A) Use the targetID to look up the connection information under
         * managed systems
         */
        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);
        ManagedSystemObjectMatch matchObj = null;
        List<ManagedSystemObjectMatchEntity> matchObjList = managedSysObjectMatchDao
                .findBySystemId(targetID, "USER");
        if (matchObjList != null && matchObjList.size() > 0) {
            matchObj = managedSystemObjectMatchDozerConverter.convertToDTO(matchObjList.get(0),false);
        }

        ExtensibleObject obj = reqType.getUser();


        List<ExtensibleAttribute> attrList = obj.getAttributes();
        for (ExtensibleAttribute att : attrList) {

            log.debug("Attr Name=" + att.getName() + " " + att.getValue());

            String name = att.getName();
            String value = att.getValue();

            if (name.equalsIgnoreCase("password")) {
                password = value;

            }
            if (name.equalsIgnoreCase("firstName")) {
                givenName = value;

            }
            if (name.equalsIgnoreCase("lastName")) {
                lastName = value;

            }
        }


        try {
            UserService userService = new UserService(
                    "gdata-sample-AppsForYourDomain-UserService");

            log.debug("google connector login:" + managedSys.getUserId());
            log.debug("google connector PASSWORD:"
                    + managedSys.getDecryptPassword());

            userService.setUserCredentials(managedSys.getUserId(),
                    managedSys.getDecryptPassword());

            UserEntry entry = new UserEntry();
            Login login = new Login();
            login.setUserName(userName);
            login.setPassword(password);
            entry.addExtension(login);

            Name name = new Name();
            name.setGivenName(givenName);
            name.setFamilyName(lastName);
            entry.addExtension(name);

            String domainUrlBase = APPS_FEEDS_URL_BASE + matchObj.getBaseDn()
                    + "/user/2.0";

            log.debug("BASE URL=" + APPS_FEEDS_URL_BASE);

            URL insertUrl = new URL(domainUrlBase);
            userService.insert(insertUrl, entry);

        } catch (ServiceException e) {
            e.printStackTrace();
            log.error(e.getStackTrace());

            ObjectResponse response = new ObjectResponse();
            response.setStatus(StatusCodeType.FAILURE);
            response.setError(ErrorCode.ALREADY_EXISTS);
            return response;

        } catch (MalformedURLException e) {
            ObjectResponse response = new ObjectResponse();
            response.setError(ErrorCode.MALFORMED_REQUEST);
            log.error(e.getStackTrace());
            return response;

        } catch (IOException e) {
            ObjectResponse response = new ObjectResponse();
            response.setError(ErrorCode.UNSUPPORTED_OPERATION);
            log.error(e.getStackTrace());
            return response;
        }

        ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        return response;

    }

    public ObjectResponse modify(CrudRequest reqType) {
        String userName = null;
        String firstName = null;
        String lastName = null;
        boolean change = false;

        init();

        String requestID = reqType.getRequestID();
        /*
         * PSO - Provisioning Service Object - - ID must uniquely specify an
         * object on the target or in the target's namespace - Try to make the
         * PSO ID immutable so that there is consistency across changes.
         */

        userName = reqType.getUserIdentity();

        /* targetID - */
        String targetID = reqType.getTargetID();

        /*
         * A) Use the targetID to look up the connection information under
         * managed systems
         */
        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);
        ManagedSystemObjectMatch matchObj = null;
        List<ManagedSystemObjectMatchEntity> matchObjList = managedSysObjectMatchDao
                .findBySystemId(targetID, "USER");
        if (matchObjList != null && matchObjList.size() > 0) {
            matchObj = managedSystemObjectMatchDozerConverter.convertToDTO(matchObjList.get(0),false);
        }

        // check if its a rename request
        ExtensibleAttribute origIdentity = isRename(reqType.getUser());
        if (origIdentity != null) {
            log.debug("Renaming identity: " + origIdentity.getValue());

            ObjectResponse respType = renameIdentity(userName,
                    origIdentity.getValue(), managedSys, matchObj);
            if (respType.getStatus() == StatusCodeType.FAILURE) {
                return respType;
            }
        } else {

            // get the firstName and lastName values
                ExtensibleObject obj = reqType.getUser();


                    log.debug("Object:" + obj.getName() + " - operation="
                            + obj.getOperation());

                    List<ExtensibleAttribute> attrList = obj.getAttributes();
                    List<ModificationItem> modItemList = new ArrayList<ModificationItem>();
                    for (ExtensibleAttribute att : attrList) {
                        if (att.getOperation() != 0 && att.getName() != null) {
                            if (att.getName().equalsIgnoreCase("firstName")) {
                                firstName = att.getValue();
                                change = true;
                            }
                            if (att.getName().equalsIgnoreCase("lastName")) {
                                lastName = att.getValue();
                                change = true;
                            }
                        }
                    }


            // assign to google
            if (change) {
                UserService userService = new UserService(
                        "gdata-sample-AppsForYourDomain-UserService");

                try {
                    userService.setUserCredentials(managedSys.getUserId(),
                            managedSys.getDecryptPassword());
                    String domainUrlBase = APPS_FEEDS_URL_BASE
                            + matchObj.getBaseDn() + "/user/2.0";
                    URL updateUrl = new URL(domainUrlBase + "/" + userName);

                    UserEntry entry = new UserEntry();

                    Name n = new Name();
                    if (firstName != null) {
                        n.setGivenName(firstName);
                    }
                    if (lastName != null) {
                        n.setFamilyName(lastName);
                    }
                    entry.addExtension(n);

                    userService.update(updateUrl, entry);

                } catch (AuthenticationException e) {
                    log.error(e);
                    ObjectResponse respType = new ObjectResponse();
                    respType.setStatus(StatusCodeType.FAILURE);
                    respType.setError(ErrorCode.NO_SUCH_IDENTIFIER);
                    return respType;
                } catch (MalformedURLException e) {
                    log.error(e);
                    ObjectResponse respType = new ObjectResponse();
                    respType.setStatus(StatusCodeType.FAILURE);
                    respType.setError(ErrorCode.MALFORMED_REQUEST);
                    return respType;
                } catch (AppsForYourDomainException e) {
                    System.out.println("Google AppsForYourDomainException="
                            + e.getCodeName());
                    log.error(e);
                    // e.printStackTrace();
                    ObjectResponse respType = new ObjectResponse();
                    respType.setStatus(StatusCodeType.FAILURE);
                    respType.setError(ErrorCode.INVALID_CONTAINMENT);
                    return respType;
                } catch (IOException e) {
                    log.error(e);
                    e.printStackTrace();
                } catch (ServiceException e) {
                    log.error(e);
                    System.out.println("Google ServiceException...="
                            + e.getCodeName());
                    ObjectResponse respType = new ObjectResponse();
                    respType.setStatus(StatusCodeType.FAILURE);
                    respType.setError(ErrorCode.CUSTOM_ERROR);
                    return respType;
                }

            }
        }
        ObjectResponse respType = new ObjectResponse();
        respType.setStatus(StatusCodeType.SUCCESS);
        return respType;

    }

    private ObjectResponse renameIdentity(String newIdentity,
            String origIdentity, ManagedSysDto managedSys,
            ManagedSystemObjectMatch matchObj) {
        UserService userService = new UserService(
                "gdata-sample-AppsForYourDomain-UserService");

        try {
            userService.setUserCredentials(managedSys.getUserId(),
                    managedSys.getDecryptPassword());
            String domainUrlBase = APPS_FEEDS_URL_BASE + matchObj.getBaseDn()
                    + "/user/2.0";
            URL updateUrl = new URL(domainUrlBase + "/" + origIdentity);

            UserEntry entry = new UserEntry();
            Login login = new Login();
            login.setUserName(newIdentity);
            entry.addExtension(login);

            userService.update(updateUrl, entry);

        } catch (AuthenticationException e) {
            log.error(e);
            e.printStackTrace();
            ObjectResponse respType = new ObjectResponse();
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.NO_SUCH_IDENTIFIER);
            return respType;
        } catch (MalformedURLException e) {
            log.error(e);
            e.printStackTrace();
            ObjectResponse respType = new ObjectResponse();
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.MALFORMED_REQUEST);
            return respType;
        } catch (AppsForYourDomainException e) {
            log.error(e);
            e.printStackTrace();
            ObjectResponse respType = new ObjectResponse();
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.INVALID_CONTAINMENT);
            return respType;
        } catch (IOException e) {
            log.error(e);
            e.printStackTrace();
        } catch (ServiceException e) {
            log.error(e);
            e.printStackTrace();
            ObjectResponse respType = new ObjectResponse();
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.CUSTOM_ERROR);
            return respType;
        }
        ObjectResponse respType = new ObjectResponse();
        respType.setStatus(StatusCodeType.SUCCESS);
        return respType;
    }

    private ExtensibleAttribute isRename(ExtensibleObject obj) {
        log.debug("Object:" + obj.getName() + " - operation="
                + obj.getOperation());

        List<ExtensibleAttribute> attrList = obj.getAttributes();
        List<ModificationItem> modItemList = new ArrayList<ModificationItem>();
        for (ExtensibleAttribute att : attrList) {
            if (att.getOperation() != 0 && att.getName() != null) {
                if (att.getName().equalsIgnoreCase("ORIG_IDENTITY")) {
                    return att;
                }
            }
        }
        return null;
    }

//    @Override
    public SearchResponse search(@WebParam(name = "searchRequest", targetNamespace = "") SearchRequest searchRequest) {
        throw new UnsupportedOperationException("Not supportable.");
    }

    public ObjectResponse delete(CrudRequest reqType) {
        init();

        String userName = null;

        String requestID = reqType.getRequestID();

        userName = reqType.getUserIdentity();
        /* targetID - */
        String targetID = reqType.getTargetID();

        /*
         * A) Use the targetID to look up the connection information under
         * managed systems
         */
        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);
        ManagedSystemObjectMatch matchObj = null;
        List<ManagedSystemObjectMatchEntity> matchObjList = managedSysObjectMatchDao
                .findBySystemId(targetID, "USER");
        if (matchObjList != null && matchObjList.size() > 0) {
            matchObj = managedSystemObjectMatchDozerConverter.convertToDTO(matchObjList.get(0),false);
        }

        UserService userService = new UserService(
                "gdata-sample-AppsForYourDomain-UserService");
        try {
            userService.setUserCredentials(managedSys.getUserId(),
                    managedSys.getDecryptPassword());
            String domainUrlBase = APPS_FEEDS_URL_BASE + matchObj.getBaseDn()
                    + "/user/2.0";
            URL deleteUrl = new URL(domainUrlBase + "/" + userName);
            userService.delete(deleteUrl);

        } catch (AuthenticationException e) {
            log.error(e);
            ObjectResponse respType = new ObjectResponse();
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.NO_SUCH_IDENTIFIER);
            return respType;
        } catch (MalformedURLException e) {
            log.error(e);
            ObjectResponse respType = new ObjectResponse();
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.MALFORMED_REQUEST);
            return respType;
        } catch (AppsForYourDomainException e) {
            log.error(e);
            ObjectResponse respType = new ObjectResponse();
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.INVALID_CONTAINMENT);
            return respType;
        } catch (IOException e) {
            log.error(e);
        } catch (ServiceException e) {
            log.error(e);
            ObjectResponse respType = new ObjectResponse();
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.CUSTOM_ERROR);
            return respType;
        }

        ObjectResponse respType = new ObjectResponse();
        respType.setStatus(StatusCodeType.SUCCESS);
        return respType;

    }

    public SearchResponse lookup(LookupRequest reqType) {
        log.debug("Google connector:lookup. Will return a failure");
        SearchResponse resp = new SearchResponse();
        resp.setStatus(StatusCodeType.FAILURE);
        return resp;
    }

    /*
* (non-Javadoc)
*
* @see org.openiam.spml2.interf.SpmlCore#lookupAttributeNames(org.openiam.spml2.msg.
* LookupAttributeRequestType)
*/
    public LookupAttributeResponse lookupAttributeNames(LookupRequest reqType){
        LookupAttributeResponse respType = new LookupAttributeResponse();
        respType.setStatus(StatusCodeType.FAILURE);
        respType.setError(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION);

        return respType;
    }

    public ResponseType setPassword(PasswordRequest request) {
        String userName = null;

        init();

        String requestID = request.getRequestID();

        userName = request.getUserIdentity();

        /* targetID - */
        String targetID = request.getTargetID();

        /*
         * A) Use the targetID to look up the connection information under
         * managed systems
         */
        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);
        ManagedSystemObjectMatch matchObj = null;
        List<ManagedSystemObjectMatchEntity> matchObjList = managedSysObjectMatchDao
                .findBySystemId(targetID, "USER");
        if (matchObjList != null && matchObjList.size() > 0) {
            matchObj = managedSystemObjectMatchDozerConverter.convertToDTO(matchObjList.get(0),false);
        }

        UserService userService = new UserService(
                "gdata-sample-AppsForYourDomain-UserService");

        try {
            userService.setUserCredentials(managedSys.getUserId(),
                    managedSys.getDecryptPassword());
            String domainUrlBase = APPS_FEEDS_URL_BASE + matchObj.getBaseDn()
                    + "/user/2.0";
            URL updateUrl = new URL(domainUrlBase + "/" + userName);

            UserEntry entry = new UserEntry();
            Login login = new Login();
            login.setPassword(request.getPassword());
            entry.addExtension(login);

            userService.update(updateUrl, entry);

        } catch (AuthenticationException e) {
            log.error(e);
            e.printStackTrace();
            ResponseType respType = new ResponseType();
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.NO_SUCH_IDENTIFIER);
            return respType;
        } catch (MalformedURLException e) {
            log.error(e);
            e.printStackTrace();
            ResponseType respType = new ResponseType();
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.MALFORMED_REQUEST);
            return respType;
        } catch (AppsForYourDomainException e) {
            log.error(e);
            e.printStackTrace();
            ResponseType respType = new ResponseType();
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.INVALID_CONTAINMENT);
            return respType;
        } catch (IOException e) {
            log.error(e);
            e.printStackTrace();
        } catch (ServiceException e) {
            log.error(e);
            e.printStackTrace();
            ResponseType respType = new ResponseType();
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.CUSTOM_ERROR);
            return respType;
        }

        ResponseType respType = new ResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);
        return respType;

    }

    public ResponseType expirePassword(PasswordRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

    public ResponseType resetPassword(
            PasswordRequest request) {
        // TODO Auto-generated method stub
        init();

        return null;
    }

    public ResponseType validatePassword(
            PasswordRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

    public ManagedSystemWebService getManagedSysService() {
        return managedSysService;
    }

    public void setManagedSysService(ManagedSystemWebService managedSysService) {
        this.managedSysService = managedSysService;
    }

    public ManagedSystemObjectMatchDAO getManagedSysObjectMatchDao() {
        return managedSysObjectMatchDao;
    }

    public void setManagedSysObjectMatchDao(
            ManagedSystemObjectMatchDAO managedSysObjectMatchDao) {
        this.managedSysObjectMatchDao = managedSysObjectMatchDao;
    }

    public ResourceDataService getResourceDataService() {
        return resourceDataService;
    }

    public void setResourceDataService(ResourceDataService resourceDataService) {
        this.resourceDataService = resourceDataService;
    }

    public IdmAuditLogDataService getAuditDataService() {
        return auditDataService;
    }

    public void setAuditDataService(IdmAuditLogDataService auditDataService) {
        this.auditDataService = auditDataService;
    }

    public LoginDataService getLoginManager() {
        return loginManager;
    }

    public void setLoginManager(LoginDataService loginManager) {
        this.loginManager = loginManager;
    }

    public PolicyDataService getPolicyDataService() {
        return policyDataService;
    }

    public void setPolicyDataService(PolicyDataService policyDataService) {
        this.policyDataService = policyDataService;
    }

    public UserDataService getUserManager() {
        return userManager;
    }

    public void setUserManager(UserDataService userManager) {
        this.userManager = userManager;
    }

    public ResponseType suspend(SuspendRequest request) {
        String userName = null;
        String firstName = null;
        String lastName = null;
        boolean change = false;
        ResponseType respType = new ResponseType();

        init();

        String requestID = request.getRequestID();

        userName = request.getUserIdentity();

        /* targetID - */
        String targetID = request.getTargetID();

        /*
         * A) Use the targetID to look up the connection information under
         * managed systems
         */
        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);
        ManagedSystemObjectMatch matchObj = null;
        List<ManagedSystemObjectMatchEntity> matchObjList = managedSysObjectMatchDao
                .findBySystemId(targetID, "USER");
        if (matchObjList != null && matchObjList.size() > 0) {
            matchObj = managedSystemObjectMatchDozerConverter.convertToDTO(matchObjList.get(0),false);
        }

        UserService userService = new UserService(
                "gdata-sample-AppsForYourDomain-UserService");

        try {
            userService.setUserCredentials(managedSys.getUserId(),
                    managedSys.getDecryptPassword());
            String domainUrlBase = APPS_FEEDS_URL_BASE + matchObj.getBaseDn()
                    + "/user/2.0";
            URL updateUrl = new URL(domainUrlBase + "/" + userName);
            URL retrieveUrl = new URL(domainUrlBase + "/" + userName);

            UserEntry userEntry = userService.getEntry(retrieveUrl,
                    UserEntry.class);
            userEntry.getLogin().setSuspended(true);

            userService.update(updateUrl, userEntry);

        } catch (AuthenticationException e) {
            log.error(e);

            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.NO_SUCH_IDENTIFIER);
            return respType;
        } catch (MalformedURLException e) {
            log.error(e);
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.MALFORMED_REQUEST);
            return respType;
        } catch (AppsForYourDomainException e) {
            log.error(e);
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.INVALID_CONTAINMENT);
            return respType;
        } catch (IOException e) {
            log.error(e);

        } catch (ServiceException e) {
            log.error(e);
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.CUSTOM_ERROR);
            return respType;
        }

        respType.setStatus(StatusCodeType.SUCCESS);
        return respType;

    }

    public ResponseType resume(SuspendResumeRequest request) {
        String userName = null;
        String firstName = null;
        String lastName = null;
        boolean change = false;
        ResponseType respType = new ResponseType();

        init();

        String requestID = request.getRequestID();

        userName = request.getUserIdentity();

        /* targetID - */
        String targetID = request.getTargetID();

        /*
         * A) Use the targetID to look up the connection information under
         * managed systems
         */
        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);
        ManagedSystemObjectMatch matchObj = null;
        List<ManagedSystemObjectMatchEntity> matchObjList = managedSysObjectMatchDao
                .findBySystemId(targetID, "USER");
        if (matchObjList != null && matchObjList.size() > 0) {
            matchObj = managedSystemObjectMatchDozerConverter.convertToDTO(matchObjList.get(0),false);
        }

        UserService userService = new UserService(
                "gdata-sample-AppsForYourDomain-UserService");

        try {
            userService.setUserCredentials(managedSys.getUserId(),
                    managedSys.getDecryptPassword());
            String domainUrlBase = APPS_FEEDS_URL_BASE + matchObj.getBaseDn()
                    + "/user/2.0";
            URL updateUrl = new URL(domainUrlBase + "/" + userName);
            URL retrieveUrl = new URL(domainUrlBase + "/" + userName);

            UserEntry userEntry = userService.getEntry(retrieveUrl,
                    UserEntry.class);
            userEntry.getLogin().setSuspended(false);

            userService.update(updateUrl, userEntry);

        } catch (AuthenticationException e) {
            log.error(e);

            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.NO_SUCH_IDENTIFIER);
            return respType;
        } catch (MalformedURLException e) {
            log.error(e);
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.MALFORMED_REQUEST);
            return respType;
        } catch (AppsForYourDomainException e) {
            log.error(e);
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.INVALID_CONTAINMENT);
            return respType;
        } catch (IOException e) {
            log.error(e);

        } catch (ServiceException e) {
            log.error(e);
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.CUSTOM_ERROR);
            return respType;
        }

        respType.setStatus(StatusCodeType.SUCCESS);
        return respType;
    }

    public ResponseType reconcileResource(
            @WebParam(name = "config", targetNamespace = "") ReconciliationConfig config) {
        ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.FAILURE);
        response.setError(ErrorCode.UNSUPPORTED_OPERATION);
        return response;
    }

    public ResponseType testConnection(ManagedSysDto managedSys) {
        ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        ManagedSystemObjectMatch matchObj = null;
        List<ManagedSystemObjectMatchEntity> matchObjList = managedSysObjectMatchDao
                .findBySystemId(managedSys.getManagedSysId(), "USER");
        if (matchObjList != null && matchObjList.size() > 0) {
            matchObj = managedSystemObjectMatchDozerConverter.convertToDTO(matchObjList.get(0),false);
        }

        UserService userService = new UserService(
                "gdata-sample-AppsForYourDomain-UserService");

        try {
            userService.setUserCredentials(managedSys.getUserId(),
                    managedSys.getDecryptPassword());
            String domainUrlBase = APPS_FEEDS_URL_BASE + matchObj.getBaseDn()
                    + "/user/2.0";

            URL retrieveUrl = new URL(domainUrlBase + "/"
                    + managedSys.getUserId());

            UserEntry userEntry = userService.getEntry(retrieveUrl,
                    UserEntry.class);

        } catch (AuthenticationException e) {
            log.debug("Authentication Exception");
            log.error(e);

            response.setStatus(StatusCodeType.FAILURE);
            response.setError(ErrorCode.NO_SUCH_IDENTIFIER);
            response.addErrorMessage(e.toString());
        } catch (MalformedURLException e) {
            log.debug("MalformedURL Exception");
            log.error(e);
            response.setStatus(StatusCodeType.FAILURE);
            response.setError(ErrorCode.MALFORMED_REQUEST);
            response.addErrorMessage(e.toString());
        } catch (AppsForYourDomainException e) {
            log.debug("AppsForYourDomainException");
            log.error(e);

        } catch (IOException e) {
            log.error(e);

        } catch (ServiceException e) {
            log.debug("ServiceException");
            log.error(e);
            response.setStatus(StatusCodeType.FAILURE);
            response.setError(ErrorCode.CUSTOM_ERROR);
            response.addErrorMessage(e.toString());

        }

        return response;
    }
}
