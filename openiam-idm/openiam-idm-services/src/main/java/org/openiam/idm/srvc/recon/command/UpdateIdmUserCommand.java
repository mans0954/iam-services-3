package org.openiam.idm.srvc.recon.command;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.base.response.ProvisionUserResponse;
import org.openiam.provision.service.ProvisioningDataService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component("updateIdmUserCommand")
public class UpdateIdmUserCommand extends BaseReconciliationUserCommand {
    private static final Log log = LogFactory.getLog(UpdateIdmUserCommand.class);

	@Autowired
	private ProvisioningDataService provisionService;

	@Autowired
	@Qualifier("userManager")
	protected UserDataService userManager;

	public UpdateIdmUserCommand(){
    }

	@Override
	public boolean execute(ReconciliationSituation config, String principal, String mSysID, User user, List<ExtensibleAttribute> attributes) {
		if(log.isDebugEnabled()) {
			log.debug("Entering UpdateIdmUserCommand");
			log.debug("Update user: " + user.getId());
		}
		try {
			ProvisionUser pUser = new ProvisionUser(user);
			setCurrentSuperiors(pUser);
			pUser.setSrcSystemId(mSysID);
			executeScript(config.getScript(), attributes, pUser);
			ProvisionUserResponse response = provisionService.modifyUser(pUser);
			return response.isSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return false;
    }

	private void setCurrentSuperiors(ProvisionUser pUser) {
		if (StringUtils.isNotEmpty(pUser.getId())) {
			List<User> superiors = userManager.getSuperiorsDto(pUser.getId(), -1, -1);
			if (CollectionUtils.isNotEmpty(superiors)) {
				pUser.setSuperiors(new HashSet<>(superiors));
			}
		}
	}

}
