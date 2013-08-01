package org.openiam.spml2.spi.orcl.command.base;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.spml2.msg.password.SetPasswordRequestType;
import org.openiam.spml2.msg.suspend.AbstractAccountStatusRequest;
import org.openiam.spml2.util.msg.ResponseBuilder;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/12/13
 * Time: 1:48 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractOracleAccountStatusCommand<Request extends AbstractAccountStatusRequest> extends AbstractOracleCommand<Request, ResponseType>  {

    protected enum AccountStatus {
        LOCKED("lock"),
        UNLOCKED("unlock");

        private String name;

        AccountStatus(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static final String SQL = "ALTER USER \"%s\" account %s";

    @Override
    public ResponseType execute(Request request) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = request.getPsoID().getID();

        final PSOIdentifierType psoID = request.getPsoID();
        /* targetID -  */
        final String targetID = psoID.getTargetID();

        final ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
        String resourceId = getResourceId(targetID, managedSys);

        Connection connection = this.getConnection(managedSys);

        try {
            final String sql = String.format(SQL, principalName, getNewAccountStatus());
            connection.createStatement().execute(sql);
            return response;
        } catch (SQLException se) {
            log.error(se.getMessage(), se);
            throw new ConnectorDataException(ErrorCode.SQL_ERROR, se.getMessage());
        }  catch(Throwable e) {
            log.error(e.getMessage(),e);
            throw new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        }finally {
            this.closeConnection(connection);
        }
    }


    protected abstract String getNewAccountStatus();
}
