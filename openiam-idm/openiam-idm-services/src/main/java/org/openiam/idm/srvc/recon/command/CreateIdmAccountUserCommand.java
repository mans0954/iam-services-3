package org.openiam.idm.srvc.recon.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Pascal
 * Date: 27.04.12
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
@Component("createIdmAccountUserCommand")
public class CreateIdmAccountUserCommand extends BaseReconciliationUserCommand {
    private static final Log log = LogFactory.getLog(CreateIdmAccountUserCommand.class);

    public static final String OPENIAM_MANAGED_SYS_ID = "0";

    @Autowired
    @Qualifier("defaultProvision")
    private ProvisionService provisionService;

    public CreateIdmAccountUserCommand() {
    }

	@Override
    public boolean execute(ReconciliationSituation config, String principal, String mSysID, User user, List<ExtensibleAttribute> attributes)  {
		if(log.isDebugEnabled()) {
	        log.debug("Entering CreateIdmAccountCommand");
			log.debug("Create account for principal: " + principal);
		}
		if(attributes == null){
			if(log.isDebugEnabled()) {
				log.debug("Can't create IDM user without attributes");
			}
        } else {
			try {
				ProvisionUser pUser = new ProvisionUser(user);
				pUser.setSrcSystemId(mSysID);
				int retval = executeScript(config.getScript(), attributes, pUser);
                if (retval == 0) {
                    Login idmLogin = null;
                    for (Login pr : user.getPrincipalList()) {
                        if (OPENIAM_MANAGED_SYS_ID.equalsIgnoreCase(pr.getManagedSysId())) {
                            idmLogin = pr;
                        }
                    }
                    if (idmLogin == null) {
                        idmLogin = new Login();
                        idmLogin.setOperation(AttributeOperationEnum.ADD);
                        idmLogin.setLogin(principal);
                        idmLogin.setManagedSysId(OPENIAM_MANAGED_SYS_ID);
                        pUser.getPrincipalList().add(idmLogin);
                    }
                    provisionService.addUser(pUser);
					return true;
				} else {
					if(log.isDebugEnabled()) {
						log.debug("Couldn't populate ProvisionUser. User not added");
					}
				}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
