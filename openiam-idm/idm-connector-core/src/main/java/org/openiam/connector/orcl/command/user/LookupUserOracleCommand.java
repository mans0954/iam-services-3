package org.openiam.connector.orcl.command.user;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.connector.orcl.command.base.AbstractLookupOracleCommand;
import org.springframework.stereotype.Service;

import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/12/13
 * Time: 1:17 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("lookupUserOracleCommand")
public class LookupUserOracleCommand extends AbstractLookupOracleCommand<ExtensibleUser> {
    private static final String SELECT_USER = "SELECT * FROM DBA_USERS WHERE USERNAME=?";

    @Override
    protected ResultSet lookupObject(Connection con, String dataId) throws ConnectorDataException {
        PreparedStatement statement = null;
        try {
            statement = con.prepareStatement(SELECT_USER);
            statement.setString(1, dataId);

            return statement.executeQuery();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,e.getMessage());
        } finally {
            //this.closeStatement(statement);
        }
    }
}
