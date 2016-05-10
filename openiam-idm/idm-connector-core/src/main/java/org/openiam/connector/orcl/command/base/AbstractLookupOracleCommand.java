package org.openiam.connector.orcl.command.base;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;

import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/12/13
 * Time: 1:00 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractLookupOracleCommand<ExtObject extends ExtensibleObject> extends AbstractOracleCommand<LookupRequest<ExtObject>, SearchResponse> {
    @Override
    public SearchResponse execute(LookupRequest<ExtObject> lookupRequest) throws ConnectorDataException {
        final SearchResponse response = new SearchResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        final String dataId = lookupRequest.getSearchValue();
        /* targetID -  */
        ConnectorConfiguration config =  getConfiguration(lookupRequest.getTargetID(), ConnectorConfiguration.class);
        Connection con = this.getConnection(config.getManagedSys());

        try {
            final ObjectValue resultObject = new ObjectValue();
       
            resultObject.setObjectIdentity(dataId);
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
                    resultObject.getAttributeList().add(extAttr);
                }
                response.getObjectList().add(resultObject);
                response.setStatus(StatusCodeType.SUCCESS);
            }else {
            	response.setStatus(StatusCodeType.FAILURE);
            	if(log.isDebugEnabled()) {
            		log.debug("LOOKUP successful without results.");
            	}
            }
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
                if (rs.getTimestamp(colIndx) !=null){
                    extAttr.setValue(String.valueOf(rs.getTimestamp(colIndx).getTime()));
                 }

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
