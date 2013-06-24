package org.openiam.spml2.spi.common;

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

public abstract class AbstractCommand<Request extends RequestType, Response extends ResponseType> implements ConnectorCommand<Request, Response>, ApplicationContextAware {

    @Autowired
    protected ManagedSystemService managedSysService;
    @Autowired
    protected ManagedSystemObjectMatchDAO managedSysObjectMatchDao;

    protected ApplicationContext applicationContext;

    @Value("${openiam.default_managed_sys}")
    protected String defaultManagedSysId;



    public void setApplicationContext(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }
}
