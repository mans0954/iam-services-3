package org.openiam.spml2.spi.orcl;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.spml2.spi.common.ModifyCommand;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/21/12
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class OracleModifyCommand extends AbstractOracleCommand implements ModifyCommand {
    @Override
    public ObjectResponse modify(CrudRequest reqType) {
        ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.FAILURE);
        response.setError(ErrorCode.UNSUPPORTED_OPERATION);
        return response;
    }
}
