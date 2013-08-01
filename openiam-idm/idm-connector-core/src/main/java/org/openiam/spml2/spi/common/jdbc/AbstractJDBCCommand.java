package org.openiam.spml2.spi.common.jdbc;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.connector.common.command.AbstractCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/21/12
 * Time: 10:27 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractJDBCCommand<Request extends RequestType, Response extends ResponseType> extends AbstractCommand<Request, Response> {


    @Autowired
    @Qualifier("jdbcConnection")
    protected JDBCConnectionMgr connectionMgr;

    protected List<AttributeMapEntity> attributeMaps(final String resourceId) {
        return managedSysService.getResourceAttributeMaps(resourceId);
    }

    protected Connection getConnection(ManagedSysEntity managedSys) throws ConnectorDataException {
        ManagedSysDto dto = managedSysDozerConverter.convertToDTO(managedSys, false);
        dto.setDecryptPassword(this.getDecryptedPassword(managedSys.getUserId(), managedSys.getPswd()));
        Connection con = null;

        try {
            con = connectionMgr.connect(dto);
        } catch (Exception e) {
           log.error(e.getMessage(), e);
           throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "Cannot connect to to target system");
        }
        if(con == null)
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "Cannot connect to to target system");
        return con;
    }

    protected void closeConnection(Connection con){
        if (con != null) {
            try {
                con.close();
            } catch (SQLException s) {
                log.error(s.toString());
            }
        }
    }

    protected void closeStatement(PreparedStatement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

}
