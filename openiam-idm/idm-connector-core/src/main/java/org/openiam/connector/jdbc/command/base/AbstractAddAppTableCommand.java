package org.openiam.connector.jdbc.command.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.openiam.base.BaseAttribute;
import org.openiam.connector.jdbc.command.data.AppTableConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;

public abstract class AbstractAddAppTableCommand<ExtObject extends ExtensibleObject> extends
        AbstractAppTableCommand<CrudRequest<ExtObject>, ObjectResponse> {

    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> crudRequest) throws ConnectorDataException {
        final ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        AppTableConfiguration configuration = this.getConfiguration(crudRequest.getTargetID());

        final String principalName = crudRequest.getObjectIdentity();
        final ExtObject extObject = crudRequest.getExtensibleObject();

        if (log.isDebugEnabled()) {
            log.debug(String.format("ExtensibleObject in Add Request=%s", extObject));
        }
        Connection con = getConnection(configuration.getManagedSys());
        try {
            addObject(con, principalName, extObject, configuration, this.getObjectType());
            return response;
        } catch (ConnectorDataException e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            this.closeConnection(con);
        }
    }

    private void addObject(Connection con, String principalName, ExtensibleObject object, AppTableConfiguration config,
            String objectType) throws ConnectorDataException {
        // build sql
        final StringBuilder columns = new StringBuilder("");
        final StringBuilder values = new StringBuilder("");
        String sql = "";
        int ctr = 0;
        final List<ExtensibleAttribute> attrList = object.getAttributes();
        if (!CollectionUtils.isEmpty(attrList) && !StringUtils.isEmpty(this.getTableName(config, objectType))) {
            try {
                if (identityExists(con, this.getTableName(config, objectType), principalName, object)) {
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("%s exists. Returning success to the connector", principalName));
                    }
                    return;
                }
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Number of attributes to persist in ADD = %s", attrList.size()));
                }

                for (final ExtensibleAttribute att : attrList) {
                    if (att.getAttributeContainer() != null
                            && !CollectionUtils.isEmpty(att.getAttributeContainer().getAttributeList())) {
                        for (BaseAttribute a : att.getAttributeContainer().getAttributeList()) {
                            String supportedObjType = a.getName();
                            ExtensibleObject ea = this.createNewExtensibleObject(a);
                            this.addObject(con, ea.getObjectId(), ea, config, supportedObjType);
                        }
                    } else {
                        if (ctr != 0) {
                            columns.append(",");
                            values.append(",");
                        }
                        ctr++;
                        columns.append(att.getName());
                        values.append("?");
                    }
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

                sql = String.format(INSERT_SQL, this.getTableName(config, objectType), columns, values);

                if (log.isDebugEnabled()) {
                    log.debug(String.format("ADD SQL=%s", sql));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            }
            PreparedStatement statement = null;
            try {
                statement = con.prepareStatement(sql);
                // set the parameters
                for (int i = 0; i < ctr; i++) {
                    setStatement(statement, i + 1, attrList.get(i).getDataType(), attrList.get(i).getValue());
                }

                if (object.getPrincipalFieldName() != null) {
                    setStatement(statement, ctr + 1, object.getPrincipalFieldDataType(), principalName);
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
