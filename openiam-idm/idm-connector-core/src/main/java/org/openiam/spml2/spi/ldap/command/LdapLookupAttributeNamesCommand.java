package org.openiam.spml2.spi.ldap.command;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.response.LookupAttributeResponse;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.spml2.spi.common.LookupAttributeNamesCommand;
import org.openiam.spml2.spi.ldap.command.base.LdapAbstractCommand;
import org.springframework.stereotype.Service;

@Service("ldapLookupAttributeNamesCommand")
public class LdapLookupAttributeNamesCommand extends LdapAbstractCommand implements LookupAttributeNamesCommand {
    @Override
    public LookupAttributeResponse lookupAttributeNames(LookupRequest reqType) {
        LookupAttributeResponse respType = new LookupAttributeResponse();
        respType.setStatus(StatusCodeType.FAILURE);
        respType.setError(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION);

        return respType;
    }
}
