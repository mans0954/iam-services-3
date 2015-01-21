package org.openiam.idm.srvc.recon.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.ProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("createResourceAccountUserCommand")
public class CreateResourceAccountUserCommand extends BaseReconciliationUserCommand {
    private static final Log log = LogFactory.getLog(CreateResourceAccountUserCommand.class);

    @Autowired
    @Qualifier("defaultProvision")
    private ProvisionService provisionService;

    public CreateResourceAccountUserCommand(){
    }

	@Override
	public boolean execute(ReconciliationSituation config, String principal, String mSysID, User user, List<ExtensibleAttribute> attributes) {
        log.debug("Entering CreateResourceAccountCommand");
        log.debug("Create Resource Account for user: " + user.getId());
		try {
			ProvisionUser pUser = new ProvisionUser(user);
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
