package org.openiam.spml2.spi.ldap.command.base;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.ldap.dirtype.Directory;
import org.openiam.spml2.spi.ldap.dirtype.DirectorySpecificImplFactory;
import org.openiam.spml2.util.connect.ConnectionFactory;
import org.openiam.spml2.util.connect.ConnectionManagerConstant;
import org.openiam.spml2.util.connect.ConnectionMgr;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import java.util.List;
import java.util.Set;

public abstract class AbstractDeleteLdapCommand<ProvisionObject extends GenericProvisionObject> extends AbstractLdapCommand<DeleteRequestType<ProvisionObject>, ResponseType>{
    @Override
    public ResponseType execute(DeleteRequestType<ProvisionObject> deleteRequestType) throws ConnectorDataException {
        log.debug("delete request called..");

        ResponseType respType = new ResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);

        //String uid = null;
        String ou = null;

        String requestID = deleteRequestType.getRequestID();

        /* PSO - Provisioning Service Object -
           *     -  ID must uniquely specify an object on the target or in the target's namespace
           *     -  Try to make the PSO ID immutable so that there is consistency across changes. */
        PSOIdentifierType psoID = deleteRequestType.getPsoID();
        /* targetID -  */
        String targetID = psoID.getTargetID();
        /* ContainerID - May specify the container in which this object should be created
           *      ie. ou=Development, org=Example */
        PSOIdentifierType containerID = psoID.getContainerID();


        /* A) Use the targetID to look up the connection information under managed systems */
        ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
        LdapContext ldapctx = null;
        try {
            ldapctx = this.connect(managedSys);
            deleteObject(deleteRequestType, managedSys,  ldapctx);

        } catch (ConnectorDataException ex) {
            log.error(ex.getMessage(), ex);
            throw  ex;

        } finally {
            /* close the connection to the directory */
            this.closeContext(ldapctx);
        }
        return respType;
    }

    protected abstract void deleteObject(DeleteRequestType<ProvisionObject> deleteRequestType, ManagedSysEntity managedSys, LdapContext ldapctx) throws ConnectorDataException;
}
