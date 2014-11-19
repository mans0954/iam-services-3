package org.openiam.idm.srvc.recon.command.grp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.service.ObjectProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("removeIdmGroupCommand")
public class RemoveIdmGroupCommand extends BaseReconciliationGroupCommand {
    private static final Log log = LogFactory.getLog(RemoveIdmGroupCommand.class);

    @Autowired
    @Qualifier("groupProvision")
    private ObjectProvisionService<ProvisionGroup> provisionService;

    public RemoveIdmGroupCommand() {
    }

	@Override
	public boolean execute(ReconciliationSituation config, String principal, String mSysID, Group group, List<ExtensibleAttribute> attributes) {
        log.debug("Entering RemoveIdmGroupCommand");
        log.debug("Remove  group: " + principal);

		try {
			ProvisionGroup pGroup = new ProvisionGroup(group);
			pGroup.setSrcSystemId(mSysID);
			executeScript(config.getScript(), attributes, pGroup);
			Response response =  provisionService.delete(mSysID, group.getId(), UserStatusEnum.REMOVE, DEFAULT_REQUESTER_ID);
			return response.isSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
}
