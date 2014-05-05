package org.openiam.connector.jdbc.command.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.openiam.connector.jdbc.command.base.AbstractAppTableCommand;
import org.openiam.connector.jdbc.command.data.AppTableConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.pswd.service.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("suspendAppTableCommand")
public class SuspendUserAppTableCommand extends AbstractAppTableCommand<SuspendResumeRequest, ResponseType> {
    @Autowired
    private PasswordGenerator passwordGenerator;

    @Override
    public ResponseType execute(SuspendResumeRequest suspendRequest) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        AppTableConfiguration configuration = this.getConfiguration(suspendRequest.getTargetID());
        Connection con = this.getConnection(configuration.getManagedSys());

        final String password = passwordGenerator.generatePassword(10);

        PreparedStatement statement = null;
        try {
            statement = createSetPasswordStatement(con, configuration.getResourceId(),
                    this.getTableName(configuration, this.getObjectType()), suspendRequest.getObjectIdentity(),
                    password);
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
