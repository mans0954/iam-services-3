package org.openiam.spml2.spi.orcl.command;

import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.spi.orcl.command.base.AbstractDeleteOracleCommand;
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
@Service("deleteUserOracleCommand")
public class DeleteUserOracleCommand extends AbstractDeleteOracleCommand<ProvisionUser> {
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
