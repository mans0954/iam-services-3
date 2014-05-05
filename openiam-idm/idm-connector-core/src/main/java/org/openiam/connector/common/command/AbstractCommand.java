package org.openiam.connector.common.command;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.dozer.converter.ManagedSysDozerConverter;
import org.openiam.dozer.converter.ManagedSystemObjectMatchDozerConverter;
import org.openiam.exception.ConfigurationException;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemObjectMatchDAO;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractCommand<Request extends RequestType, Response extends ResponseType> implements
        ConnectorCommand<Request, Response>, ApplicationContextAware {
    protected final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    protected ManagedSystemService managedSysService;
    @Autowired
    protected ManagedSystemObjectMatchDAO managedSysObjectMatchDao;
    @Autowired
    protected ResourceDataService resourceDataService;
    @Autowired
    protected ManagedSystemObjectMatchDozerConverter managedSystemObjectMatchDozerConverter;

    @Autowired
    protected ManagedSysDozerConverter managedSysDozerConverter;

    @Autowired
    @Qualifier("cryptor")
    protected Cryptor cryptor;
    @Autowired
    protected KeyManagementService keyManagementService;

    protected ApplicationContext applicationContext;

    @Value("${openiam.default_managed_sys}")
    protected String defaultManagedSysId;
    @Value("${org.openiam.idm.system.user.id}")
    protected String systemUserId;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected ManagedSystemObjectMatch getMatchObject(String targetID, String type) throws ConfigurationException {
        ManagedSystemObjectMatch matchObj = null;
        List<ManagedSystemObjectMatchEntity> matchObjList = managedSysObjectMatchDao.findBySystemId(targetID, type);
        if (matchObjList != null && matchObjList.size() > 0) {
            matchObj = managedSystemObjectMatchDozerConverter.convertToDTO(matchObjList.get(0), false);
        }
        log.debug("matchObj = " + matchObj);

        if (matchObj == null) {
            throw new ConfigurationException("Configuration is missing Match Object information");
        }
        return matchObj;
    }

    protected String getPassword(String managedSystemId) throws ConnectorDataException {
        ManagedSysEntity mSys = managedSysService.getManagedSysById(managedSystemId);
        return this.getDecryptedPassword(mSys.getPswd());
    }

    protected HashMap<String, String> objectToAttributes(String login, ExtensibleObject obj) {
        HashMap<String, String> attributes = new HashMap<String, String>();
        if (!StringUtils.isEmpty(login)) {
            // Extract attribues into a map. Also save groups

            attributes.put("login", login);
            if (obj == null) {
                log.debug("Object: not provided, just identity, seems it is delete operation");
            } else {
                log.debug("Object:" + obj.getName() + " - operation=" + obj.getOperation());
                // Extract attributes
                for (ExtensibleAttribute att : obj.getAttributes()) {
                    if (att != null) {
                        attributes.put(att.getName().toLowerCase(), att.getValue());
                    }
                }
            }
        } else {
            log.error("Login name for Linux user not specified");
        }
        return attributes;
    }

    protected String getDecryptedPassword(String encPwd) throws ConnectorDataException {
        String result = null;
        if (encPwd != null) {
            try {
                result = cryptor
                        .decrypt(keyManagementService.getUserKey(systemUserId, KeyName.password.name()), encPwd);
            } catch (Exception e) {
                log.error(e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            }
        }
        return result;
    }

    protected <T extends ConnectorConfiguration> T getConfiguration(String targetID, Class<T> clazz)
            throws ConnectorDataException {
        try {
            T configuration = clazz.newInstance();

            ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
            if (managedSys == null)
                throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, String.format(
                        "No Managed System with target id: %s", targetID));
            configuration.setManagedSys(managedSys);

            if (StringUtils.isBlank(managedSys.getResourceId()))
                throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION,
                        "ResourceID is not defined in the ManagedSys Object");

            final Resource res = resourceDataService.getResource(managedSys.getResourceId(), null);
            if (res == null)
                throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION,
                        "No resource for managed resource found");

            configuration.setResource(res);

            // final ResourceProp prop =
            // res.getResourceProperty(TABLE_NAME_PROP);
            // if(prop == null)
            // throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION,
            // "No TABLE_NAME property found");
            // final String tableName = prop.getPropValue();
            // if (StringUtils.isBlank(tableName))
            // throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION,
            // "TABLE NAME is not defined.");
            //
            // configuration.setTableName(tableName);

            return configuration;
        } catch (InstantiationException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "Cannot get connector configuration");
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "Cannot get connector configuration");
        }

    }
}
