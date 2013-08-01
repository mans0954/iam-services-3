package org.openiam.spml2.spi.gapps.command.base;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.provision.type.ExtensibleObject;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;

public abstract class AbstractAddGoogleAppsCommand<ProvisionObject extends GenericProvisionObject> extends AbstractGoogleAppsCommand<AddRequestType<ProvisionObject>, AddResponseType>{
    @Override
    public AddResponseType execute(AddRequestType<ProvisionObject> addRequestType) throws ConnectorDataException {
        String userName = null;
        String password = null;
        String givenName = null;
        String lastName = null;

        init();


        String requestID = addRequestType.getRequestID();
    /*
     * ContainerID - May specify the container in which this object should
     * be created ie. ou=Development, org=Example
     */
        PSOIdentifierType containerID = addRequestType.getContainerID();

    /*
     * PSO - Provisioning Service Object - - ID must uniquely specify an
     * object on the target or in the target's namespace - Try to make the
     * PSO ID immutable so that there is consistency across changes.
     */
        PSOIdentifierType psoID = addRequestType.getPsoID();
        userName = psoID.getID();

    /* targetID - */
        String targetID = addRequestType.getTargetID();

        // Data sent with request - Data must be present in the request per the
        // spec
        ExtensibleType data = addRequestType.getData();
        Map<QName, String> otherData = addRequestType.getOtherAttributes();

    /* Indicates what type of data we should return from this operations */
        ReturnDataType returnData = addRequestType.getReturnData();

    /*
     * A) Use the targetID to look up the connection information under
     * managed systems
     */
        ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);


        AddResponseType response = new AddResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        return response;
     }

    protected abstract void addObject(String targetID, ManagedSysEntity managedSys,  List<ExtensibleObject> requestAttributeList) throws ConnectorDataException;
}
