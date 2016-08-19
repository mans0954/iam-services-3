package org.openiam.connector.linux.command.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openiam.connector.linux.command.base.AbstractSearchLinuxCommand;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service("searchGroupLinuxCommand")
public class SearchGroupLinuxCommand extends
        AbstractSearchLinuxCommand<ExtensibleUser> {

    @Override
    protected List<String> searchObject(SSHAgent ssh)
            throws ConnectorDataException {
        try {
            String result = ssh.executeCommand(searchGroupQuery);
            if (StringUtils.hasText(result)) {
                return Arrays.asList(result.replaceAll(NOGROUP_LINUX_GROUP, "")
                        .split("\n"));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    e.getMessage());
        }
        return new ArrayList<String>();
    }
}
