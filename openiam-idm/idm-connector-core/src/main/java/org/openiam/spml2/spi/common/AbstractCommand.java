package org.openiam.spml2.spi.common;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.ManagedSysDozerConverter;
import org.openiam.dozer.converter.ManagedSystemObjectMatchDozerConverter;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemObjectMatchDAO;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.RequestType;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.spi.common.data.ConnectorConfiguration;
import org.openiam.spml2.spi.jdbc.command.data.AppTableConfiguration;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;

public abstract class AbstractCommand<Request extends RequestType, Response extends ResponseType> implements ConnectorCommand<Request, Response>, ApplicationContextAware {
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
    private Cryptor cryptor;
    @Autowired
    private KeyManagementService keyManagementService;


    protected ApplicationContext applicationContext;

    @Value("${openiam.default_managed_sys}")
    protected String defaultManagedSysId;



    public void setApplicationContext(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }


    protected ManagedSystemObjectMatch getMatchObject(String targetID, String type){
        ManagedSystemObjectMatch matchObj = null;
        List<ManagedSystemObjectMatchEntity> matchObjList = managedSysObjectMatchDao.findBySystemId(targetID, type);
        if (matchObjList != null && matchObjList.size() > 0) {
            matchObj = managedSystemObjectMatchDozerConverter.convertToDTO(matchObjList.get(0),false);
        }
        return matchObj;
    }

    protected String getDecryptedPassword(String userId, String encPwd) throws ConnectorDataException {
        String result = null;
        if(encPwd!=null){
            try {
                result = cryptor.decrypt(keyManagementService.getUserKey(userId, KeyName.password.name()), encPwd);
            } catch (Exception e) {
                log.error(e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            }
        }
        return null;
    }

    protected <T extends ConnectorConfiguration> T getConfiguration(String targetID, Class<T> clazz) throws ConnectorDataException{
        try {
            T configuration = clazz.newInstance();

            ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
            if(managedSys == null)
                throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, String.format("No Managed System with target id: %s", targetID));
            configuration.setManagedSys(managedSys);

            if (StringUtils.isBlank(managedSys.getResourceId()))
                throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "ResourceID is not defined in the ManagedSys Object");

            final Resource res = resourceDataService.getResource(managedSys.getResourceId());
            if(res == null)
                throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "No resource for managed resource found");

            configuration.setResource(res);

//            final ResourceProp prop = res.getResourceProperty(TABLE_NAME_PROP);
//            if(prop == null)
//                throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "No TABLE_NAME property found");
//            final String tableName = prop.getPropValue();
//            if (StringUtils.isBlank(tableName))
//                throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "TABLE NAME is not defined.");
//
//            configuration.setTableName(tableName);

            return  configuration;
        } catch (InstantiationException e) {
            log.error(e.getMessage(),e);
            throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "Cannot get connector configuration");
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(),e);
            throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "Cannot get connector configuration");
        }

    }
}
