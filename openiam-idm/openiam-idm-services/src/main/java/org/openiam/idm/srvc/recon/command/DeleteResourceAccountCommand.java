package org.openiam.idm.srvc.recon.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.openiam.idm.srvc.recon.service.ReconciliationCommand;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.provision.service.ProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Pascal
 * Date: 27.04.12
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class DeleteResourceAccountCommand implements ReconciliationCommand {
    private ProvisionService provisionService;
    private static final Log log = LogFactory.getLog(DeleteResourceAccountCommand.class);
    private ManagedSystemWebService managedSysService;
    private ProvisionConnectorWebService connectorService;
    private MuleContext muleContext;
    private String managedSysId;
    private ConnectorAdapter connectorAdapter;

    public DeleteResourceAccountCommand(ProvisionService provisionService,
                                        ManagedSystemWebService managedSysService,
                                        ProvisionConnectorWebService connectorService,
                                        MuleContext muleContext,
                                        String managedSysId,
                                        ConnectorAdapter connectorAdapter) {
        this.provisionService = provisionService;
        this.managedSysService = managedSysService;
        this.connectorService = connectorService;
        this.muleContext = muleContext;
        this.managedSysId = managedSysId;
        this.connectorAdapter = connectorAdapter;
    }

    public boolean execute(Login login, User user, List<ExtensibleAttribute> attributes) {
        log.debug("Entering DeleteResourceAccountCommand");
        if(user == null) {
            ManagedSysDto mSys = managedSysService.getManagedSys(managedSysId);

            log.debug("Calling delete with Remote connector");
            CrudRequest<ExtensibleUser> request = new CrudRequest<ExtensibleUser>();
            request.setObjectIdentity(login.getLogin());
            request.setTargetID(login.getManagedSysId());
            request.setHostLoginId(mSys.getUserId());
            request.setHostLoginPassword(mSys.getDecryptPassword());
            request.setHostUrl(mSys.getHostUrl());
            request.setScriptHandler(mSys.getDeleteHandler());
            log.debug("Calling delete local connector");
            connectorAdapter.deleteRequest(mSys, request, muleContext);

            return true;
        }
        List<Login> principleList = user.getPrincipalList();
        for(Login l : principleList){
            if(l.getLoginId().equals(login.getLoginId())){
                l.setOperation(AttributeOperationEnum.DELETE);
                break;
            }
        }

        ProvisionUser pUser = new ProvisionUser(user);
        pUser.setPrincipalList(principleList);

        provisionService.modifyUser(pUser);
        return false;
    }
}
