package org.openiam.spml2.spi.jdbc.command.user;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.pswd.service.PasswordGenerator;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.msg.suspend.SuspendRequestType;
import org.openiam.spml2.spi.jdbc.command.base.AbstractAppTableCommand;
import org.openiam.spml2.spi.jdbc.command.data.AppTableConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service("suspendAppTableCommand")
public class SuspendAppTableCommand extends AbstractAppTableCommand<SuspendRequestType, ResponseType> {
    @Autowired
    private PasswordGenerator passwordGenerator;

    @Override
    public ResponseType execute(SuspendRequestType suspendRequestType) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = suspendRequestType.getPsoID().getID();

        final PSOIdentifierType psoID = suspendRequestType.getPsoID();
        /* targetID -  */
        final String targetID = psoID.getTargetID();

        final String password = passwordGenerator.generatePassword(10);

        final ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
        AppTableConfiguration configuration = this.getConfiguration(targetID, managedSys);
        Connection con = this.getConnection(managedSys);
        PreparedStatement statement = null;
        try {
            statement = createSetPasswordStatement(con, configuration.getResourceId(), configuration.getTableName(), principalName, password);
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
