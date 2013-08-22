package org.openiam.connector.linux.command.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.apache.commons.collections.CollectionUtils;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.provision.type.ExtensibleAttribute;
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
            for (String userIdentity : usersIdentities) {
                ObjectValue obj = new ObjectValue();
                obj.setAttributeList(Arrays.asList(new ExtensibleAttribute(
                        "login", userIdentity), new ExtensibleAttribute("name",
                        userIdentity)));
                obj.setObjectIdentity(userIdentity);
                objectValues.add(obj);
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
