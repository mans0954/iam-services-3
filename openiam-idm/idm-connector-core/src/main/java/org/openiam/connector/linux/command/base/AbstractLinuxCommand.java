package org.openiam.connector.linux.command.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openiam.connector.common.command.AbstractCommand;
import org.openiam.connector.linux.data.LinuxGroup;
import org.openiam.connector.linux.data.LinuxUser;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.openiam.connector.linux.ssh.SSHConnectionFactory;
import org.openiam.connector.linux.ssh.SSHException;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapObjectTypeOptions;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

public abstract class AbstractLinuxCommand<Request extends RequestType, Response extends ResponseType>
        extends AbstractCommand<Request, Response> {
    private SSHConnectionFactory sshConnectionFactory = new SSHConnectionFactory();
    protected static final String searchUserQuery = "awk -F: \'$3 >= 500 {print $1\";\"$5}\' /etc/passwd";
    protected static final String NOBODY_LINUX_USER = "nobody";
    protected static final String searchGroupQuery = "awk -F: \'$3 >= 500 {print $1}\' /etc/group";
    protected static final String NOGROUP_LINUX_GROUP = "nogroup\n";
    protected static final String LINUX_GROUPS = "GROUPS";
    protected static final String LINUX_LOGIN = "LOGIN";
    protected static final String LINUX_GECOS = "GECOS";

    @Value("${remote.linux.command.directory}")
    protected String remoteDirectory;
    @Value("${local.linux.command.directory}")
    protected String localDirectory;

    protected SSHAgent getSSHAgent(String targetId)
            throws ConnectorDataException {
        log.debug("Getting SSH for managed sys with id=" + targetId);
        return getSSHAgent(managedSysService.getManagedSysById(targetId));
    }

    protected String getKeyField(String mSysId) {
        List<AttributeMapEntity> attrMapList = managedSysService
                .getAttributeMapsByManagedSysId(mSysId);
        String key = "";
        for (AttributeMapEntity attrMap : attrMapList) {
            if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(
                    attrMap.getMapForObjectType())) {
                key = attrMap.getAttributeName();
            }
        }
        return key;
    }

    protected SSHAgent getSSHAgent(ManagedSysEntity managedSys)
            throws ConnectorDataException {
        SSHAgent ssh = null;
        if (managedSys != null) {
            String managedSysId = managedSys.getManagedSysId();
            if (!(managedSys.getResourceId() == null || managedSys
                    .getResourceId().length() == 0)) {
                log.debug("ManagedSys found; Name=" + managedSys.getName());

                if ((ssh = sshConnectionFactory.getSSH(managedSysId)) == null)
                    ssh = sshConnectionFactory.addSSH(managedSysId,
                            managedSys.getHostUrl(), managedSys.getPort(),
                            managedSys.getUserId(),
                            this.getDecryptedPassword(managedSys.getPswd()));
            }
        }
        if (ssh == null)
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    "Cannot establish connection");
        return ssh;
    }

    /**
     * Change the password of another account as root. Passwd expects the
     * password to be sent twice over STDIN This is more secure than changing
     * the password via arguments to useradd/usermodify, as these would appear
     * within the process list
     * 
     * @param sshAgent
     *            Handle to SSH connection
     * @param user
     *            User with new password set
     * @throws org.openiam.connector.linux.ssh.SSHException
     */
    protected void sendPassword(SSHAgent sshAgent, LinuxUser user,
            String sudoPassword) throws SSHException {
        String pass = user.getPassword();
        String doubledPass = pass + "\n" + pass + "\n" + sudoPassword + "\n";
        sshAgent.executeCommand(user.getUserSetPasswordCommand(), doubledPass);
    }

    protected HashMap<String, String> objectToAttributes(String login,
            ExtensibleObject obj) {
        HashMap<String, String> attributes = new HashMap<String, String>();
        if (StringUtils.hasText(login)) {
            // Extract attribues into a map. Also save groups

            attributes.put("login", login);
            if (obj == null) {
                log.debug("Object: not provided, just identity, seems it is delete operation");
            } else {
                log.debug("Object:" + obj.getName() + " - operation="
                        + obj.getOperation());
                // Extract attributes
                for (ExtensibleAttribute att : obj.getAttributes()) {
                    if (att != null) {
                        attributes.put(att.getName().toLowerCase(),
                                att.getValue());
                    }
                }
            }
        } else {
            log.error("Login name for Linux user not specified");
        }
        return attributes;
    }

    /**
     * Extracts a LinuxUser from the given list of Extensible Objects,
     * 
     * @param login
     *            Login name of new user
     * @param obj
     *            List containing attributes
     * @return A LinuxUser with the relevant fields populated
     */
    protected LinuxUser objectToLinuxUser(String login, ExtensibleObject obj) {
        LinuxUser user = null;
        try {
            user = new LinuxUser(this.objectToAttributes(login, obj));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return user;
    }

    protected LinuxGroup objectToLinuxGroup(String name) {
        LinuxGroup group = null;

        if (StringUtils.hasText(name)) {
            log.debug("Object: group" + name);
            group = new LinuxGroup(name);
        } else {
            log.error("Login name for Linux user not specified");
        }
        return group;
    }

    protected String getGroups(LinuxUser user, SSHAgent ssh) {
        String groups = "";
        try {
            groups = ssh.executeCommand(user.getUserGroupsCommand());
        } catch (Exception e) {
            log.error(e);
            log.error(e.getStackTrace());
            return groups;
        }
        if (StringUtils.hasText(groups)) {
            try {
                String[] gr = groups.split(":");
                if (gr != null && gr.length > 1) {
                    log.info("GROUPS FOR USER:" + user.getLogin() + ": "
                            + gr[1].trim());
                    return gr[1].trim();
                }
            } catch (Exception e) {
                log.info("groups not founded");
            }
        }
        return groups;
    }

    protected String getPassword(String managedSystemId)
            throws ConnectorDataException {
        ManagedSysEntity mSys = managedSysService
                .getManagedSysById(managedSystemId);
        return this.getDecryptedPassword(mSys.getPswd());
    }

    protected Map<String, String> ruleToMap(String searchRule) {
        Map<String, String> resultMap = new HashMap<String, String>();
        if (StringUtils.hasText(searchRule)) {
            String splitted[] = searchRule.split(",");
            if (splitted == null)
                return resultMap;
            for (String rule : splitted) {
                String splitterRule[] = rule.split("=");
                if (splitterRule.length != 2) {
                    continue;
                } else {
                    String values[] = splitterRule[1].split(":");
                    if (values.length > 1) {
                        for (int i = 0; i < values.length; i++) {
                            resultMap.put(splitterRule[0].trim() + i,
                                    values[i].trim());
                        }
                    } else {
                        resultMap.put(splitterRule[0].trim(),
                                splitterRule[1].trim());
                    }
                }
            }
        }
        return resultMap;
    }

    protected String getUserSearchQuery(String login) {
        StringBuilder sb = new StringBuilder(searchUserQuery);
        sb.append("| grep '");
        sb.append(login);
        sb.append("'");
        return sb.toString();
    }

    protected ObjectValue getObjectValue(String searchRule,
            String userAsString, SSHAgent ssh) {
        ObjectValue obj = new ObjectValue();
        String[] linuxUser = userAsString.split(";");
        if (linuxUser != null && linuxUser.length > 0) {

            LinuxUser linuxU = objectToLinuxUser(userAsString, null);
            // login
            String linuxLogin = linuxUser[0];
            if (linuxLogin.trim().equals(NOBODY_LINUX_USER)) {
                return null;
            }
            // groups
            String linuxGroups = this.getGroups(linuxU, ssh);
            // GECOS
            String gecos = "";
            if (linuxUser.length > 1) {
                gecos = linuxUser[1];
            }

            obj.setObjectIdentity(linuxLogin);

            Map<String, String> ruleMap = this.ruleToMap(searchRule);
            if (ruleMap.get(LINUX_LOGIN) != null) {
                obj.getAttributeList().add(
                        new ExtensibleAttribute(ruleMap.get(LINUX_LOGIN),
                                linuxLogin));
            }
            if (ruleMap.get(LINUX_GROUPS) != null) {
                obj.getAttributeList().add(
                        new ExtensibleAttribute(ruleMap.get(LINUX_GROUPS),
                                linuxGroups));
            }
            if (ruleMap.get(LINUX_GECOS) != null) {
                obj.getAttributeList()
                        .add(new ExtensibleAttribute(ruleMap.get(LINUX_GECOS),
                                gecos));
            } else {
                String splittedGecos[] = gecos.split(",");
                for (int i = 0; i < splittedGecos.length; i++) {
                    if (ruleMap.get(LINUX_GECOS + i) != null) {
                        obj.getAttributeList().add(
                                new ExtensibleAttribute(ruleMap.get(LINUX_GECOS
                                        + i), splittedGecos[i]));
                    }
                }
            }
        }
        return obj;
    }
}
