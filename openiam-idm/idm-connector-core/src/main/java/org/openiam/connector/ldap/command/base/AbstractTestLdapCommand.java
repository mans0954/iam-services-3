package org.openiam.connector.ldap.command.base;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.provision.type.ExtensibleObject;

import javax.naming.ldap.LdapContext;

public abstract class AbstractTestLdapCommand <T, ExtObject extends ExtensibleObject> extends AbstractLdapCommand<RequestType<ExtObject>, ResponseType> {
    @Override
    public ResponseType execute(RequestType<ExtObject> requestType) throws ConnectorDataException {
        ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        ConnectorConfiguration config =  getConfiguration(requestType.getTargetID(), ConnectorConfiguration.class);
        LdapContext ldapctx = this.connect(config.getManagedSys());

        return response;
    }

}
