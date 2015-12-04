package org.openiam.idm.srvc.recon.command.grp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.service.ObjectProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("updateIdmGroupCommand")
public class UpdateIdmGroupCommand extends BaseReconciliationGroupCommand {
    private static final Log log = LogFactory.getLog(UpdateIdmGroupCommand.class);

    @Autowired
    @Qualifier("groupProvision")
    private ObjectProvisionService<ProvisionGroup> provisionService;

    @Autowired
    @Qualifier("groupWS")
    private GroupDataWebService groupDataService;

    @Autowired
    private ResourceDataService resourceDataService;

    public UpdateIdmGroupCommand() {
    }

	@Override
	public boolean execute(ReconciliationSituation config, String principal, String mSysID, Group group, List<ExtensibleAttribute> attributes) {
        log.debug("Entering UpdateIdmGroupCommand");
		log.debug("Update group: " + principal);

		try {
			ProvisionGroup pGroup = new ProvisionGroup(group);
			pGroup.setSrcSystemId(mSysID);
			executeScript(config.getScript(), attributes, pGroup);

			Set<Resource> resources = pGroup.getResources();
			Response grpResp = groupDataService.saveGroup(pGroup, DEFAULT_REQUESTER_ID);
			String groupId = (String) grpResp.getResponseValue();
			for (Resource res : resources) {
				if (res.getOperation() == AttributeOperationEnum.ADD) {
					resourceDataService.addGroupToResource(res.getId(), groupId, DEFAULT_REQUESTER_ID, null);
				} else if (res.getOperation() == AttributeOperationEnum.DELETE) {
					resourceDataService.removeGroupToResource(res.getId(), groupId, DEFAULT_REQUESTER_ID);
				}
			}
			Response response = provisionService.modify(pGroup);
			return response.isSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return false;
    }
}
