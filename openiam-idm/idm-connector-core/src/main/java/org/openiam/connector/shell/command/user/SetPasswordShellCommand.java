package org.openiam.connector.shell.command.user;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.shell.command.base.AbstractShellCommand;
import org.springframework.stereotype.Service;

import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/20/13
 * Time: 12:20 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("setPasswordShellCommand")
public class SetPasswordShellCommand extends AbstractShellCommand<PasswordRequest, ResponseType> {
    @Override
    public ResponseType execute(PasswordRequest passwordRequest) throws ConnectorDataException {
        ResponseType respType = new ResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);

        /*
         * PSO - Provisioning Service Object - - ID must uniquely specify an
         * object on the target or in the target's namespace - Try to make the
         * PSO ID immutable so that there is consistency across changes.
         */
        String userName = passwordRequest.getObjectIdentity();
        /* targetID - */
        String targetID = passwordRequest.getTargetID();

        /*
         * A) Use the targetID to look up the connection information under
         * managed systems
         */
        ConnectorConfiguration configuration = this.getConfiguration(targetID, ConnectorConfiguration.class);

        String host = configuration.getManagedSys().getHostUrl();
        String hostlogin = configuration.getManagedSys().getUserId();
        String hostpassword = getDecryptedPassword(configuration.getManagedSys().getPswd());

        StringBuffer strBuf = new StringBuffer();

        strBuf.append("cmd /c powershell.exe -command \"& C:\\powershell\\ad\\SetPassword-UserActiveDir.ps1 ");
        strBuf.append("'" + host + "' ");
        strBuf.append("'" + hostlogin + "' ");
        strBuf.append("'" + hostpassword + "' ");
        strBuf.append("'" + userName + "' ");
        strBuf.append("'" + passwordRequest.getPassword() + "' \" ");

        if(log.isDebugEnabled()) {
        	log.debug("Command line string= " + strBuf.toString());
        }
        String[] cmdarray = { "cmd", strBuf.toString() };
        try {
            // Runtime.getRuntime().exec(cmdarray); //exec(strBuf.toString());
            Process p = Runtime.getRuntime().exec(strBuf.toString());
            if(log.isDebugEnabled()) {
            	log.debug("Process =" + p);
            }
            OutputStream stream = p.getOutputStream();
            if(log.isDebugEnabled()) {
            	log.debug("stream=" + stream.toString());
            }
            return respType;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        }
    }
}
