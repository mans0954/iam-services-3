package org.openiam.connector.salesforce.command.base;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.provision.type.ExtensibleObject;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/16/13
 * Time: 7:45 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDeleteSalesForceCommand<ExtObject extends ExtensibleObject> extends AbstractSalesforceCommand<CrudRequest<ExtObject>, ObjectResponse>{
    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> deleteRequestType) throws ConnectorDataException {
        final ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = deleteRequestType.getObjectIdentity();
        final String targetID = deleteRequestType.getTargetID();
        ConnectorConfiguration configuration = this.getConfiguration(targetID, ConnectorConfiguration.class);

        deleteObject(principalName, configuration.getManagedSys());
        return response;
    }

    protected abstract void deleteObject(String principalName, ManagedSysEntity managedSys) throws ConnectorDataException;
}
