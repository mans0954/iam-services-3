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
import org.openiam.provision.request.SuspendResumeRequest;
import org.openiam.base.response.ResponseType;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("resumeAppTableCommand")
public class ResumeUserAppTableCommand extends AbstractAppTableCommand<SuspendResumeRequest, ResponseType> {
    @Autowired
    private LoginDataService loginManager;

    @Override
    public ResponseType execute(SuspendResumeRequest resumeRequest) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        AppTableConfiguration configuration = this.getConfiguration(resumeRequest.getTargetID());

        if (StringUtils.isBlank(configuration.getUserStatus()) ||
                StringUtils.isBlank(configuration.getActiveUserStatus())
                || StringUtils.isBlank(configuration.getInactiveUserStatus())) {
            String message = "Status synchronization is turned off! Need to add attributes: 'INCLUDE_IN_STATUS_SYNC' = 'Y' " +
                    "USER_STATUS_FIELD, USER_STATUS_ACTIVE,USER_STATUS_INACTIVE ";
            log.warn(message);
            return response;
        }

        Connection con = this.getConnection(configuration.getManagedSys());
        /* targetID - */
        PreparedStatement statement = null;
        try {
            statement = createChangeUserControlParamsStatement(con, configuration,
                    this.getTableName(configuration, this.getObjectType()), resumeRequest.getObjectIdentity(), configuration.getActiveUserStatus(), false);
            statement.executeUpdate();
            return response;
        } catch (SQLException se) {
            log.error(se.getMessage(), se);
            throw new ConnectorDataException(ErrorCode.SQL_ERROR, se.getMessage());
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
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
