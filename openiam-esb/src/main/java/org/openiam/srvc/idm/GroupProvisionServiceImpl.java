package org.openiam.srvc.idm;


import java.util.List;

import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.base.response.LookupObjectResponse;
import org.openiam.provision.service.*;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("groupProvision")
@WebService(endpointInterface = "org.openiam.srvc.idm.ObjectProvisionService",
        targetNamespace = "http://www.openiam.org/service/provision",
        portName = "GroupProvisionControllerServicePort",
        serviceName = "GroupProvisionService")
public class GroupProvisionServiceImpl extends AbstractBaseService implements ObjectProvisionService<ProvisionGroup> {
    @Autowired
    protected GroupProvisionDataServiceImpl groupProvisionDataService;

    @Override
    public Response add(final ProvisionGroup group) throws Exception {
        return groupProvisionDataService.add(group);
    }

    @Override
    public Response modify(ProvisionGroup group) {
        return groupProvisionDataService.modify(group);
    }

    @Override
    public Response modifyIdentity(IdentityDto identity) {
        return groupProvisionDataService.modifyIdentity(identity);
    }

    @Override
    public Response delete(String managedSystemId, String groupId, UserStatusEnum status, String requesterId) {
        return groupProvisionDataService.delete(managedSystemId, groupId, status, requesterId);
    }

    @Override
    public LookupObjectResponse getTargetSystemObject(final String principalName, final String managedSysId,
                                                        final List<ExtensibleAttribute> extensibleAttributes) {
        return groupProvisionDataService.getTargetSystemObject(principalName, managedSysId, extensibleAttributes);
    }


    @Override
    public Response remove(String groupId, String requesterId) {
        return groupProvisionDataService.remove(groupId, requesterId);
    }

    @Override
    public Response deprovisionSelectedResources(String groupId, String requesterId, List<String> resourceList) {
        return groupProvisionDataService.deprovisionSelectedResources(groupId, requesterId, resourceList);
    }

	@Override
	public Response addResourceToGroup(final ProvisionGroup pGroup, String resourceId) {
        return groupProvisionDataService.addResourceToGroup(pGroup, resourceId);
	}

}
