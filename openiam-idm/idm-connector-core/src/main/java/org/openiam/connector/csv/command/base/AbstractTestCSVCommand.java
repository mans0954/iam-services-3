package org.openiam.connector.csv.command.base;

import org.openiam.connector.data.ConnectorConfiguration;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.type.ExtensibleObject;

import java.util.List;

public abstract class AbstractTestCSVCommand <T, ExtObject extends ExtensibleObject> extends AbstractCSVCommand<RequestType<ExtObject>, ResponseType> {
    @Override
    public ResponseType execute(RequestType<ExtObject> requestType) throws ConnectorDataException {
        ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        ConnectorConfiguration config =  getConfiguration(requestType.getTargetID(), ConnectorConfiguration.class);
        this.getObjectList(config.getManagedSys());
        return response;
    }

    protected abstract List<ReconciliationObject<T>> getObjectList(ManagedSysEntity managedSys) throws ConnectorDataException;
}
