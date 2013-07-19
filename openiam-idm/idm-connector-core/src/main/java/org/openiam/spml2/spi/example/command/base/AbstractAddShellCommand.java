package org.openiam.spml2.spi.example.command.base;

import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.common.data.ConnectorConfiguration;

import javax.xml.namespace.QName;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/19/13
 * Time: 7:28 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractAddShellCommand<ProvisionObject extends GenericProvisionObject> extends AbstractShellCommand<AddRequestType<ProvisionObject>, AddResponseType> {
    @Override
    public AddResponseType execute(AddRequestType<ProvisionObject> addRequestType) throws ConnectorDataException {
        AddResponseType response = new AddResponseType();
        response.setStatus(StatusCodeType.SUCCESS);


        // powershell.exe -command "&
        // C:\appserver\apache-tomcat-6.0.26\powershell\create.ps1 displayName
        // principalName firstName middleInit lastName password"

        /*
         * ContainerID - May specify the container in which this object should
         * be created ie. ou=Development, org=Example
         */
        PSOIdentifierType containerID = addRequestType.getContainerID();
        System.out.println("ContainerId =" + containerID);

        /*
         * PSO - Provisioning Service Object - - ID must uniquely specify an
         * object on the target or in the target's namespace - Try to make the
         * PSO ID immutable so that there is consistency across changes.
         */
        PSOIdentifierType psoID = addRequestType.getPsoID();
        String objectId = psoID.getID();

        System.out.println("PSOId=" + psoID.getID());

        /* targetID - */
        String targetID = addRequestType.getTargetID();

        ConnectorConfiguration configuration = this.getConfiguration(targetID, ConnectorConfiguration.class);
        /*
         * A) Use the targetID to look up the connection information under
         * managed systems
         */

        String host = configuration.getManagedSys().getHostUrl();
        String hostlogin = configuration.getManagedSys().getUserId();
        String hostpassword = getDecryptedPassword(configuration.getManagedSys().getUserId(), configuration.getManagedSys().getPswd());



        List<ExtensibleObject> requestAttributeList = addRequestType.getData().getAny();

        String cmd = getAddCommand(host, hostlogin, hostpassword, objectId, requestAttributeList);

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

    protected abstract String getAddCommand(String host, String hostlogin, String hostpassword, String objectId, List<ExtensibleObject> requestAttributeList);
}
