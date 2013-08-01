package org.openiam.spml2.spi.ldap.command.base;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.util.connect.ConnectionManagerConstant;
import org.openiam.spml2.util.connect.ConnectionMgr;

import javax.naming.ldap.LdapContext;

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
