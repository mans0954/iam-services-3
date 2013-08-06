package org.openiam.spml2.spi.example.command.base;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.connector.common.data.ConnectorConfiguration;

import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/19/13
 * Time: 11:59 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDeleteShellCommand<ProvisionObject extends GenericProvisionObject> extends AbstractShellCommand<DeleteRequestType<ProvisionObject>, ResponseType> {
    @Override
    public ResponseType execute(DeleteRequestType<ProvisionObject> deleteRequestType) throws ConnectorDataException {

        ResponseType respType = new ResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);
        /*
         * PSO - Provisioning Service Object - - ID must uniquely specify an
         * object on the target or in the target's namespace - Try to make the
         * PSO ID immutable so that there is consistency across changes.
         */
        PSOIdentifierType psoID = deleteRequestType.getPsoID();
        String objectId = psoID.getID();
        /* targetID - */
        String targetID = psoID.getTargetID();

        ConnectorConfiguration configuration = this.getConfiguration(targetID, ConnectorConfiguration.class);

        String host = configuration.getManagedSys().getHostUrl();
        String hostlogin = configuration.getManagedSys().getUserId();
        String hostpassword = getDecryptedPassword(configuration.getManagedSys().getUserId(), configuration.getManagedSys().getPswd());

        // powershell.exe -command "&
        // C:\appserver\apache-tomcat-6.0.26\powershell\delete.ps1
        // principalName"
        try {
            // Runtime.getRuntime().exec(cmdarray); //exec(strBuf.toString());
            Process p = Runtime.getRuntime().exec(getDeleteCommand(host, hostlogin, hostpassword, objectId));
            log.debug("Process =" + p);
            OutputStream stream = p.getOutputStream();
            log.debug("stream=" + stream.toString());

            return respType;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        }
    }

    protected abstract String getDeleteCommand(String host, String hostlogin, String hostpassword, String objectId);
}
