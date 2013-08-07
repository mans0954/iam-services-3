package org.openiam.connector.ldap.command.base;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleObject;

import javax.naming.ldap.LdapContext;

public abstract class AbstractLookupLdapCommand<ExtObject extends ExtensibleObject> extends AbstractLdapCommand<LookupRequest<ExtObject>, SearchResponse> {
    @Override
    public SearchResponse execute(LookupRequest<ExtObject> lookupRequest) throws ConnectorDataException {
        log.debug("LOOKUP operation called.");
        boolean found = false;
        SearchResponse respType = new SearchResponse();
        respType.setStatus(StatusCodeType.SUCCESS);

        if (lookupRequest == null) {
            throw new ConnectorDataException(ErrorCode.MALFORMED_REQUEST);
        }
        ConnectorConfiguration config =  getConfiguration(lookupRequest.getTargetID(), ConnectorConfiguration.class);
        LdapContext ldapctx = this.connect(config.getManagedSys());

        try {
            found = this.lookup(config.getManagedSys(), lookupRequest, respType, ldapctx);
            log.debug("LOOKUP successful");
            if (!found) {
                throw  new ConnectorDataException(ErrorCode.NO_RESULTS_RETURNED);
            }

        } finally {
            /* close the connection to the directory */
            this.closeContext(ldapctx);
        }

        return respType;
    }


    protected abstract boolean lookup(ManagedSysEntity managedSys, LookupRequest<ExtObject> lookupRequest, SearchResponse respType, LdapContext ldapctx) throws ConnectorDataException;


}
