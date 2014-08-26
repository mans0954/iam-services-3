package org.openiam.idm.srvc.recon.command.grp;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttribute;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.IdentityService;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.PopulationScript;
import org.openiam.idm.srvc.recon.service.ReconciliationObjectCommand;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.service.ObjectProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("createIdmGroupCommand")
public class CreateIdmGroupCommand  implements ReconciliationObjectCommand<Group> {
    private static final Log log = LogFactory.getLog(CreateIdmGroupCommand.class);

    @Autowired
    @Qualifier("groupProvision")
    private ObjectProvisionService<ProvisionGroup> provisionService;

    @Autowired
    private GroupDataWebService groupDataWebService;

    @Autowired
    @Qualifier("identityManager")
    private IdentityService identityService;

    @Autowired
    @Qualifier("userManager")
    private UserDataService userManager;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    public CreateIdmGroupCommand() {
    }

    public boolean execute(ReconciliationSituation config, String principal, String mSysID, Group group, List<ExtensibleAttribute> attributes) {
        log.debug("Entering CreateIdmGroupCommand");
        if(attributes == null){
            log.debug("Can't create IDM group without attributes");
        } else {
            Map<String, String> line = new HashMap<String, String>();
            for (ExtensibleAttribute attr : attributes) {
                if (attr.getValue() != null) {
                    line.put(attr.getName(), attr.getValue());
                } else if (attr.getAttributeContainer() != null &&
                        CollectionUtils.isNotEmpty(attr.getAttributeContainer().getAttributeList()) &&
                        line.get(attr.getName()) == null) {
                    StringBuilder value = new StringBuilder();
                    boolean isFirst = true;
                    for (BaseAttribute ba : attr.getAttributeContainer().getAttributeList()) {
                        if (!isFirst) {
                            value.append('^');
                        } else {
                            isFirst = false;
                        }
                        value.append(ba.getValue());
                    }
                    line.put(attr.getName(), value.toString());
                }
            }
            try {
                Map<String, Object> bindingMap = new HashMap<String, Object>();
                bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, mSysID);
                PopulationScript<ProvisionGroup> script = (PopulationScript<ProvisionGroup>)scriptRunner.instantiateClass(bindingMap, config.getScript());
                ProvisionGroup pGroup = new ProvisionGroup(group);
                pGroup.setSrcSystemId(mSysID);
                int retval = script.execute(line, pGroup);
                if(retval == 0) {
                    Response responce = groupDataWebService.saveGroup(pGroup,"3000");
                    String groupId = (String)responce.getResponseValue();
                    IdentityDto identity = new IdentityDto();
                    identity.setIdentity(principal);
                    identity.setType(IdentityTypeEnum.GROUP);
                    identity.setManagedSysId(mSysID);
                    identity.setOperation(AttributeOperationEnum.ADD);
                    identity.setStatus(LoginStatusEnum.ACTIVE);
                    identity.setReferredObjectId(groupId);
                    identityService.save(identity);
                    provisionService.add(pGroup);
                    for(String memberPrincipal : pGroup.getMembersIds()) {
                        UserEntity user = userManager.getUserByPrincipal(memberPrincipal, mSysID, false);
                        if(user != null) {
                            Response response = groupDataWebService.addUserToGroup(groupId, user.getId(), "3000");
                            log.debug("User Member with principal = "+memberPrincipal+" was added to Group = "+identity.getIdentity() + " Managed Sys = "+identity.getManagedSysId() + ". \nResponse = "+response);
                        }
                    }
                }else{
                    log.debug("Couldn't populate ProvisionGroup. Group not added");
                    return false;
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
