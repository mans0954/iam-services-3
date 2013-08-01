package org.openiam.spml2.spi.orcl;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.response.LookupAttributeResponse;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.spml2.spi.common.LookupAttributeNamesCommand;

@Deprecated
public class OracleLookupAttributeNamesCommand extends AbstractOracleCommand implements LookupAttributeNamesCommand {

    @Override
    public LookupAttributeResponse lookupAttributeNames(LookupRequest reqType) {
        LookupAttributeResponse respType = new LookupAttributeResponse();
        respType.setStatus(StatusCodeType.FAILURE);
        respType.setError(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION);

        return respType;
    }
}
