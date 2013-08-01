package org.openiam.spml2.spi.ldap.command.base;

import org.openiam.base.BaseAttribute;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.util.connect.ConnectionManagerConstant;
import org.openiam.spml2.util.connect.ConnectionMgr;

import javax.naming.ldap.LdapContext;
import javax.xml.namespace.QName;
import java.util.*;

public abstract class AbstractAddLdapCommand<ProvisionObject extends GenericProvisionObject> extends AbstractLdapCommand<AddRequestType<ProvisionObject>, AddResponseType> {
    @Override
    public AddResponseType execute(AddRequestType<ProvisionObject> addRequestType) throws ConnectorDataException {
        AddResponseType response = new AddResponseType();
        response.setStatus(StatusCodeType.SUCCESS);
        List<BaseAttribute> targetMembershipList = new ArrayList<BaseAttribute>();

        boolean groupMembershipEnabled = true;

        String requestID = addRequestType.getRequestID();
        /* ContainerID - May specify the container in which this object should be created
           *      ie. ou=Development, org=Example */
        PSOIdentifierType containerID = addRequestType.getContainerID();
        /* PSO - Provisioning Service Object -
           *     -  ID must uniquely specify an object on the target or in the target's namespace
           *     -  Try to make the PSO ID immutable so that there is consistency across changes. */
        PSOIdentifierType psoID = addRequestType.getPsoID();
        /* targetID -  */
        String targetID = addRequestType.getTargetID();

        // Data sent with request - Data must be present in the request per the spec
        ExtensibleType data = addRequestType.getData();
        Map<QName, String> otherData = addRequestType.getOtherAttributes();

        /* Indicates what type of data we should return from this operations */
        ReturnDataType returnData = addRequestType.getReturnData();


        /* A) Use the targetID to look up the connection information under managed systems */
        ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);

        LdapContext ldapctx = this.connect(managedSys);

        try {
            addObject(managedSys, psoID, targetID, addRequestType.getData().getAny(),  ldapctx);
        } catch (ConnectorDataException ex) {
            log.error(ex.getMessage(), ex);
            throw  ex;

        } finally {
            /* close the connection to the directory */
            this.closeContext(ldapctx);
        }

        return response;
    }

    protected abstract void addObject(ManagedSysEntity managedSys, PSOIdentifierType psoID, String targetID, List<ExtensibleObject> anyObjectList,  LdapContext ldapctx) throws ConnectorDataException;
}
