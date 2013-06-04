package org.openiam.spml2.spi.orcl;

import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.LookupAttributeRequestType;
import org.openiam.spml2.msg.LookupAttributeResponseType;
import org.openiam.spml2.msg.StatusCodeType;
import org.openiam.spml2.spi.common.LookupAttributeNamesCommand;
import org.springframework.stereotype.Service;

@Service("oracleLookupAttributeNamesCommand")
public class OracleLookupAttributeNamesCommand extends AbstractOracleCommand implements LookupAttributeNamesCommand {

    @Override
    public LookupAttributeResponseType lookupAttributeNames(LookupAttributeRequestType reqType) {
        LookupAttributeResponseType respType = new LookupAttributeResponseType();
        respType.setStatus(StatusCodeType.FAILURE);
        respType.setError(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION);

        return respType;
    }
}
