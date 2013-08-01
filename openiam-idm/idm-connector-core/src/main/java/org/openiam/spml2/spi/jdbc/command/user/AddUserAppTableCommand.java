package org.openiam.spml2.spi.jdbc.command.user;

import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.spi.jdbc.command.base.AbstractAddAppTableCommand;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Service("addUserAppTableCommand")
public class AddUserAppTableCommand extends AbstractAddAppTableCommand<ProvisionUser> {
    @Override
    protected void addObject(Connection con, String principalName, List<ExtensibleObject> objectList, String tableName) throws ConnectorDataException {
        // build sql
        final StringBuilder columns = new StringBuilder("");
        final StringBuilder values = new StringBuilder("");

        for (final ExtensibleObject obj : objectList) {
            PreparedStatement statement = null;
            try {
                if (identityExists(con, tableName, principalName, obj)) {
                    if(log.isDebugEnabled()) {
                        log.debug(String.format("%s exists. Returning success to the connector", principalName));
                    }
                    return;
                }

                final List<ExtensibleAttribute> attrList = obj.getAttributes();

                if(log.isDebugEnabled()) {
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

                if(log.isDebugEnabled()) {
                    log.debug(String.format("Principal column name=%s", obj.getPrincipalFieldName()));
                }
                if (obj.getPrincipalFieldName() != null) {
                    if (ctr != 0) {
                        columns.append(",");
                        values.append(",");
                    }
                    columns.append(obj.getPrincipalFieldName());
                    values.append("?");
                }

                final String sql = String.format(INSERT_SQL, tableName, columns, values);

                if(log.isDebugEnabled()) {
                    log.debug(String.format("ADD SQL=%s", sql));
                }


                statement = con.prepareStatement(sql);

                // set the parameters
                for (final ExtensibleObject extObj : objectList) {
                    final List<ExtensibleAttribute> extAttrList = extObj.getAttributes();
                    ctr = 1;
                    for (ExtensibleAttribute att : extAttrList) {
                        setStatement(statement, ctr, att);
                        ctr++;
                        if(log.isDebugEnabled()) {
                            log.debug(String.format("Binding parameter: %s -> %s", att.getName(), att.getValue()));
                        }

                    }
                }
                if (obj.getPrincipalFieldName() != null) {

                    setStatement(statement, ctr, obj.getPrincipalFieldDataType(), principalName);
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


}
