package org.openiam.idm.srvc.recon.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.provision.request.CrudRequest;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.base.response.ProvisionUserResponse;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.provision.service.ProvisioningDataService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("deleteResourceAccountUserCommand")
public class DeleteResourceAccountUserCommand extends BaseReconciliationUserCommand {

    private static final Log log = LogFactory.getLog(DeleteResourceAccountUserCommand.class);

    @Autowired
    protected ManagedSystemService managedSystemService;

    @Autowired
    private ConnectorAdapter connectorAdapter;

    @Autowired
    private ProvisioningDataService provisionService;

    public DeleteResourceAccountUserCommand(){
    }

	@Override
	public boolean execute(ReconciliationSituation config, String principal, String mSysID, User user, List<ExtensibleAttribute> attributes) {
		if(log.isDebugEnabled()) {
	        log.debug("Entering DeleteResourceAccountCommand");
			log.debug("Delete Resource for principal: " + principal);
		}
		if(user == null) {
            ManagedSysDto mSys = managedSystemService.getManagedSys(mSysID);

            CrudRequest<ExtensibleUser> request = new CrudRequest<ExtensibleUser>();
            request.setObjectIdentity(principal);
            request.setTargetID(mSysID);
            request.setHostLoginId(mSys.getUserId());
            request.setHostLoginPassword(mSys.getDecryptPassword());
            request.setHostUrl(mSys.getHostUrl());
            request.setScriptHandler(mSys.getDeleteHandler());
            log.debug("Calling delete local connector");
            connectorAdapter.deleteRequest(mSys, request);

            return true;
        }
        List<Login> principleList = user.getPrincipalList();
        for(Login l : principleList){
            if(l.getManagedSysId().equals(mSysID)){
                l.setOperation(AttributeOperationEnum.DELETE);
                break;
            }
        }

		try {
			ProvisionUser pUser = new ProvisionUser(user);
			pUser.setPrincipalList(principleList);
			pUser.setSrcSystemId(mSysID);
			executeScript(config.getScript(), attributes, pUser);
			//Reset source system flag from User to avoid ignoring Provisioning for this resource
			pUser.setSrcSystemId(null);
			ProvisionUserResponse response = provisionService.modifyUser(pUser);
			return response.isSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
}
