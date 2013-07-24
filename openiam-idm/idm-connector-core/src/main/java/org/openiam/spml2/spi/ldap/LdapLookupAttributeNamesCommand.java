package org.openiam.spml2.spi.ldap;

import org.openiam.connector.type.ErrorCode;
import org.openiam.connector.type.LookupAttributeResponse;
import org.openiam.connector.type.LookupRequest;
import org.openiam.connector.type.StatusCodeType;
import org.openiam.spml2.spi.common.LookupAttributeNamesCommand;
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
