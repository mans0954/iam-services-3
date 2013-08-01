package org.openiam.spml2.spi.example.command.user;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.spml2.msg.password.SetPasswordRequestType;
import org.openiam.connector.data.ConnectorConfiguration;
import org.openiam.spml2.spi.example.command.base.AbstractShellCommand;
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
public class SetPasswordShellCommand extends AbstractShellCommand<SetPasswordRequestType, ResponseType> {
    @Override
    public ResponseType execute(SetPasswordRequestType setPasswordRequestType) throws ConnectorDataException {
        ResponseType respType = new ResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);

        /*
         * PSO - Provisioning Service Object - - ID must uniquely specify an
         * object on the target or in the target's namespace - Try to make the
         * PSO ID immutable so that there is consistency across changes.
         */
        PSOIdentifierType psoID = setPasswordRequestType.getPsoID();
        String userName = psoID.getID();
        /* targetID - */
        String targetID = psoID.getTargetID();

        /*
         * A) Use the targetID to look up the connection information under
         * managed systems
         */
        ConnectorConfiguration configuration = this.getConfiguration(targetID, ConnectorConfiguration.class);

        String host = configuration.getManagedSys().getHostUrl();
        String hostlogin = configuration.getManagedSys().getUserId();
        String hostpassword = getDecryptedPassword(configuration.getManagedSys().getUserId(), configuration.getManagedSys().getPswd());

        StringBuffer strBuf = new StringBuffer();

        strBuf.append("cmd /c powershell.exe -command \"& C:\\powershell\\ad\\SetPassword-UserActiveDir.ps1 ");
        strBuf.append("'" + host + "' ");
        strBuf.append("'" + hostlogin + "' ");
        strBuf.append("'" + hostpassword + "' ");
        strBuf.append("'" + userName + "' ");
        strBuf.append("'" + setPasswordRequestType.getPassword() + "' \" ");

        log.debug("Command line string= " + strBuf.toString());
        String[] cmdarray = { "cmd", strBuf.toString() };
        try {
            // Runtime.getRuntime().exec(cmdarray); //exec(strBuf.toString());
            Process p = Runtime.getRuntime().exec(strBuf.toString());
            log.debug("Process =" + p);
            OutputStream stream = p.getOutputStream();
            log.debug("stream=" + stream.toString());
            return respType;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        }
    }
}
