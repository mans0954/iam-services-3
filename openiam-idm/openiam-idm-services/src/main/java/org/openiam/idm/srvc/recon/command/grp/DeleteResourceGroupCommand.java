package org.openiam.idm.srvc.recon.command.grp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.provision.request.CrudRequest;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.provision.service.ObjectProvisionDataService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("deleteResourceGroupCommand")
public class DeleteResourceGroupCommand extends BaseReconciliationGroupCommand {
    private static final Log log = LogFactory.getLog(DeleteResourceGroupCommand.class);

    @Autowired
    @Qualifier("groupProvisionDataService")
    private ObjectProvisionDataService<ProvisionGroup> provisionService;

    @Autowired
    protected ManagedSystemService managedSystemService;

    @Autowired
    private ConnectorAdapter connectorAdapter;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    public DeleteResourceGroupCommand() {
    }

    @Override
    public boolean execute(ReconciliationSituation config, String principal, String mSysID, Group group, List<ExtensibleAttribute> attributes) {
    	if(log.isDebugEnabled()) {
	        log.debug("Entering DeleteResourceGroupCommand");
	        log.debug("Do delete for Group: " + principal);
    	}
		if(group == null) {
            ManagedSysDto mSys = managedSystemService.getManagedSys(mSysID);
            if(log.isDebugEnabled()) {
            	log.debug("Calling delete with Remote connector");
            }
            CrudRequest<ExtensibleUser> request = new CrudRequest<ExtensibleUser>();
            request.setObjectIdentity(principal);
            request.setTargetID(mSysID);
            request.setHostLoginId(mSys.getUserId());
            request.setHostLoginPassword(mSys.getDecryptPassword());
            request.setHostUrl(mSys.getHostUrl());
            request.setScriptHandler(mSys.getDeleteHandler());
            if(log.isDebugEnabled()) {
            	log.debug("Calling delete local connector");
            }
            connectorAdapter.deleteRequest(mSys, request);

            return true;
        }

		try {
			ProvisionGroup pGroup = new ProvisionGroup(group);
			pGroup.setSrcSystemId(mSysID);
			executeScript(config.getScript(), attributes, pGroup);
			//Reset source system flag from User to avoid ignoring Provisioning for this resource
			pGroup.setSrcSystemId(null);
			Response response = provisionService.delete(mSysID, pGroup.getId(), UserStatusEnum.DELETED, DEFAULT_REQUESTER_ID);
			return response.isSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }

}
