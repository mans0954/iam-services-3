package org.openiam.spml2.spi.common.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.common.AbstractCommand;
import org.openiam.spml2.util.msg.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

import java.sql.Connection;
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
}
