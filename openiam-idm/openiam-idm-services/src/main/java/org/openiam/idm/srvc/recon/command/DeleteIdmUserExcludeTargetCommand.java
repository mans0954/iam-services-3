package org.openiam.idm.srvc.recon.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.ProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("deleteIdmUserExcludeTargetCommand")
public class DeleteIdmUserExcludeTargetCommand extends BaseReconciliationUserCommand {
    private static final Log log = LogFactory.getLog(DeleteIdmUserExcludeTargetCommand.class);

    @Autowired
    @Qualifier("defaultProvision")
    private ProvisionService provisionService;

    public DeleteIdmUserExcludeTargetCommand(){
    }

	@Override
	public boolean execute(ReconciliationSituation config, String principal, String mSysID, User user, List<ExtensibleAttribute> attributes) {
        log.debug("Entering DeleteIdmUserExcludeTargetCommand");
        log.debug("Delete user: " + user.getId());
		try {
			ProvisionUser pUser = new ProvisionUser(user);
			pUser.setSrcSystemId(mSysID);
			executeScript(config.getScript(), attributes, pUser);
			ProvisionUserResponse response = provisionService.deleteByUserId(user.getId(), UserStatusEnum.DELETED, DEFAULT_REQUESTER_ID);
			return response.isSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
}
