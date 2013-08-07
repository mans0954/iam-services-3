package org.openiam.spml2.spi.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.connector.common.jdbc.JDBCConnectionMgr;

/**
 * Base class for commands that are used by the MySQL Connector
 */
@Deprecated
public abstract class MySQLAbstractCommand {

    protected static final Log log = LogFactory.getLog(MySQLAbstractCommand.class);

    protected ManagedSystemWebService managedSysService;
    protected ResourceDataService resourceDataService;
    protected JDBCConnectionMgr connectionMgr;

    public ManagedSystemWebService getManagedSysService() {
        return managedSysService;
    }

    public void setManagedSysService(ManagedSystemWebService managedSysService) {
        this.managedSysService = managedSysService;
    }

    public ResourceDataService getResourceDataService() {
        return resourceDataService;
    }

    public void setResourceDataService(ResourceDataService resourceDataService) {
        this.resourceDataService = resourceDataService;
    }

    public JDBCConnectionMgr getConnectionMgr() {
        return connectionMgr;
    }

    public void setConnectionMgr(JDBCConnectionMgr connectionMgr) {
        this.connectionMgr = connectionMgr;
    }
}

