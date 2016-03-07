package org.openiam.idm.srvc.recon.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
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

@Component("disableIdmAccountUserCommand")
public class DisableIdmAccountUserCommand extends BaseReconciliationUserCommand {
    private static final Log log = LogFactory.getLog(DisableIdmAccountUserCommand.class);

    @Autowired
    @Qualifier("defaultProvision")
    private ProvisionService provisionService;

    public DisableIdmAccountUserCommand(){
    }

	@Override
	public boolean execute(ReconciliationSituation config, String principal, String mSysID, User user, List<ExtensibleAttribute> attributes) {
		if(log.isDebugEnabled()) {
			log.debug("Entering DisableIdmAccountUserCommand");
			log.debug("Disable account for user: " + user.getId());
		}
		List<Login> principleList = user.getPrincipalList();
        for(Login l : principleList){
            if(l.getManagedSysId().equals(mSysID)){
                l.setStatus(LoginStatusEnum.INACTIVE);
                break;
            }
        }

		try {
			ProvisionUser pUser = new ProvisionUser(user);
			pUser.setPrincipalList(principleList);
			pUser.setSrcSystemId(mSysID);
			executeScript(config.getScript(), attributes, pUser);
			ProvisionUserResponse response = provisionService.modifyUser(pUser);
			return response.isSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
}
