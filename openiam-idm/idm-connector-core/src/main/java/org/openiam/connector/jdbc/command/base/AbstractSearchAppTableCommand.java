package org.openiam.connector.jdbc.command.base;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;

import org.openiam.connector.jdbc.command.data.AppTableConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.provision.type.ExtensibleObject;

public abstract class AbstractSearchAppTableCommand<ExtObject extends ExtensibleObject> extends
        AbstractGetAppTableCommand<ExtObject, SearchRequest<ExtObject>, SearchResponse> {
    @Override
    public SearchResponse execute(SearchRequest<ExtObject> searchRequest) throws ConnectorDataException {
        final SearchResponse response = new SearchResponse();
        response.setStatus(StatusCodeType.SUCCESS);
        final String searchQuery = searchRequest.getSearchQuery();
        AppTableConfiguration configuration = this.getConfiguration(searchRequest.getTargetID());
        Connection con = this.getConnection(configuration.getManagedSys());
        List<AttributeMapEntity> attrMap = (configuration.getManagedSys().getResource() != null) ? this.attributeMaps(configuration.getManagedSys().getResource().getId()) : Collections.EMPTY_LIST;
        try {
            List<ObjectValue> oVals = this.createUserSelectStatement(con, configuration.getUserTableName(), null,
                    configuration, attrMap, searchQuery);
            if (oVals != null) {
                response.setObjectList(oVals);
            }
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
