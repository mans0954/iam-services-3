package org.openiam.connector.linux.command.base;

import java.util.Map;

import org.openiam.connector.linux.data.LinuxUser;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.util.connect.FileUtil;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.util.StringUtils;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 8/7/13 Time: 10:32 PM To
 * change this template use File | Settings | File Templates.
 */
public abstract class AbstractCrudLinuxCommand<ExtObject extends ExtensibleObject>
        extends AbstractLinuxCommand<CrudRequest<ExtObject>, ObjectResponse> {
    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> crudRequest)
            throws ConnectorDataException {
        ObjectResponse responseType = new ObjectResponse();
        responseType.setRequestID(crudRequest.getRequestID());
        responseType.setStatus(StatusCodeType.SUCCESS);

        SSHAgent ssh = getSSHAgent(crudRequest.getTargetID());

        try {
            performObjectOperation(crudRequest, ssh);
        } catch (ConnectorDataException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            ssh.logout();
        }
        return responseType;
    }

    protected String getScriptName(String commandHandler) {
        String name = "";
        if (StringUtils.hasText(commandHandler)) {
            String[] args = commandHandler.trim().split(" ");
            if (args != null) {
                if (args[0] != null)
                    name = args[0];
            }
        } else {
            log.info("Handler not founded");
        }
        return name;
    }

    protected String getArgs(String commandHandler) {
        String name = "";
        if (StringUtils.hasText(commandHandler)) {
            String[] args = commandHandler.trim().split(" ");
            if (args != null) {
                if (args[0] != null)
                    name = args[0];
            }
        } else {
            log.info("Handler not founded");
            return "";
        }
        return commandHandler.replace(name, "").trim();
    }

    protected abstract String getCommandScriptHandler(String id);

    protected void copyFile(SSHAgent ssh, String fileName) throws Exception {
        String localMD5 = FileUtil.getMD5Sum(localDirectory + fileName);
        String remoteMD5 = ssh
                .executeCommand("md5sum " + remoteDirectory + fileName)
                .replace(remoteDirectory + fileName, "").trim();
        if (StringUtils.hasText(localMD5) && StringUtils.hasText(localMD5)
                && localMD5.trim().equals(remoteMD5.trim())) {
            log.info(fileName + " Already copied to: " + remoteDirectory);
            return;
        }

        ssh.copyScript(localDirectory, remoteDirectory, fileName);
    }

    private void performObjectOperation(CrudRequest<ExtObject> crudRequest,
            SSHAgent ssh) throws ConnectorDataException {
        Map<String, String> user = objectToAttributes(
                crudRequest.getObjectIdentity(),
                crudRequest.getExtensibleObject());
        if (user != null) {
            try {
                String sudoPassword = this.getPassword(crudRequest
                        .getTargetID());
                ssh.connect();
                String commandHandler = this
                        .getCommandScriptHandler(crudRequest.getTargetID());
                String scriptName = this.getScriptName(commandHandler);
                String argsName = this.getArgs(commandHandler);
                String argsValues = this.userAttributesToFormatString(argsName,
                        user);
                this.copyFile(ssh, scriptName);
                StringBuilder command = new StringBuilder("sudo -S sh ");
                command.append(remoteDirectory);
                command.append(scriptName);
                command.append(" ");
                command.append(argsValues);
                log.info(ssh.executeCommand(command.toString(), sudoPassword));
                log.info(command.toString());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                        e.getMessage());
            }
        }
    }

    private String userAttributesToFormatString(String argsString,
            Map<String, String> attributes) {
        StringBuilder result = new StringBuilder();
        String[] argsName = argsString.split(" ");
        if (argsName != null) {
            for (String name : argsName) {
                if (attributes.get(name) == null) {
                    log.error("Can't execute operation. Not all data exist in extensible object");
                    return null;
                }
                result.append(attributes.get(name));
                result.append(" ");
            }
        }
        return result.toString();
    }

}
