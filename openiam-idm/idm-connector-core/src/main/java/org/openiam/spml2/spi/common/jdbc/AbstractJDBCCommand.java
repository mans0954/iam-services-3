package org.openiam.spml2.spi.common.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/21/12
 * Time: 10:27 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractJDBCCommand {

    protected static final Log log = LogFactory.getLog(AbstractJDBCCommand.class);

    protected ManagedSystemWebService managedSysService;
    protected ResourceDataService resourceDataService;
    protected JDBCConnectionMgr connectionMgr;

    protected List<AttributeMap> attributeMaps(final Resource resource) {
        return managedSysService.getResourceAttributeMaps(resource.getResourceId());
    }

    @Required
    public void setManagedSysService(ManagedSystemWebService managedSysService) {
        this.managedSysService = managedSysService;
    }

    @Required
    public void setResourceDataService(ResourceDataService resourceDataService) {
        this.resourceDataService = resourceDataService;
    }

    @Required
    public void setConnectionMgr(JDBCConnectionMgr connectionMgr) {
        this.connectionMgr = connectionMgr;
    }
}
