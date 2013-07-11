package org.openiam.spml2.spi.orcl.command.base;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.util.msg.ResponseBuilder;

import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/12/13
 * Time: 1:00 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractLookupOracleCommand<ProvisionObject extends GenericProvisionObject> extends AbstractOracleCommand<LookupRequestType<ProvisionObject>, LookupResponseType> {
    @Override
    public LookupResponseType execute(LookupRequestType<ProvisionObject> lookupRequestType) throws ConnectorDataException {
        final LookupResponseType response = new LookupResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String dataId = lookupRequestType.getPsoID().getID();
        /* targetID -  */
        final String targetID = lookupRequestType.getPsoID().getTargetID();

        final ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);

        Connection con = this.getConnection(managedSys);

        try {
            final ExtensibleObject resultObject = new ExtensibleObject();
            resultObject.setObjectId(dataId);
            final ResultSet rs = lookupObject(con, dataId);

            final ResultSetMetaData rsMetadata = rs.getMetaData();
            int columnCount = rsMetadata.getColumnCount();

            if(log.isDebugEnabled()) {
                log.debug(String.format("Query contains column count = %s",columnCount));
            }

            if (rs.next()) {
                for (int colIndx = 1; colIndx <= columnCount; colIndx++) {
                    final ExtensibleAttribute extAttr = new ExtensibleAttribute();
                    extAttr.setName(rsMetadata.getColumnName(colIndx));
                    setColumnValue(extAttr, colIndx, rsMetadata, rs);
                    resultObject.getAttributes().add(extAttr);
                }
                response.getAny().add(resultObject);
                response.setStatus(StatusCodeType.SUCCESS);
            } else
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "Principal not found");

            return response;
        } catch(Throwable e) {
            log.error(e.getMessage(),e);
            throw new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        } finally {
            this.closeConnection(con);
        }
    }

    private void setColumnValue(ExtensibleAttribute extAttr, int colIndx, ResultSetMetaData rsMetadata, ResultSet rs)
            throws ConnectorDataException {

        try {
            final int fieldType = rsMetadata.getColumnType(colIndx);

            if(log.isDebugEnabled()) {
                log.debug(String.format("column type = %s", fieldType));
            }

            if (fieldType == Types.INTEGER) {
                if(log.isDebugEnabled()) {
                    log.debug("type = Integer");
                }
                extAttr.setDataType("INTEGER");
                extAttr.setValue(String.valueOf(rs.getInt(colIndx)));
            }

            if (fieldType == Types.FLOAT || fieldType == Types.NUMERIC) {
                if(log.isDebugEnabled()) {
                    log.debug("type = Float");
                }
                extAttr.setDataType("FLOAT");
                extAttr.setValue(String.valueOf(rs.getFloat(colIndx)));

            }

            if (fieldType == Types.DATE) {
                if(log.isDebugEnabled()) {
                    log.debug("type = Date");
                }
                extAttr.setDataType("DATE");
                if (rs.getDate(colIndx) != null) {
                    extAttr.setValue(String.valueOf(rs.getDate(colIndx).getTime()));
                }

            }
            if (fieldType == Types.TIMESTAMP) {
                if(log.isDebugEnabled()) {
                    log.debug("type = Timestamp");
                }
                extAttr.setDataType("TIMESTAMP");
                extAttr.setValue(String.valueOf(rs.getTimestamp(colIndx).getTime()));

            }
            if (fieldType == Types.VARCHAR || fieldType == Types.CHAR) {
                if(log.isDebugEnabled()) {
                    log.debug("type = Varchar");
                }
                extAttr.setDataType("STRING");
                if (rs.getString(colIndx) != null) {
                    extAttr.setValue(rs.getString(colIndx));
                }

            }
        } catch (SQLException e) {
            log.error(e.getMessage(),e);
            throw new ConnectorDataException(ErrorCode.SQL_ERROR, e.getMessage());
        }
    }


    protected abstract ResultSet lookupObject(Connection con, String dataId) throws ConnectorDataException;
}
