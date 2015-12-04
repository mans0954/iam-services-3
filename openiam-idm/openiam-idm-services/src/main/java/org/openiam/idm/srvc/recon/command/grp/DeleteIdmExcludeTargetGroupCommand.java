package org.openiam.idm.srvc.recon.command.grp;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.BaseAttribute;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.auth.login.IdentityService;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.PopulationScript;
import org.openiam.idm.srvc.recon.service.ReconciliationObjectCommand;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("deleteIdmExcludeTargetGroupCommand")
public class DeleteIdmExcludeTargetGroupCommand extends BaseReconciliationGroupCommand {
    private static final Log log = LogFactory.getLog(DeleteIdmExcludeTargetGroupCommand.class);

    @Autowired
    @Qualifier("groupWS")
    private GroupDataWebService groupDataWebService;

    @Autowired
    private IdentityService identityService;

    public DeleteIdmExcludeTargetGroupCommand() {
    }

	@Override
	public boolean execute(ReconciliationSituation config, String principal, String mSysID, Group group, List<ExtensibleAttribute> attributes) {
        log.debug("Entering DeleteIdmExcludeTargetGroupCommand");
        log.debug("Delete  group from IDM only :" + principal);
		try {
			ProvisionGroup pGroup = new ProvisionGroup(group);
			pGroup.setSrcSystemId(mSysID);
			executeScript(config.getScript(), attributes, pGroup);

			IdentityDto identityDto = identityService.getIdentityByManagedSys(group.getId(), mSysID);
			identityService.deleteIdentity(identityDto.getId());

			Response response = groupDataWebService.deleteGroup(group.getId(), DEFAULT_REQUESTER_ID);
			return response.isSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
}
