package org.openiam.connector.jdbc.command.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.jdbc.command.base.AbstractAppTableCommand;
import org.openiam.connector.jdbc.command.data.AppTableConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.PasswordRequest;
import org.openiam.base.response.ResponseType;
import org.springframework.stereotype.Service;

@Service("setPasswordAppTableCommand")
public class SetUserPasswordAppTableCommand extends AbstractAppTableCommand<PasswordRequest, ResponseType> {
    @Override
    public ResponseType execute(PasswordRequest passwordRequest) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        AppTableConfiguration configuration = this.getConfiguration(passwordRequest.getTargetID());
        if (StringUtils.isBlank(configuration.getPrincipalPassword())) {
            String message = "Password synchronization is furned off! Need to add attributes: 'INCLUDE_IN_PASSWORD_SYNC' = 'Y' and 'PRINCIPAL_PASSWORD' = NAME OF PASSWORD COLUMN";
            log.warn(message);
            return response;
        }
        Connection con = this.getConnection(configuration.getManagedSys());

        PreparedStatement statement = null;
        try {
            statement = createChangeUserControlParamsStatement(con, configuration,
                    this.getTableName(configuration, this.getObjectType()), passwordRequest.getObjectIdentity(),
                    passwordRequest.getPassword(), true);
            statement.executeUpdate();
            return response;
        } catch (SQLException se) {
            log.error(se.getMessage(), se);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, se.getMessage());
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        } finally {
            this.closeStatement(statement);
            this.closeConnection(con);
        }
    }

    @Override
    protected String getObjectType() {
        return "USER";
    }

}
