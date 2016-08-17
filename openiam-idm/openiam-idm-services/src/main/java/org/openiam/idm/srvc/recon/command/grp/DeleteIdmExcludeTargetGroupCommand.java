package org.openiam.idm.srvc.recon.command.grp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.auth.login.IdentityService;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("deleteIdmExcludeTargetGroupCommand")
public class DeleteIdmExcludeTargetGroupCommand extends BaseReconciliationGroupCommand {
    private static final Log log = LogFactory.getLog(DeleteIdmExcludeTargetGroupCommand.class);

    @Autowired
    private GroupDataService groupDataWebService;

    @Autowired
    private IdentityService identityService;

    public DeleteIdmExcludeTargetGroupCommand() {
    }

	@Override
	public boolean execute(ReconciliationSituation config, String principal, String mSysID, Group group, List<ExtensibleAttribute> attributes) {
		if(log.isDebugEnabled()) {
	        log.debug("Entering DeleteIdmExcludeTargetGroupCommand");
	        log.debug("Delete  group from IDM only :" + principal);
		}
		try {
			ProvisionGroup pGroup = new ProvisionGroup(group);
			pGroup.setSrcSystemId(mSysID);
			executeScript(config.getScript(), attributes, pGroup);

			IdentityDto identityDto = identityService.getIdentityByManagedSys(group.getId(), mSysID);
			identityService.deleteIdentity(identityDto.getId());

			groupDataWebService.deleteGroup(group.getId());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
}
