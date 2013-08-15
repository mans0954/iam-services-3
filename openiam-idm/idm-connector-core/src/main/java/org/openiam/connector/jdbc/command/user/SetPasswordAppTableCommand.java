package org.openiam.connector.jdbc.command.user;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.jdbc.command.base.AbstractAppTableCommand;
import org.openiam.connector.jdbc.command.data.AppTableConfiguration;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service("setPasswordAppTableCommand")
public class SetPasswordAppTableCommand extends AbstractAppTableCommand<PasswordRequest, ResponseType> {
    @Override
    public ResponseType execute(PasswordRequest passwordRequest) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        AppTableConfiguration configuration = this.getConfiguration(passwordRequest.getTargetID());
        Connection con = this.getConnection(configuration.getManagedSys());

        PreparedStatement statement = null;
        try {
            statement = createSetPasswordStatement(con, configuration.getResourceId(), configuration.getTableName(), passwordRequest.getObjectIdentity(), passwordRequest.getPassword());
            statement.executeUpdate();
            return response;
        } catch (SQLException se) {
            log.error(se.getMessage(), se);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, se.getMessage());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        } finally {
            this.closeStatement(statement);
            this.closeConnection(con);
        }
    }

}
