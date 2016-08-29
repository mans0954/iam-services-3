package org.openiam.connector.peoplesoft.command.user;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.peoplesoft.command.base.AbstractPeoplesoftCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.LookupRequest;
import org.openiam.base.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: Lev Date: 8/21/12 Time: 10:48 AM To change
 * this template use File | Settings | File Templates.
 */
@Service("lookupUserPeopleSoftCommand")
public class PeoplesoftLookupCommand extends AbstractPeoplesoftCommand<LookupRequest<ExtensibleObject>, SearchResponse> {

    @Override
    public SearchResponse execute(LookupRequest<ExtensibleObject> request) throws ConnectorDataException {

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
        if (managedSys.getResource() == null || StringUtils.isBlank(managedSys.getResource().getId())) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    "ResourceID is not defined in the ManagedSys Object");
        }

        try {
            response.setObjectList(this.getObjectValues(principalName, null, schemaName, managedSys));
            response.setStatus(StatusCodeType.SUCCESS);
        } catch (Throwable e) {
            log.error(e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.toString());
        }
        return response;
    }

}
