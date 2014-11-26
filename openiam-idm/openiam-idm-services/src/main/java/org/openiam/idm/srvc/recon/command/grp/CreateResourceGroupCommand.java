package org.openiam.idm.srvc.recon.command.grp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.service.ObjectProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("createResourceGroupCommand")
public class CreateResourceGroupCommand extends BaseReconciliationGroupCommand {

    private static final Log log = LogFactory.getLog(CreateResourceGroupCommand.class);

    @Autowired
    @Qualifier("groupProvision")
    private ObjectProvisionService<ProvisionGroup> provisionService;

    public CreateResourceGroupCommand() {
    }

	@Override
	public boolean execute(ReconciliationSituation config, String principal, String mSysId, Group group, List<ExtensibleAttribute> attributes) {
        log.debug("Entering CreateResourceGroupCommand");
        log.debug("Create Resource Account for group: " + group.getId());

		try {
			ProvisionGroup pGroup = new ProvisionGroup(group);
			pGroup.setSrcSystemId(mSysId);
			executeScript(config.getScript(), attributes, pGroup);
			//Reset source system flag from User to avoid ignoring Provisioning for this resource
			pGroup.setSrcSystemId(null);
			Response response = provisionService.modify(pGroup);
			return response.isSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }

}
