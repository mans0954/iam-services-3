package org.openiam.base.response;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;

import java.util.List;

/**
 * Created by alexander on 06/09/16.
 */
public class ManagedSysListResponse extends Response {
    private java.util.List<ManagedSysDto> managedSysList;

    public List<ManagedSysDto> getManagedSysList() {
        return managedSysList;
    }

    public void setManagedSysList(List<ManagedSysDto> managedSysList) {
        this.managedSysList = managedSysList;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ManagedSysListResponse{");
        sb.append(super.toString());
        sb.append("managedSysList=").append(managedSysList);
        sb.append('}');
        return sb.toString();
    }
}
