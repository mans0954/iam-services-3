package org.openiam.spml2.spi.jdbc.command.user;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.spml2.msg.suspend.ResumeRequestType;
import org.openiam.spml2.spi.jdbc.command.base.AbstractAppTableCommand;
import org.openiam.spml2.spi.jdbc.command.data.AppTableConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Service("resumeAppTableCommand")
public class ResumeAppTableCommand  extends AbstractAppTableCommand<ResumeRequestType, ResponseType> {
    @Autowired
    private LoginDataService loginManager;

    @Override
    public ResponseType execute(ResumeRequestType resumeRequestType) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = resumeRequestType.getPsoID().getID();

        final PSOIdentifierType psoID = resumeRequestType.getPsoID();
        /* targetID -  */
        final String targetID = psoID.getTargetID();

        List<LoginEntity> loginList = loginManager.getLoginDetailsByManagedSys(principalName, targetID);
        if (CollectionUtils.isEmpty(loginList))
            throw new ConnectorDataException(ErrorCode.INVALID_IDENTIFIER, "Principal not found");

        AppTableConfiguration configuration = this.getConfiguration(targetID);
        Connection con = this.getConnection(configuration.getManagedSys());
        PreparedStatement statement = null;
        try {
            final LoginEntity login = loginList.get(0);
            final String encPassword = login.getPassword();
            final String decPassword = loginManager.decryptPassword(login.getUserId(),encPassword);
            statement = createSetPasswordStatement(con, configuration.getResourceId(), configuration.getTableName(), principalName, decPassword);

            statement.executeUpdate();

            return response;
        } catch (SQLException se) {
            log.error(se.getMessage(), se);
            throw new ConnectorDataException(ErrorCode.SQL_ERROR, se.getMessage());
        }  catch (EncryptionException ee) {
            log.error(ee.getMessage(), ee);
            throw new ConnectorDataException(ErrorCode.OTHER_ERROR, ee.getMessage());
        } catch(Throwable e) {
            log.error(e.getMessage(),e);
            throw new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        } finally {
            this.closeStatement(statement);
            this.closeConnection(con);
        }
    }
}
