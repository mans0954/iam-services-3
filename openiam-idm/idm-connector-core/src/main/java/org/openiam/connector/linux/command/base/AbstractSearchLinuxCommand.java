package org.openiam.connector.linux.command.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.type.ObjectValue;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.SearchRequest;
import org.openiam.base.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleObject;

public abstract class AbstractSearchLinuxCommand<ExtObject extends ExtensibleObject>
        extends AbstractLinuxCommand<SearchRequest<ExtObject>, SearchResponse> {

    @Override
    public SearchResponse execute(SearchRequest<ExtObject> searchRequest)
            throws ConnectorDataException {
        SearchResponse responseType = new SearchResponse();
        responseType.setRequestID(searchRequest.getRequestID());
        responseType.setStatus(StatusCodeType.SUCCESS);

        SSHAgent ssh = getSSHAgent(searchRequest.getTargetID());
        try {
            List<String> usersIdentities = searchObject(ssh);
            if (CollectionUtils.isEmpty(usersIdentities))
                throw new ConnectorDataException(ErrorCode.NO_SUCH_IDENTIFIER);
            List<ObjectValue> objectValues = new ArrayList<ObjectValue>();
            ManagedSysEntity ms = managedSysService
                    .getManagedSysById(searchRequest.getTargetID());
            String searchRule = ms.getSearchHandler();

            for (String userIdentity : usersIdentities) {
                ObjectValue ov = getObjectValue(searchRule, userIdentity, ssh);
                if (ov != null) {
                    objectValues.add(ov);
                }
            }
            responseType.setObjectList(objectValues);
            return responseType;
        } catch (ConnectorDataException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            ssh.logout();
        }
    }

    protected abstract List<String> searchObject(SSHAgent ssh)
            throws ConnectorDataException;
}
