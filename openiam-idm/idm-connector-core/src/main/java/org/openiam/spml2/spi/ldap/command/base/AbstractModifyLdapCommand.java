package org.openiam.spml2.spi.ldap.command.base;

import org.openiam.base.BaseAttribute;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.ldap.dirtype.Directory;
import org.openiam.spml2.spi.ldap.dirtype.DirectorySpecificImplFactory;
import org.openiam.spml2.util.connect.ConnectionFactory;
import org.openiam.spml2.util.connect.ConnectionManagerConstant;
import org.openiam.spml2.util.connect.ConnectionMgr;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractModifyLdapCommand<ProvisionObject extends GenericProvisionObject> extends AbstractLdapCommand<ModifyRequestType<ProvisionObject>, ModifyResponseType>{

    @Override
    public ModifyResponseType execute(ModifyRequestType<ProvisionObject> modifyRequestType) throws ConnectorDataException {
         /* FOR LDAP, need to be able to move object's OU - incase of re-org, person changes roles, etc */
        /* Need to be able add and remove users from groups */

        log.debug("LDAP Modify request called..");
        ConnectionMgr conMgr = null;
        LdapContext ldapctx = null;
        List<ExtensibleObject> extobjectList = null;
        boolean groupMembershipEnabled = true;


        List<BaseAttribute> targetMembershipList = new ArrayList<BaseAttribute>();

        ModifyResponseType respType = new ModifyResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);


        String requestID = modifyRequestType.getRequestID();
        /* PSO - Provisioning Service Object -
           *     -  ID must uniquely specify an object on the target or in the target's namespace
           *     -  Try to make the PSO ID immutable so that there is consistency across changes. */
        PSOIdentifierType psoID = modifyRequestType.getPsoID();
        /* targetID -  */
        String targetID = psoID.getTargetID();
        /* ContainerID - May specify the container in which this object should be created
           *      ie. ou=Development, org=Example */
        PSOIdentifierType containerID = psoID.getContainerID();


        // modificationType contains a collection of objects for each type of operation
        List<ModificationType> modificationList = modifyRequestType.getModification();

        log.debug("ModificationList = " + modificationList);
        log.debug("Modificationlist size= " + modificationList.size());


        /* A) Use the targetID to look up the connection information under managed systems */
        ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
        log.debug("managedSys found for targetID=" + targetID + " " + " Name=" + managedSys.getName());
        try {
            ldapctx = this.connect(managedSys);
            log.debug("Ldapcontext = " + ldapctx);
            if (ldapctx == null)
                throw new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, "Unable to connect to directory.");

            modifyObject(modifyRequestType, managedSys, modificationList, ldapctx);


        } catch (ConnectorDataException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return respType;
    }

    protected abstract void modifyObject(ModifyRequestType<ProvisionObject> modifyRequestType, ManagedSysEntity managedSys, List<ModificationType> modificationList, LdapContext ldapctx)throws ConnectorDataException;
}
