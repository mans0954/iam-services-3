package org.openiam.spml2.spi.orcl.command;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.msg.password.SetPasswordRequestType;
import org.openiam.spml2.spi.orcl.command.base.AbstractOracleCommand;
import org.openiam.spml2.util.msg.ResponseBuilder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/12/13
 * Time: 1:33 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("setPasswordOracleCommand")
public class SetPasswordOracleCommand extends AbstractOracleCommand<SetPasswordRequestType, ResponseType> {
    private static final String CHANGE_PASSWORD_SQL = "ALTER USER \"%s\" IDENTIFIED BY \"%s\"";

    @Override
    public ResponseType execute(SetPasswordRequestType setPasswordRequestType) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = setPasswordRequestType.getPsoID().getID();
        /* targetID -  */
        final String targetID = setPasswordRequestType.getPsoID().getTargetID();

        final String password = setPasswordRequestType.getPassword();

        final ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);

        Connection con = this.getConnection(managedSys);
        try {
            final String sql = String.format(CHANGE_PASSWORD_SQL, principalName, password);
            if(log.isDebugEnabled()) {
                log.debug(String.format("SQL=%s", sql));
            }
            con.createStatement().execute(sql);
            return response;
        } catch (SQLException se) {
           log.error(se.getMessage(),se);
           throw new ConnectorDataException(ErrorCode.SQL_ERROR, se.getMessage());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        } finally {
           this.closeConnection(con);
        }
    }
}
