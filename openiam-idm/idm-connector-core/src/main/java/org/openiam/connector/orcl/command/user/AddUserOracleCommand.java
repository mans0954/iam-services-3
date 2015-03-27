package org.openiam.connector.orcl.command.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.orcl.command.base.AbstractAddOracleCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/11/13
 * Time: 10:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("addUserOracleCommand")
public class AddUserOracleCommand extends AbstractAddOracleCommand<ExtensibleUser> {
    private static final String INSERT_SQL = "CREATE USER \"%s\" IDENTIFIED BY \"%s\"";
    private static final String SELECT_SQL = "SELECT USER_ID FROM DBA_USERS WHERE USERNAME=?";

   
    @Override
    protected void addObject(CrudRequest<ExtensibleUser> crudRequest, List<AttributeMapEntity> attributeMap, Connection con) throws ConnectorDataException {
    	
        if (identityExists(con, crudRequest.getObjectIdentity())) {
            if(log.isDebugEnabled()) {
                log.debug(String.format("%s exists. Returning success to the connector", crudRequest.getObjectIdentity()));
            }
            return;
        }

        String identifiedBy = null;
        ExtensibleObject obj = crudRequest.getExtensibleObject();
        
        if (StringUtils.isBlank(crudRequest.getObjectIdentity())) {
            throw new ConnectorDataException(ErrorCode.INVALID_ATTRIBUTE, "No identity specified");
        }

        log.debug("Create user:" + crudRequest.getObjectIdentity());

        // Extract attributes
        for (ExtensibleAttribute att : obj.getAttributes()) {
            if ("PASSWORD".equalsIgnoreCase(att.getName())) {
                identifiedBy = att.getValue();
            }
        }

        if(StringUtils.isBlank(identifiedBy)) {
            throw new ConnectorDataException(ErrorCode.INVALID_ATTRIBUTE, "No password specified");
        }

        final String sql = String.format(INSERT_SQL,  crudRequest.getObjectIdentity(), identifiedBy);
        if(log.isDebugEnabled()) {
            log.debug(String.format("SQL=%s", sql));
        }

        try {
            con.createStatement().execute(sql);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        }
    }

    protected boolean identityExists(final Connection connection, final String principalName) throws ConnectorDataException {
    	
        PreparedStatement statement = null;
        try {
            boolean exists = false;
            if(connection != null) {
                if(org.mule.util.StringUtils.isNotBlank(principalName)) {
                    statement = connection.prepareStatement(SELECT_SQL);
                    statement.setString(1, principalName);
                    final ResultSet rs = statement.executeQuery();
                    if (rs != null && rs.next()) {
                        return true;
                    }
                }
            }
            return exists;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        } finally {
            this.closeStatement(statement);
        }

    }
}
