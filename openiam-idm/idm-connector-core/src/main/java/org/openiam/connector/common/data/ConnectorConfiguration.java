package org.openiam.connector.common.data;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.dto.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/15/13
 * Time: 11:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConnectorConfiguration {
    private ManagedSysEntity managedSys;
    private Resource resource;

    public String getResourceId() {
        return resource.getResourceId();
    }

    public Resource getResource() {
        return resource;
    }
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public ManagedSysEntity getManagedSys() {
        return managedSys;
    }

    public void setManagedSys(ManagedSysEntity managedSys) {
        this.managedSys = managedSys;
    }
}
