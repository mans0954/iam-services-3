package org.openiam.connector.scim.command.user;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.connector.scim.command.base.AbstractDeleteScimCommand;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/12/13
 * Time: 12:35 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("deleteUserScimCommand")
public class DeleteUserScimCommand extends AbstractDeleteScimCommand<ExtensibleUser> {
    private static final String DROP_USER = "DROP USER \"%s\"";

    @Override
    protected void deleteObject(String dataId, Connection con) throws ConnectorDataException {
        final String sql = String.format(DROP_USER, dataId);
        try {
            con.createStatement().execute(sql);
        } catch (SQLException e) {
            log.error(e.getMessage(),e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        }
    }
}
