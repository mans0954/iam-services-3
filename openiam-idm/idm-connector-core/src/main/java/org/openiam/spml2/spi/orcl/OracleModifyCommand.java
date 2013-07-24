package org.openiam.spml2.spi.orcl;

import org.openiam.connector.type.ErrorCode;
import org.openiam.connector.type.StatusCodeType;
import org.openiam.connector.type.UserRequest;
import org.openiam.connector.type.UserResponse;
import org.openiam.spml2.spi.common.ModifyCommand;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/21/12
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class OracleModifyCommand extends AbstractOracleCommand implements ModifyCommand {
    @Override
    public UserResponse modify(UserRequest reqType) {
        UserResponse response = new UserResponse();
        response.setStatus(StatusCodeType.FAILURE);
        response.setError(ErrorCode.UNSUPPORTED_OPERATION);
        return response;
    }
}
