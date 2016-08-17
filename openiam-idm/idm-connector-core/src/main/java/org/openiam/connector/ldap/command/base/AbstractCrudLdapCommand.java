package org.openiam.connector.ldap.command.base;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.CrudRequest;
import org.openiam.base.response.ObjectResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleObject;

import javax.naming.ldap.LdapContext;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 8/5/13
 * Time: 11:43 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractCrudLdapCommand<ExtObject extends ExtensibleObject> extends AbstractLdapCommand<CrudRequest<ExtObject>, ObjectResponse> {
    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> crudRequest) throws ConnectorDataException {
        ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        ConnectorConfiguration config =  getConfiguration(crudRequest.getTargetID(), ConnectorConfiguration.class);
        LdapContext ldapctx = this.connect(config.getManagedSys());

        try {
            performObjectOperation(config.getManagedSys(), crudRequest, ldapctx);
        } finally {
            /* close the connection to the directory */
            this.closeContext(ldapctx);
        }
        return response;
    }

    protected abstract void performObjectOperation(ManagedSysEntity managedSys, CrudRequest<ExtObject> crudRequest, LdapContext ldapctx) throws ConnectorDataException;
}
