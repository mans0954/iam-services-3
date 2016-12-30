package org.openiam.idm.srvc.recon.command;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.ProvisionService;
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
    @Qualifier("defaultProvision")
    private ProvisionService provisionService;

    @Autowired
    @Qualifier("userWS")
    UserDataWebService userWS;

    public UpdateIdmUserCommand() {
    }

    @Override
    public boolean execute(ReconciliationSituation config, String principal, String mSysID, User user, List<ExtensibleAttribute> attributes) {
        boolean updated = false;
        if (log.isDebugEnabled()) {
            log.debug("Entering UpdateIdmUserCommand");
            log.debug("Update user: " + user.getId());
        }
        try {
            ProvisionUser pUser = new ProvisionUser(user);
            setCurrentSuperiors(pUser);
            pUser.setSrcSystemId(mSysID);
            // SIA: It's necessary to validate result of script execution
            int retval = executeScript(config.getScript(), attributes, pUser);
            if (retval == 0) {
                ProvisionUserResponse response = provisionService.modifyUser(pUser);
                updated = response.isSuccess();
            } else {
                log.debug("Couldn't populate ProvisionUser. User not modified");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception: " + e.getMessage());
        } finally {
            log.debug("User updated?" + updated);
            return updated;
        }
    }

    private void setCurrentSuperiors(ProvisionUser pUser) {
        if (StringUtils.isNotEmpty(pUser.getId())) {
            List<User> superiors = userWS.getSuperiors(pUser.getId(), -1, -1);
            if (CollectionUtils.isNotEmpty(superiors)) {
                pUser.setSuperiors(new HashSet<>(superiors));
            }
        }
    }

}
