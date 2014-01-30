package org.openiam.connector.jdbc.command.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.connector.jdbc.command.base.AbstractAppTableCommand;
import org.openiam.connector.jdbc.command.data.AppTableConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("resumeAppTableCommand")
public class ResumeAppTableCommand extends AbstractAppTableCommand<SuspendResumeRequest, ResponseType> {
    @Autowired
    private LoginDataService loginManager;

    @Override
    public ResponseType execute(SuspendResumeRequest resumeRequest) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        AppTableConfiguration configuration = this.getConfiguration(resumeRequest.getTargetID());

        final String principalName = resumeRequest.getObjectIdentity();
        Connection con = this.getConnection(configuration.getManagedSys());
        /* targetID - */
        final String targetID = resumeRequest.getTargetID();

        List<LoginEntity> loginList = loginManager.getLoginDetailsByManagedSys(principalName, targetID);
        if (CollectionUtils.isEmpty(loginList))
            throw new ConnectorDataException(ErrorCode.INVALID_IDENTIFIER, "Principal not found");

        PreparedStatement statement = null;
        try {
            final LoginEntity login = loginList.get(0);
            final String encPassword = login.getPassword();
            final String decPassword = loginManager.decryptPassword(login.getUserId(), encPassword);
            statement = createSetPasswordStatement(con, configuration.getResourceId(), configuration.getTableName(),
                    principalName, decPassword);

            statement.executeUpdate();

            return response;
        } catch (SQLException se) {
            log.error(se.getMessage(), se);
            throw new ConnectorDataException(ErrorCode.SQL_ERROR, se.getMessage());
        } catch (EncryptionException ee) {
            log.error(ee.getMessage(), ee);
            throw new ConnectorDataException(ErrorCode.OTHER_ERROR, ee.getMessage());
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        } finally {
            this.closeStatement(statement);
            this.closeConnection(con);
        }
    }
}
