package org.openiam.connector.linux.command.base;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.common.command.AbstractCommand;
import org.openiam.connector.linux.data.LinuxUser;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.openiam.connector.linux.ssh.SSHConnectionFactory;
import org.openiam.connector.linux.ssh.SSHException;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractLinuxCommand<Request extends RequestType, Response extends ResponseType> extends AbstractCommand<Request, Response> {
    private SSHConnectionFactory sshConnectionFactory = new SSHConnectionFactory();

    protected SSHAgent getSSHAgent(String targetId) throws ConnectorDataException  {
       log.debug("Getting SSH for managed sys with id=" + targetId);
       return getSSHAgent(managedSysService.getManagedSysById(targetId));
    }

    protected SSHAgent getSSHAgent(ManagedSysEntity managedSys) throws ConnectorDataException {
        SSHAgent ssh = null;
        if (managedSys != null) {
            String managedSysId = managedSys.getManagedSysId();
            if (!(managedSys.getResourceId() == null || managedSys.getResourceId().length() == 0)) {
                log.debug("ManagedSys found; Name=" + managedSys.getName());

                if ((ssh = sshConnectionFactory.getSSH(managedSysId)) == null)
                    ssh = sshConnectionFactory.addSSH(managedSysId, managedSys.getHostUrl(),
                                                      managedSys.getPort(), managedSys.getUserId(),
                                                      this.getDecryptedPassword(managedSys.getPswd()));
            }
        }
        if(ssh==null)
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "Cannot establish connection");
        return ssh;
    }


    /**
     * Change the password of another account as root. Passwd expects the password to be sent twice over STDIN
     * This is more secure than changing the password via arguments to useradd/usermodify, as these would appear
     * within the process list
     *
     * @param sshAgent Handle to SSH connection
     * @param user     User with new password set
     * @throws org.openiam.connector.linux.ssh.SSHException
     */
    protected void sendPassword(SSHAgent sshAgent, LinuxUser user) throws SSHException {
        String pass = user.getPassword();
        String doubledPass = pass + "\n" + pass + "\n";
        sshAgent.executeCommand(user.getUserSetPasswordCommand(), doubledPass);
    }
    /**
     * Extracts a LinuxUser from the given list of Extensible Objects,
     *
     * @param login      Login name of new user
     * @param obj List containing attributes
     * @return A LinuxUser with the relevant fields populated
     */
    protected LinuxUser objectToLinuxUser(String login,ExtensibleObject obj) {
        LinuxUser user = null;

        if (login != null) {
            // Extract attribues into a map. Also save groups
            HashMap<String, String> attributes = new HashMap<String, String>();
            attributes.put("login", login);

            log.debug("Object:" + obj.getName() + " - operation=" + obj.getOperation());
            // Extract attributes
            for (ExtensibleAttribute att : obj.getAttributes()) {
                if (att != null) {
                    attributes.put(att.getName(), att.getValue());
                }
            }


            try {
                user = new LinuxUser(attributes);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        } else {
            log.error("Login name for Linux user not specified");
        }
        return user;
    }
}
