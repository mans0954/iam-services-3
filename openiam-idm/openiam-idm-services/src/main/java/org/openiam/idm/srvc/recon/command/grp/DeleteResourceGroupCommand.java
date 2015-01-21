package org.openiam.idm.srvc.recon.command.grp;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.BaseAttribute;
import org.openiam.base.ws.Response;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.PopulationScript;
import org.openiam.idm.srvc.recon.service.ReconciliationObjectCommand;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.provision.service.ObjectProvisionService;
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

@Component("deleteResourceGroupCommand")
public class DeleteResourceGroupCommand extends BaseReconciliationGroupCommand {
    private static final Log log = LogFactory.getLog(DeleteResourceGroupCommand.class);

    @Autowired
    @Qualifier("groupProvision")
    private ObjectProvisionService<ProvisionGroup> provisionService;

    @Autowired
    private ManagedSystemWebService managedSysService;

    @Autowired
    private ConnectorAdapter connectorAdapter;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    public DeleteResourceGroupCommand() {
    }

    @Override
    public boolean execute(ReconciliationSituation config, String principal, String mSysID, Group group, List<ExtensibleAttribute> attributes) {
        log.debug("Entering DeleteResourceGroupCommand");
        log.debug("Do delete for Group: " + principal);

		if(group == null) {
            ManagedSysDto mSys = managedSysService.getManagedSys(mSysID);

            log.debug("Calling delete with Remote connector");
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
