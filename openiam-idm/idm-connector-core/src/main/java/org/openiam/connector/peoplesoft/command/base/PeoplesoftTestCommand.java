package org.openiam.connector.peoplesoft.command.base;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.RequestType;
import org.openiam.base.response.ResponseType;
import org.openiam.base.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: Lev Date: 8/21/12 Time: 10:48 AM To change
 * this template use File | Settings | File Templates.
 */
@Service("testPeopleSoftCommand")
public class PeoplesoftTestCommand extends AbstractPeoplesoftCommand<RequestType<ExtensibleObject>, ResponseType> {

    @Override
    public ResponseType execute(RequestType<ExtensibleObject> request) throws ConnectorDataException {

        if (log.isDebugEnabled()) {
            log.debug("AppTable lookup operation called.");
        }

        final ResponseType response = new SearchResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        final String targetID = request.getTargetID();

        final ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
        if (managedSys == null) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, String.format(
                    "No Managed System with target id: %s", targetID));
        }
        if (managedSys.getResource() == null || StringUtils.isBlank(managedSys.getResource().getId())) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    "ResourceID is not defined in the ManagedSys Object");
        }

        this.getConnection(managedSys);
        return response;
    }

}
