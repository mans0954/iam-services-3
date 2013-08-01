package org.openiam.spml2.spi.example.command.base;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.connector.data.ConnectorConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/20/13
 * Time: 12:11 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractModifyShellCommand<ProvisionObject extends GenericProvisionObject> extends AbstractShellCommand<ModifyRequestType<ProvisionObject>, ModifyResponseType>  {
    @Override
    public ModifyResponseType execute(ModifyRequestType<ProvisionObject> modifyRequestType) throws ConnectorDataException {
        ModifyResponseType respType = new ModifyResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);

        /*
         * PSO - Provisioning Service Object - - ID must uniquely specify an
         * object on the target or in the target's namespace - Try to make the
         * PSO ID immutable so that there is consistency across changes.
         */
        PSOIdentifierType psoID = modifyRequestType.getPsoID();
        String objectId = psoID.getID();

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

        // get the firstName and lastName values

        List<ModificationType> modTypeList = modifyRequestType.getModification();

        try {
            // Runtime.getRuntime().exec(cmdarray); //exec(strBuf.toString());
            Process p = Runtime.getRuntime().exec(getModifyCommand(host, hostlogin, hostpassword, objectId, modTypeList));
            System.out.println("Process =" + p);
            // OutputStream stream = p.getOutputStream();
            // System.out.println( "stream=" + stream.toString() );

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            log.debug("stream reader=" + in.toString());
            in.close();

            return respType;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        }
    }


    protected abstract String getModifyCommand(String host, String hostlogin, String hostpassword, String objectId, List<ModificationType> modTypeList);
}
