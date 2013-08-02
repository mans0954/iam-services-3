package org.openiam.spml2.spi.linux;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.type.*;
import org.openiam.connector.type.ResponseType;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemObjectMatchDAO;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.base.AbstractSpml2Complete;
import org.openiam.connector.ConnectorService;
import org.openiam.spml2.spi.linux.ssh.SSHAgent;
import org.openiam.spml2.spi.linux.ssh.SSHConnectionFactory;
import org.openiam.spml2.spi.linux.ssh.SSHException;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.HashMap;

/**
 * User: kevin
 * Date: 2/26/12
 * Time: 2:12 AM
 */
@WebService(endpointInterface = "org.openiam.connector.ConnectorService",
        targetNamespace = "http://www.openiam.org/service/connector",
        portName = "LinuxConnectorServicePort",
        serviceName = "LinuxConnectorService")
public class LinuxConnectorImpl extends AbstractSpml2Complete implements ConnectorService {
    private static final Log log = LogFactory.getLog(LinuxConnectorImpl.class);

    protected ManagedSystemWebService managedSysService;
    protected ManagedSystemObjectMatchDAO managedSysObjectMatchDao;
    protected ResourceDataService resourceDataService;


    // ------------------- SSH connection methods
    private SSHConnectionFactory connections = new SSHConnectionFactory();

    private SSHAgent getSSH(ManagedSysDto managedSys) {
        SSHAgent ssh = null;

        if (managedSys != null) {
            String managedSysId = managedSys.getManagedSysId();
            if (!(managedSys.getResourceId() == null || managedSys.getResourceId().length() == 0)) {
                log.debug("ManagedSys found; Name=" + managedSys.getName());

                if ((ssh = connections.getSSH(managedSysId)) == null)
                    ssh = connections.addSSH(managedSysId, managedSys.getHostUrl(), managedSys.getPort(), managedSys.getUserId(), managedSys.getDecryptPassword());
            }
        }

        return ssh;
    }

    private SSHAgent getSSH(String targetId) {
        log.debug("Getting SSH for managed sys with id=" + targetId);
        return getSSH(managedSysService.getManagedSys(targetId));
    }
    // ------------------------------------------


