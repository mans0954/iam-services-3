package org.openiam.connector.shell.command.base;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.orcl.command.base.AbstractOracleCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.provision.type.ExtensibleObject;

import java.io.OutputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 8/8/13
 * Time: 2:48 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract  class AbstractCrudShellCommand<ExtObject extends ExtensibleObject> extends AbstractShellCommand<CrudRequest<ExtObject>, ObjectResponse> {
    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> crudRequest) throws ConnectorDataException {
        ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);


        // powershell.exe -command "&
        // C:\appserver\apache-tomcat-6.0.26\powershell\create.ps1 displayName
        // principalName firstName middleInit lastName password"

        /*
         * ContainerID - May specify the container in which this object should
         * be created ie. ou=Development, org=Example
         */

        /*
         * PSO - Provisioning Service Object - - ID must uniquely specify an
         * object on the target or in the target's namespace - Try to make the
         * PSO ID immutable so that there is consistency across changes.
         */
        String objectId = crudRequest.getObjectIdentity();

        /* targetID - */
        String targetID = crudRequest.getTargetID();

        ConnectorConfiguration configuration = this.getConfiguration(targetID, ConnectorConfiguration.class);
        /*
         * A) Use the targetID to look up the connection information under
         * managed systems
         */

        String host = configuration.getManagedSys().getHostUrl();
        String hostlogin = configuration.getManagedSys().getUserId();
        String hostpassword = getDecryptedPassword(configuration.getManagedSys().getUserId(), configuration.getManagedSys().getPswd());



        ExtObject object = crudRequest.getExtensibleObject();

        String cmd = getCommand(host, hostlogin, hostpassword, objectId, object);

        // System.out.println("Command line string= " + strBuf.toString());
        try {
            // Runtime.getRuntime().exec(cmdarray); //exec(strBuf.toString());
            Process p = Runtime.getRuntime().exec(cmd);
            System.out.println("Process =" + p);
            OutputStream stream = p.getOutputStream();
            // System.out.println( "stream=" + stream.toString() );
            return response;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        }
    }

    protected abstract String getCommand(String host, String hostlogin, String hostpassword, String objectId, ExtObject object);
}
