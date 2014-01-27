package org.openiam.connector.jdbc.command.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.openiam.connector.jdbc.command.base.AbstractAddAppTableCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

@Service("addUserAppTableCommand")
public class AddUserAppTableCommand extends AbstractAddAppTableCommand<ExtensibleUser> {
    @Override
    protected void addObject(Connection con, String principalName, ExtensibleUser object, String tableName)
            throws ConnectorDataException {
        // build sql
        final StringBuilder columns = new StringBuilder("");
        final StringBuilder values = new StringBuilder("");

        PreparedStatement statement = null;
        try {
            if (identityExists(con, tableName, principalName, object)) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("%s exists. Returning success to the connector", principalName));
                }
                return;
            }

            final List<ExtensibleAttribute> attrList = object.getAttributes();

            if (log.isDebugEnabled()) {
                log.debug(String.format("Number of attributes to persist in ADD = %s", attrList.size()));
            }

            int ctr = 0;
            for (final ExtensibleAttribute att : attrList) {
                if (ctr != 0) {
                    columns.append(",");
                    values.append(",");
                }
                ctr++;
                columns.append(att.getName());
                values.append("?");
            }
            // add the primary key

            if (log.isDebugEnabled()) {
                log.debug(String.format("Principal column name=%s", principalName));
            }
            if (object.getPrincipalFieldName() != null) {
                if (ctr != 0) {
                    columns.append(",");
                    values.append(",");
                }
                columns.append(object.getPrincipalFieldName());
                values.append("?");
            }

            final String sql = String.format(INSERT_SQL, tableName, columns, values);

            if (log.isDebugEnabled()) {
                log.debug(String.format("ADD SQL=%s", sql));
            }

            statement = con.prepareStatement(sql);

            // set the parameters

            if (object.getPrincipalFieldName() != null) {
                setStatement(statement, ctr, object.getPrincipalFieldDataType(), principalName);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        } finally {
            this.closeStatement(statement);
        }
    }

}
