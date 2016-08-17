package org.openiam.idm.srvc.recon.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.base.response.ProvisionUserResponse;
import org.openiam.provision.service.ProvisioningDataService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("removeIdmUserCommand")
public class RemoveIdmUserCommand extends BaseReconciliationUserCommand {
    private static final Log log = LogFactory.getLog(RemoveIdmUserCommand.class);

	@Autowired
	private ProvisioningDataService provisionService;

    public RemoveIdmUserCommand() {
    }

	@Override
	public boolean execute(ReconciliationSituation config, String principal, String mSysID, User user, List<ExtensibleAttribute> attributes) {
		if(log.isDebugEnabled()) {
	        log.debug("Entering RemoveIdmUserCommand");
	        log.debug("Delete user: " + user.getId());
		}
		try {
			ProvisionUser pUser = new ProvisionUser(user);
			pUser.setSrcSystemId(mSysID);
			executeScript(config.getScript(), attributes, pUser);
			ProvisionUserResponse response =  provisionService.deleteByUserId(user.getId(), UserStatusEnum.REMOVE, DEFAULT_REQUESTER_ID);
			return response.isSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
}
