package org.openiam.idm.srvc.recon.command.grp;

import org.apache.commons.collections.CollectionUtils;
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
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.service.GroupProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.script.ScriptIntegration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateIdmGroupCommand  implements ReconciliationObjectCommand<Group> {
    public static final String OPENIAM_MANAGED_SYS_ID = "0";
    private GroupProvisionService provisionService;
    private ReconciliationSituation config;
    private static final Log log = LogFactory.getLog(CreateIdmGroupCommand.class);
    private GroupDataWebService groupDataWebService;
    private IdentityService identityService;
    private final ScriptIntegration scriptRunner;

    public CreateIdmGroupCommand(final GroupDataWebService groupDataWebService,
                                 final IdentityService identityService,
                                 final GroupProvisionService provisionService,
                                 final ReconciliationSituation config,
                                 final ScriptIntegration scriptRunner) {
        this.groupDataWebService = groupDataWebService;
        this.identityService = identityService;
        this.provisionService = provisionService;
        this.config = config;
        this.scriptRunner = scriptRunner;
    }

    public boolean execute(IdentityDto identity, Group group, List<ExtensibleAttribute> attributes) {
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
                bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, identity.getManagedSysId());
                PopulationScript<ProvisionGroup> script = (PopulationScript<ProvisionGroup>)scriptRunner.instantiateClass(bindingMap, config.getScript());
                ProvisionGroup pGroup = new ProvisionGroup(group);
                pGroup.setSrcSystemId(identity.getManagedSysId());
                int retval = script.execute(line, pGroup);
                if(retval == 0) {
                    Response responce = groupDataWebService.saveGroup(pGroup,"3000");
                    identity.setReferredObjectId((String)responce.getResponseValue());
                    identityService.save(identity);
                    provisionService.addGroup(pGroup);
                }else{
                    log.debug("Couldn't populate ProvisionGroup. Group not added");
                    return false;
                }
                return true;
      //      } catch (ClassNotFoundException e) {
      //          e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
