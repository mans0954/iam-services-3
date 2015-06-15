package org.openiam.connector.jdbc.command.base;

import java.sql.Connection;
import java.util.List;

import org.openiam.connector.jdbc.command.data.AppTableConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.provision.type.ExtensibleObject;

public abstract class AbstractLookupAppTableCommand<ExtObject extends ExtensibleObject> extends
        AbstractGetAppTableCommand<ExtObject, LookupRequest<ExtObject>, SearchResponse> {
    @Override
    public SearchResponse execute(LookupRequest<ExtObject> lookupRequest) throws ConnectorDataException {
        final SearchResponse response = new SearchResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = lookupRequest.getSearchValue();
        AppTableConfiguration configuration = this.getConfiguration(lookupRequest.getTargetID());
        Connection con = this.getConnection(configuration.getManagedSys());
        List<ObjectValue> objectValue = null;
        final List<AttributeMapEntity> attrMap = attributeMaps(configuration.getResourceId());
        try {
            // lookup users
            objectValue = createUserSelectStatement(con, this.getTableName(configuration, this.getObjectType()), principalName,
                    configuration, attrMap, null);
            // Get linked objects
            if (objectValue != null)
                response.getObjectList().addAll(objectValue);
            response.setStatus(StatusCodeType.SUCCESS);
            return response;
        } catch (Exception se) {
            log.error(se.getMessage(), se);
            throw new ConnectorDataException(ErrorCode.SQL_ERROR, se.getMessage());
        } finally {
            this.closeConnection(con);
        }
    }

}