    public ResponseType reconcileResource(@WebParam(name = "config", targetNamespace = "") ReconciliationConfig config) {
        ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.FAILURE);
        response.setError(ErrorCode.UNSUPPORTED_OPERATION);
        return response;
    }

    // ---------- Interface
    public ResponseType testConnection(@WebParam(name = "managedSys", targetNamespace = "") ManagedSysDto managedSys) {
        String host = managedSys.getHostUrl() + ":" + managedSys.getPort().toString();
        log.debug("Testing SSH connection with Linux host " + host + " (user= " + managedSys.getUserId() + ")");

        ResponseType r = new ResponseType();
        r.setStatus((getSSH(managedSys) == null) ? StatusCodeType.FAILURE : StatusCodeType.SUCCESS);
        return r;
    }

    @Override
    public SearchResponse search(@WebParam(name = "searchRequest", targetNamespace = "") SearchRequest searchRequest) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    /**
     * Extracts a LinuxUser from the given list of Extensible Objects,
     *
     * @param login      Login name of new user
     * @param obj ExtensibleObject containing attributes
     * @return A LinuxUser with the relevant fields populated
     */
     private LinuxUser objectToLinuxUser(String login, ExtensibleObject obj) {
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

    /**
     * Change the password of another account as root. Passwd expects the password to be sent twice over STDIN
     * This is more secure than changing the password via arguments to useradd/usermodify, as these would appear
     * within the process list
     *
     * @param sshAgent Handle to SSH connection
     * @param user     User with new password set
     * @throws SSHException
     */
    private void sendPassword(SSHAgent sshAgent, LinuxUser user) throws SSHException {
        String pass = user.getPassword();
        String doubledPass = pass + "\n" + pass + "\n";
        sshAgent.executeCommand(user.getUserSetPasswordCommand(), doubledPass);
    }


    /**
     * Compares the user's group list with that of the server. Any groups which are not defined on the server
     * will be created automatically
     *
     * @param sshAgent   Handle to SSH connection
     * @param userGroups Groups to which the user should be added
     * @throws SSHException
     */
    private void addNonExistingGroups(SSHAgent sshAgent, LinuxGroups userGroups) throws SSHException {
        // Get list of groups which are not yet defined on the server
        String cmd = userGroups.getGroupsNotOnServerCommand();
        if (cmd != null) {
            String newGroups = sshAgent.executeCommand(cmd);

            // Add any new groups that are not defined on the server
            LinuxGroups groupsNotOnServer = new LinuxGroups(newGroups);
            if (groupsNotOnServer.hasGroups())
                sshAgent.executeCommand(groupsNotOnServer.getAddGroupsCommand());
        }
    }



    public UserResponse add(@WebParam(name = "reqType", targetNamespace = "") UserRequest reqType) {
        log.debug("Add user called");
        UserResponse responseType = new UserResponse();
        responseType.setRequestID(reqType.getRequestID());
        responseType.setStatus(StatusCodeType.FAILURE);

        LinuxUser user = objectToLinuxUser(reqType.getUserIdentity(), reqType.getUser());
        if (user != null) {
            SSHAgent ssh = getSSH(reqType.getTargetID());
            if (ssh != null) {
                try {
                    // Check groups and add if necessary
                    addNonExistingGroups(ssh, user.getGroups());

                    // Then add user
                    ssh.executeCommand(user.getUserAddCommand());
                    sendPassword(ssh, user);
                    ssh.executeCommand(user.getUserSetDetailsCommand());

                    responseType.setStatus(StatusCodeType.SUCCESS);
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    ssh.logout();
                }
            }
        }

        return responseType;
    }


    /**
     * Detects whether a given modifications list contains a rename directive
     *
     * @param obj ExtensibleObject with atributes
     * @return Original user account name; null if unchanged
     */
    private String isRename(ExtensibleObject obj) {

        for (ExtensibleAttribute att : obj.getAttributes()) {
            if (att.getOperation() != 0 && att.getName() != null && att.getName().equalsIgnoreCase("ORIG_IDENTITY")) {
                return att.getValue();
            }
        }
        return null;
    }


    public UserResponse modify(@WebParam(name = "reqType", targetNamespace = "") UserRequest reqType) {
        log.debug("Modify user called");

        UserResponse responseType = new UserResponse();
        responseType.setRequestID(reqType.getRequestID());
        responseType.setStatus(StatusCodeType.FAILURE);

    //    PSOIdentifierType psoID = reqType.getPsoID();

        String originalName = isRename(reqType.getUser());
        String login = (originalName == null) ? reqType.getUserIdentity() : originalName;

        LinuxUser user = objectToLinuxUser(reqType.getUserIdentity(), reqType.getUser());

        if (user != null) {
            SSHAgent ssh = getSSH(reqType.getTargetID());
            if (ssh != null) {
                try {
                    // Check groups and add if necessary
                    addNonExistingGroups(ssh, user.getGroups());

                    // Then modify account
                    ssh.executeCommand(user.getUserModifyCommand(login));
                    ssh.executeCommand(user.getUserSetDetailsCommand());
                    sendPassword(ssh, user);

                    responseType.setStatus(StatusCodeType.SUCCESS);
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    ssh.logout();
                }
            }
        }

        return responseType;
    }


    public UserResponse delete(@WebParam(name = "reqType", targetNamespace = "") UserRequest reqType) {
        log.debug("Delete user called");

        UserResponse responseType = new UserResponse();
        responseType.setRequestID(reqType.getRequestID());
        responseType.setStatus(StatusCodeType.FAILURE);

        LinuxUser user = objectToLinuxUser(reqType.getUserIdentity(), null);
        if (user != null) {
            SSHAgent ssh = getSSH(reqType.getTargetID());
            if (ssh != null) {
                try {
                    ssh.executeCommand(user.getUserDeleteCommand());
                    responseType.setStatus(StatusCodeType.SUCCESS);
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    ssh.logout();
                }
            }
        }

        return responseType;
    }


    public SearchResponse lookup(@WebParam(name = "reqType", targetNamespace = "") LookupRequest reqType) {
        log.debug("Lookup user called");

        SearchResponse responseType = new SearchResponse();
        responseType.setRequestID(reqType.getRequestID());
        responseType.setStatus(StatusCodeType.FAILURE);

        LinuxUser user = objectToLinuxUser(reqType.getSearchValue(), null);
        if (user != null) {
            SSHAgent ssh = getSSH(reqType.getTargetID());
            if (ssh != null) {
                try {
                    String result = ssh.executeCommand(user.getUserExistsCommand());

                    if (result != null && result.trim().length() > 0)
                        responseType.setStatus(StatusCodeType.SUCCESS);
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    ssh.logout();
                }
            }
        }

        return responseType;
    }

    /*
* (non-Javadoc)
*
* @see org.openiam.spml2.interf.SpmlCore#lookupAttributeNames(org.openiam.spml2.msg.
* LookupAttributeRequestType)
*/
    public LookupAttributeResponse lookupAttributeNames(LookupRequest reqType){
        LookupAttributeResponse respType = new LookupAttributeResponse();
        respType.setStatus(StatusCodeType.FAILURE);
        respType.setError(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION);

        return respType;
    }

    public ResponseType setPassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        log.debug("Set password called");

        ResponseType responseType = new ResponseType();
        responseType.setRequestID(request.getRequestID());
        responseType.setStatus(StatusCodeType.FAILURE);

        String login = request.getUserIdentity();
        String password = request.getPassword();
        SSHAgent ssh = getSSH(request.getTargetID());
        if (ssh != null) {
            try {
                LinuxUser user = new LinuxUser(null, login, password, null, null, null, null, null, null, null);

                sendPassword(ssh, user);
                responseType.setStatus(StatusCodeType.SUCCESS);
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                ssh.logout();
            }
        }

        return responseType;
    }


    public ResponseType expirePassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        log.debug("Expire password called");

        ResponseType responseType = new ResponseType();
        responseType.setRequestID(request.getRequestID());
        responseType.setStatus(StatusCodeType.FAILURE);

        String login = request.getUserIdentity();
        SSHAgent ssh = getSSH(request.getTargetID());
        if (ssh != null) {
            try {
                LinuxUser user = new LinuxUser(null, login, null, null, null, null, null, null, null, null);

                ssh.executeCommand(user.getUserExpirePasswordCommand());
                responseType.setStatus(StatusCodeType.SUCCESS);
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                ssh.logout();
            }
        }

        return responseType;
    }


    public ResponseType resetPassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        log.debug("Reset password called");

        // TODO
        return null;
    }


    public ResponseType validatePassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        log.debug("Validate password called");

        //TODO
        return null;
    }


    public ResponseType suspend(@WebParam(name = "request", targetNamespace = "") SuspendRequest request) {
        log.debug("Suspend user called");

        ResponseType responseType = new ResponseType();
        responseType.setRequestID(request.getRequestID());
        responseType.setStatus(StatusCodeType.FAILURE);

        String login = request.getUserIdentity();
        SSHAgent ssh = getSSH(request.getTargetID());
        if (ssh != null) {
            try {
                LinuxUser user = new LinuxUser(null, login, null, null, null, null, null, null, null, null);

                ssh.executeCommand(user.getUserLockCommand());
                responseType.setStatus(StatusCodeType.SUCCESS);
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                ssh.logout();
            }
        }

        return responseType;
    }


    public ResponseType resume(@WebParam(name = "request", targetNamespace = "") ResumeRequest request) {
        log.debug("Resume user called");

        ResponseType responseType = new ResponseType();
        responseType.setRequestID(request.getRequestID());
        responseType.setStatus(StatusCodeType.FAILURE);

        String login = request.getUserIdentity();
        SSHAgent ssh = getSSH(request.getTargetID());
        if (ssh != null) {
            try {
                LinuxUser user = new LinuxUser(null, login, null, null, null, null, null, null, null, null);

                ssh.executeCommand(user.getUserUnlockCommand());
                responseType.setStatus(StatusCodeType.SUCCESS);
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                ssh.logout();
            }
        }

        return responseType;
    }


    // >>>>>>>> Accessor/Mutator Methods for bean
    public ManagedSystemWebService getManagedSysService() {
        return managedSysService;
    }

    public void setManagedSysService(ManagedSystemWebService managedSysService) {
        this.managedSysService = managedSysService;
    }

    public ManagedSystemObjectMatchDAO getManagedSysObjectMatchDao() {
        return managedSysObjectMatchDao;
    }

    public void setManagedSysObjectMatchDao(ManagedSystemObjectMatchDAO managedSysObjectMatchDao) {
        this.managedSysObjectMatchDao = managedSysObjectMatchDao;
    }

    public ResourceDataService getResourceDataService() {
        return resourceDataService;
    }

    public void setResourceDataService(ResourceDataService resourceDataService) {
        this.resourceDataService = resourceDataService;
    }
}
