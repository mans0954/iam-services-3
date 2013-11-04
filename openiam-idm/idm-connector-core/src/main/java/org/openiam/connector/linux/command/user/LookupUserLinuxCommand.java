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
        LinuxUser user = objectToLinuxUser(id, null);
        if (user != null) {
            try {
                String result = ssh.executeCommand(user.getUserExistsCommand());
                if (StringUtils.hasText(result)) {
                    String resMas[] = result.split(":");
                    if (resMas != null && resMas.length > 0) {
                        result = resMas[0].trim();
                        String key = this.getKeyField(mSysId);
                        if (result.length() > 0) {
                            List<ObjectValue> ovList = new LinkedList<ObjectValue>();
                            ObjectValue ov = new ObjectValue();
                            ov.setObjectIdentity(result);
                            ov.setAttributeList(new LinkedList<ExtensibleAttribute>());
                            ov.getAttributeList().add(
                                    new ExtensibleAttribute(key, result));
                            String groups = ssh.executeCommand(user
                                    .getUserGroupsCommand());
                            if (StringUtils.hasText(groups)) {
                                try {
                                    String[] gr = groups.split(":");
                                    if (gr != null && gr.length > 1) {
                                        ov.getAttributeList().add(
                                                new ExtensibleAttribute(
                                                        "groups", gr[1]
                                                                .trim()));
                                        log.info("GROUPS FOR USER:" + result
                                                + ": " + gr[1].trim());
                                    }
                                } catch (Exception e) {
                                    log.info("groups not founded");
                                }
                            }
                            ov.setObjectIdentity(key);
                            ovList.add(ov);
                            responseType.setObjectList(ovList);
                            return true;
                        }
                    }
                }
                return false;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                        e.getMessage());
            }
        }
        return false;
    }
}
