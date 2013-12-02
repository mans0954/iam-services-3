package org.openiam.connector.scim.command.user;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.scim.command.base.AbstractScimCommand;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/12/13
 * Time: 1:33 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("setPasswordScimCommand")
public class SetPasswordScimCommand extends AbstractScimCommand<PasswordRequest, ResponseType> {
    private static final String CHANGE_PASSWORD_SQL = "ALTER USER \"%s\" IDENTIFIED BY \"%s\"";

    @Override
    public ResponseType execute(PasswordRequest passwordRequest) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = passwordRequest.getObjectIdentity();
        ConnectorConfiguration config =  getConfiguration(passwordRequest.getTargetID(), ConnectorConfiguration.class);

        Connection con = this.getConnection(config.getManagedSys());
        try {
            final String sql = String.format(CHANGE_PASSWORD_SQL, principalName, passwordRequest.getPassword());
            if(log.isDebugEnabled()) {
                log.debug(String.format("SQL=%s", sql));
            }
            con.createStatement().execute(sql);
            return response;
        } catch (SQLException se) {
           log.error(se.getMessage(),se);
           throw new ConnectorDataException(ErrorCode.SQL_ERROR, se.getMessage());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        } finally {
           this.closeConnection(con);
        }
    }
}
