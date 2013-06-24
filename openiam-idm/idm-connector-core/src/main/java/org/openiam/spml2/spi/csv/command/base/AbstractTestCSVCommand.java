package org.openiam.spml2.spi.csv.command.base;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.msg.TestRequestType;

import java.util.List;

public abstract class AbstractTestCSVCommand <T, ProvisionObject extends GenericProvisionObject> extends AbstractCSVCommand<TestRequestType<ProvisionObject>, ResponseType> {
    @Override
    public ResponseType execute(TestRequestType<ProvisionObject> requestType) throws ConnectorDataException {
        ResponseType response = new ResponseType();
        String targetID = requestType.getPsoID().getTargetID();
        ManagedSysEntity managedSys = managedSysService
                .getManagedSysById(targetID);

        this.getObjectList(managedSys);
        return response;
    }

    protected abstract List<ReconciliationObject<T>> getObjectList(ManagedSysEntity managedSys) throws ConnectorDataException;
}
