package org.openiam.idm.srvc.recon.command;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttribute;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.PopulationScript;
import org.openiam.idm.srvc.recon.service.ReconciliationObjectCommand;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.provision.service.ProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("deleteResourceAccountUserCommand")
public class DeleteResourceAccountUserCommand extends BaseReconciliationUserCommand {

    private static final Log log = LogFactory.getLog(DeleteResourceAccountUserCommand.class);

    @Autowired
    @Qualifier("managedSysService")
    private ManagedSystemWebService managedSysService;

    @Autowired
    private ConnectorAdapter connectorAdapter;

    @Autowired
    @Qualifier("defaultProvision")
    private ProvisionService provisionService;

    public DeleteResourceAccountUserCommand(){
    }

	@Override
	public boolean execute(ReconciliationSituation config, String principal, String mSysID, User user, List<ExtensibleAttribute> attributes) {
		if(log.isDebugEnabled()) {
	        log.debug("Entering DeleteResourceAccountCommand");
			log.debug("Delete Resource for principal: " + principal);
		}
		if(user == null) {
            ManagedSysDto mSys = managedSysService.getManagedSys(mSysID);

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
