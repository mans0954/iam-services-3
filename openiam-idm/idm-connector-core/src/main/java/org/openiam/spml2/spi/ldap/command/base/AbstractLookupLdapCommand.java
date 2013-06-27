package org.openiam.spml2.spi.ldap.command.base;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.util.connect.ConnectionFactory;
import org.openiam.spml2.util.connect.ConnectionManagerConstant;
import org.openiam.spml2.util.connect.ConnectionMgr;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import java.util.List;

public abstract class AbstractLookupLdapCommand<ProvisionObject extends GenericProvisionObject> extends AbstractLdapCommand<LookupRequestType<ProvisionObject>, LookupResponseType>{
    @Override
    public LookupResponseType execute(LookupRequestType<ProvisionObject> lookupRequestType) throws ConnectorDataException {
        log.debug("LOOKUP operation called.");
        boolean found = false;
        ConnectionMgr conMgr = null;
        LookupResponseType respType = new LookupResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);

        if (lookupRequestType == null) {
            throw new ConnectorDataException(ErrorCode.MALFORMED_REQUEST);
        }

        PSOIdentifierType psoId = lookupRequestType.getPsoID();


        ManagedSysEntity managedSys = managedSysService.getManagedSysById(psoId.getTargetID());
        LdapContext ldapctx = null;
        try {
            ldapctx = this.connect(managedSys);

            PSOType psoType = new PSOType();
            psoType.setPsoID(psoId);
            respType.setPso(psoType);
            found = this.lookup(psoId, managedSys, respType, ldapctx);

            log.debug("LOOKUP successful");
            if (!found) {
                throw  new ConnectorDataException(ErrorCode.NO_RESULTS_RETURNED);
            }

        } catch (ConnectorDataException e) {
            log.error(e.getMessage(), e);
            throw e;
        }  finally {
            /* close the connection to the directory */
            this.closeContext(ldapctx);
        }

        return respType;
    }


    protected abstract boolean lookup(PSOIdentifierType psoId, ManagedSysEntity managedSys,  LookupResponseType respType, LdapContext ldapctx) throws ConnectorDataException;


}
