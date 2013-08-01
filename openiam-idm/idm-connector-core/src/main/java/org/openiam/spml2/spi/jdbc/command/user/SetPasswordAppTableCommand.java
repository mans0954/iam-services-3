package org.openiam.spml2.spi.jdbc.command.user;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.spml2.msg.password.SetPasswordRequestType;
import org.openiam.spml2.spi.jdbc.command.base.AbstractAppTableCommand;
import org.openiam.spml2.spi.jdbc.command.data.AppTableConfiguration;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service("setPasswordAppTableCommand")
public class SetPasswordAppTableCommand extends AbstractAppTableCommand<SetPasswordRequestType, ResponseType> {
    @Override
    public ResponseType execute(SetPasswordRequestType setPasswordRequestType) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = setPasswordRequestType.getPsoID().getID();

        final PSOIdentifierType psoID = setPasswordRequestType.getPsoID();
        /* targetID -  */
        final String targetID = psoID.getTargetID();

        final String password = setPasswordRequestType.getPassword();

        AppTableConfiguration configuration = this.getConfiguration(targetID);
        Connection con = this.getConnection(configuration.getManagedSys());

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
