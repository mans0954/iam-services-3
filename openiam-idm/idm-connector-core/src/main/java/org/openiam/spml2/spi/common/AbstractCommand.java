package org.openiam.spml2.spi.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.ManagedSystemObjectMatchDozerConverter;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemObjectMatchDAO;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.spml2.msg.RequestType;
import org.openiam.spml2.msg.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
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
    protected ManagedSystemObjectMatchDozerConverter managedSystemObjectMatchDozerConverter;

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
}
