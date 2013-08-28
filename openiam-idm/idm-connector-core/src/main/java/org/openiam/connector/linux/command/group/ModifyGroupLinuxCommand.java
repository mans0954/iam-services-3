package org.openiam.connector.linux.command.group;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.connector.linux.command.base.AbstractCrudLinuxCommand;
import org.openiam.connector.linux.data.LinuxGroup;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

@Service("modifyGroupLinuxCommand")
public class ModifyGroupLinuxCommand extends
        AbstractCrudLinuxCommand<ExtensibleUser> {
    @Override
    protected void performObjectOperation(
            CrudRequest<ExtensibleUser> crudRequest, SSHAgent ssh)
            throws ConnectorDataException {
        String oldName = null;
        if (crudRequest.getExtensibleObject() != null
                && !CollectionUtils.isEmpty(crudRequest.getExtensibleObject()
                        .getAttributes())) {
            for (ExtensibleAttribute attr : crudRequest.getExtensibleObject()
                    .getAttributes()) {
                if ("oldName".equals(attr.getName())) {
                    oldName = attr.getValue();
                }
            }
        }
        LinuxGroup group = objectToLinuxGroup(crudRequest.getObjectIdentity());
        if (group != null) {
            try {
                ssh.executeCommand(group.getModifyGroupCommand(oldName));
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                        e.getMessage());
            }
        }
    }

    /**
     * Detects whether a given modifications list contains a rename directive
     * 
     * @param extensibleUser
     * @return Original user account name; null if unchanged
     */
    private String isRename(ExtensibleUser extensibleUser) {
        for (ExtensibleAttribute att : extensibleUser.getAttributes()) {
            if (att.getOperation() != 0 && att.getName() != null
                    && att.getName().equalsIgnoreCase("ORIG_IDENTITY")) {
                return att.getValue();
            }
        }
        return null;
    }
}
