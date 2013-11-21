package org.openiam.connector.linux.command.user;

import java.util.LinkedList;
import java.util.List;

import org.openiam.connector.linux.command.base.AbstractLookupLinuxCommand;
import org.openiam.connector.linux.data.LinuxUser;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.provision.service.UserAttributeHelper;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service("lookupUserLinuxCommand")
public class LookupUserLinuxCommand extends
        AbstractLookupLinuxCommand<ExtensibleUser> {

    @Override
    protected boolean lookupObject(String id, SSHAgent ssh,
            SearchResponse responseType, String mSysId)
            throws ConnectorDataException {
        String searchRule = managedSysService.getManagedSysById(mSysId)
                .getSearchHandler();
        LinuxUser user = objectToLinuxUser(id, null);
        try {
            String result = ssh.executeCommand(getUserSearchQuery(id));
            if (StringUtils.hasText(result)) {
                if (result.length() > 0) {
                    List<ObjectValue> ovList = new LinkedList<ObjectValue>();
                    ObjectValue ov = getObjectValue(searchRule, result, ssh);
                    if (ov != null) {
                        ovList.add(ov);
                    }
                    responseType.setObjectList(ovList);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    e.getMessage());
        }
    }
}
