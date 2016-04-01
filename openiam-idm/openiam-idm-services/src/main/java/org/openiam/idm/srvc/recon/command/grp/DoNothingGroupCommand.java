package org.openiam.idm.srvc.recon.command.grp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("doNothingGroupCommand")
public class DoNothingGroupCommand extends BaseReconciliationGroupCommand {
    private static final Log log = LogFactory.getLog(DoNothingGroupCommand.class);

    public DoNothingGroupCommand() {
    }

    @Override
    public boolean execute(ReconciliationSituation config, String principal, String mSysID, Group group, List<ExtensibleAttribute> attributes) {
    	if(log.isDebugEnabled()) {
	        log.debug("Entering DoNothingCommand");
	        log.debug("Do nothing for Group: " + principal);
    	}
		try {
			ProvisionGroup pGroup = new ProvisionGroup(group);
			pGroup.setSrcSystemId(mSysID);
			executeScript(config.getScript(), attributes, pGroup);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return true;
    }
}
