package org.openiam.connector.jdbc.command.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.openiam.connector.jdbc.command.data.AppTableConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;

public abstract class AbstractModifyAppTableCommand<ExtObject extends ExtensibleObject> extends
        AbstractAppTableCommand<CrudRequest<ExtObject>, ObjectResponse> {
    private static final String UPDATE_SQL = "UPDATE %s SET %s WHERE %s=?";
    private static final String INSERT_SQL = "INSERT INTO %s (%s) VALUES (%s)";

    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> crudRequest) throws ConnectorDataException {
        final ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        AppTableConfiguration configuration = this.getConfiguration(crudRequest.getTargetID());

        final String principalName = crudRequest.getObjectIdentity();
        Connection con = this.getConnection(configuration.getManagedSys());
        // modificationType contains a collection of objects for each type of
        // operation

        final ExtensibleObject obj = crudRequest.getExtensibleObject();

        try {
            int ctr = 0;
            if (identityExists(con, this.getTableName(configuration, this.getObjectType()), principalName, obj)) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Identity found. Modifying identity: %s", principalName));
                }

                final StringBuilder setBuffer = new StringBuilder();

                final List<ExtensibleAttribute> attrList = obj.getAttributes();
                final String principalFieldName = obj.getPrincipalFieldName();
                final String principalFieldDataType = obj.getPrincipalFieldDataType();

                for (ExtensibleAttribute att : attrList) {
                    if (att.getOperation() != 0 && att.getName() != null) {
                        if (compareObjectTypeWithObject(att.getObjectType())) {
                            if (ctr != 0) {
                                setBuffer.append(",");
                            }
                            ctr++;

                            setBuffer.append(String.format("%s = ?", att.getName()));
                        }
                    }
                }

                if (ctr > 0) {
                    final String sql = String.format(UPDATE_SQL,
                            this.getTableName(configuration, this.getObjectType()), setBuffer, principalFieldName);
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("SQL=%s", sql));
                    }

                    final PreparedStatement statement = con.prepareStatement(sql);

                    ctr = 1;
                    for (final ExtensibleAttribute att : attrList) {
                        if (att.getOperation() != 0 && att.getName() != null) {
                            if (compareObjectTypeWithObject(att.getObjectType())) {
                                setStatement(statement, ctr, att);
                                ctr++;
                            }
                        }
                    }
                    if (principalFieldName != null) {
                        setStatement(statement, ctr, principalFieldDataType, principalName);
                    }
                    statement.executeUpdate();
                }
            } else {
                // identity does not exist in the target system
                // identity needs to be re-provisioned
                addIdentity(con, this.getTableName(configuration, this.getObjectType()), principalName, obj);
            }
            return response;
        } catch (SQLException se) {
            log.error(se.getMessage(), se);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, se.getMessage());
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        } finally {
            this.closeConnection(con);
        }

    }

    private void addIdentity(final Connection con, final String tableName, final String principalName,
            final ExtensibleObject obj) throws ConnectorDataException {
        // build sql

        final StringBuilder columnBuf = new StringBuilder("");
        final StringBuilder valueBuf = new StringBuilder("");

        final List<ExtensibleAttribute> attrList = obj.getAttributes();

        final String principalFieldName = obj.getPrincipalFieldName();
        // String principalFieldDataType = obj.getPrincipalFieldDataType();

        if (log.isDebugEnabled()) {
            log.debug(String.format("Adding identity: %s", principalName));
            log.debug(String.format("Number of attributes to persist in ADD = %s", attrList.size()));
        }

        int ctr = 0;
        for (final ExtensibleAttribute att : attrList) {
            if (ctr != 0) {
                columnBuf.append(",");
                valueBuf.append(",");
            }
            ctr++;
            columnBuf.append(att.getName());
            valueBuf.append("?");
        }
        // add the primary key
        log.debug("Principal column name=" + obj.getPrincipalFieldName());
        if (principalFieldName != null) {
            if (ctr != 0) {
                columnBuf.append(",");
                valueBuf.append(",");
            }
            columnBuf.append(obj.getPrincipalFieldName());
            valueBuf.append("?");
        }

        final String sql = String.format(INSERT_SQL, tableName, columnBuf, valueBuf);

        if (log.isDebugEnabled()) {
            log.debug(String.format("ADD SQL=%s", sql));
        }

        PreparedStatement statement = null;
        try {
            con.prepareStatement(sql);
            ctr = 1;
            for (final ExtensibleAttribute att : attrList) {
                setStatement(statement, ctr, att);
                ctr++;
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Binding parameter: %s -> %s", att.getName(), att.getValue()));
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
