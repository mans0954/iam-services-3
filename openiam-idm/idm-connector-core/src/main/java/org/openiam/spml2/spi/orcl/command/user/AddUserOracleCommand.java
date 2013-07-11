package org.openiam.spml2.spi.orcl.command.user;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.StatusCodeType;
import org.openiam.spml2.spi.orcl.command.base.AbstractAddOracleCommand;
import org.openiam.spml2.util.msg.ResponseBuilder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/11/13
 * Time: 10:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("addUserOracleCommand")
public class AddUserOracleCommand extends AbstractAddOracleCommand<ProvisionUser> {
    private static final String INSERT_SQL = "CREATE USER \"%s\" IDENTIFIED BY \"%s\"";
    private static final String SELECT_SQL = "SELECT USER_ID FROM DBA_USERS WHERE USERNAME=?";

    @Override
    protected void addObject(String principalName, List<ExtensibleObject> objectList, List<AttributeMapEntity> attributeMap, Connection con) throws ConnectorDataException {
        if (identityExists(con, principalName)) {
            if(log.isDebugEnabled()) {
                log.debug(String.format("%s exists. Returning success to the connector", principalName));
            }
            return;
        }

        String identifiedBy = null;
        for (final ExtensibleObject obj : objectList) {
            final List<ExtensibleAttribute> attrList = obj.getAttributes();

            if(log.isDebugEnabled()) {
                log.debug(String.format("Number of attributes to persist in ADD = %s", attrList.size()));
            }

            if(CollectionUtils.isNotEmpty(attributeMap)) {
                for (final ExtensibleAttribute att : attrList) {
                    for(final AttributeMapEntity attribute : attributeMap) {
                        if(StringUtils.equalsIgnoreCase("password", attribute.getMapForObjectType())) {
                            if(StringUtils.equalsIgnoreCase(att.getName(),  attribute.getAttributeName())) {
                                identifiedBy = att.getValue();
                            }
                        }
                    }
                }
            }
        }

        if(StringUtils.isBlank(identifiedBy))
            throw new ConnectorDataException(ErrorCode.INVALID_ATTRIBUTE, "No password specified");

        final String sql = String.format(INSERT_SQL, principalName, identifiedBy);
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
