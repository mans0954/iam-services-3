package org.openiam.connector.linux.command.base;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.LookupRequest;
import org.openiam.base.response.SearchResponse;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.openiam.provision.type.ExtensibleObject;

public abstract class AbstractLookupLinuxCommand<ExtObject extends ExtensibleObject>
        extends AbstractLinuxCommand<LookupRequest<ExtObject>, SearchResponse> {
    @Override
    public SearchResponse execute(LookupRequest<ExtObject> lookupRequest)
            throws ConnectorDataException {
        SearchResponse responseType = new SearchResponse();
        responseType.setRequestID(lookupRequest.getRequestID());
        responseType.setStatus(StatusCodeType.SUCCESS);

        SSHAgent ssh = getSSHAgent(lookupRequest.getTargetID());
        try {
            if (!lookupObject(lookupRequest.getSearchValue(), ssh,
                    responseType, lookupRequest.getTargetID()))
                throw new ConnectorDataException(ErrorCode.NO_SUCH_IDENTIFIER);

            return responseType;
        } catch (ConnectorDataException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            ssh.logout();
        }
    }

    protected abstract boolean lookupObject(String id, SSHAgent ssh,
            SearchResponse responseType, String mSysId)
            throws ConnectorDataException;
}
