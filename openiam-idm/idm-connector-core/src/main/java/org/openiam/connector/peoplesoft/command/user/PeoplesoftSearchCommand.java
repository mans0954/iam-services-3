package org.openiam.connector.peoplesoft.command.user;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.peoplesoft.command.base.AbstractPeoplesoftCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.stereotype.Service;

/**
 * Implementation class for the Peoplesoft Connector
 */
@Service("searchUserPeopleSoftCommand")
public class PeoplesoftSearchCommand extends AbstractPeoplesoftCommand<SearchRequest<ExtensibleObject>, SearchResponse> {
    // private static final Log log =
    // LogFactory.getLog(PeoplesoftSearchCommand.class);

    @Override
    public SearchResponse execute(SearchRequest<ExtensibleObject> request) throws ConnectorDataException {

        if (log.isDebugEnabled()) {
            log.debug("AppTable lookup operation called.");
        }

        final SearchResponse response = new SearchResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = request.getObjectIdentity();

        final String targetID = request.getTargetID();

        final ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
        if (managedSys == null) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, String.format(
                    "No Managed System with target id: %s", targetID));
        }
        String schemaName = managedSys.getHostUrl();
        if (StringUtils.isBlank(managedSys.getResourceId())) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    "ResourceID is not defined in the ManagedSys Object");
        }

        final Resource res = resourceDataService.getResource(managedSys.getResourceId(), null);
        if (res == null) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "No resource for managed resource found");
        }

        try {
            String searchQuery = request.getSearchQuery() == null ? "" : request.getSearchQuery();
            response.setObjectList(this.getObjectValues(null, searchQuery, schemaName, managedSys));
            response.setStatus(StatusCodeType.SUCCESS);
        } catch (Throwable e) {
            log.error(e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.toString());
        }
        return response;
    }

}
