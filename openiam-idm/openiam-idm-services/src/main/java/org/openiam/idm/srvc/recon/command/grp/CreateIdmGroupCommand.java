package org.openiam.idm.srvc.recon.command.grp;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.IdentityService;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.service.ObjectProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("createIdmGroupCommand")
public class CreateIdmGroupCommand  extends BaseReconciliationGroupCommand {
    private static final Log log = LogFactory.getLog(CreateIdmGroupCommand.class);

    @Autowired
    @Qualifier("groupProvision")
    private ObjectProvisionService<ProvisionGroup> provisionService;

    @Autowired
    @Qualifier("groupWS")
    private GroupDataWebService groupDataWebService;

    @Autowired
    @Qualifier("identityManager")
    private IdentityService identityService;

    @Autowired
    @Qualifier("userManager")
    private UserDataService userManager;

    public CreateIdmGroupCommand() {
    }

	@Override
	public boolean execute(ReconciliationSituation config, String principal, String mSysID, Group group, List<ExtensibleAttribute> attributes) {
        log.debug("Entering CreateIdmGroupCommand");
        if(attributes == null){
            log.debug("Can't create IDM group without attributes");
        } else {
            try {
				ProvisionGroup pGroup = new ProvisionGroup(group);
				pGroup.setSrcSystemId(mSysID);
				int retval = executeScript(config.getScript(), attributes, pGroup);
                if(retval == 0) {
                    Response saveGroupResponse = groupDataWebService.saveGroup(pGroup, DEFAULT_REQUESTER_ID);
                    String groupId = (String)saveGroupResponse.getResponseValue();
                    IdentityDto identity = new IdentityDto();
                    identity.setIdentity(principal);
                    identity.setType(IdentityTypeEnum.GROUP);
                    identity.setManagedSysId(mSysID);
                    identity.setOperation(AttributeOperationEnum.ADD);
                    identity.setStatus(LoginStatusEnum.ACTIVE);
                    identity.setReferredObjectId(groupId);
                    identityService.save(identity);
                    provisionService.add(pGroup);
                    if (CollectionUtils.isNotEmpty(pGroup.getMembersIds())) {
                        for(String memberPrincipal : pGroup.getMembersIds()) {
                            UserEntity user = userManager.getUserByPrincipal(memberPrincipal, mSysID, false);
                            if(user != null) {
                                Response response = groupDataWebService.addUserToGroup(groupId, user.getId(), DEFAULT_REQUESTER_ID, null);
                                log.debug("User Member with principal = "+memberPrincipal+" was added to Group = "+identity.getIdentity() + " Managed Sys = "+identity.getManagedSysId() + ". \nResponse = "+response);
                            }
                        }
                    }
                }else{
                    log.debug("Couldn't populate ProvisionGroup. Group not added");
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
