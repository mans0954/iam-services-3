package org.openiam.idm.srvc.recon.command.grp;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttribute;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.PopulationScript;
import org.openiam.idm.srvc.recon.service.ReconciliationObjectCommand;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.resp.ProvisionGroupResponse;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.service.GroupProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("updateIdmGroupCommand")
public class UpdateIdmGroupCommand  implements ReconciliationObjectCommand<Group> {
    private static final Log log = LogFactory.getLog(UpdateIdmGroupCommand.class);

    @Autowired
    @Qualifier("groupProvision")
    private GroupProvisionService provisionService;

    @Autowired
    private GroupDataWebService groupDataService;

    @Autowired
    private ResourceDataService resourceDataService;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    public UpdateIdmGroupCommand() {
    }

    public boolean execute(ReconciliationSituation config, IdentityDto identity, Group group, List<ExtensibleAttribute> attributes) {
        log.debug("Entering UpdateIdmGroupCommand");
            ProvisionGroup pGroup = new ProvisionGroup(group);
            pGroup.setSrcSystemId(identity.getManagedSysId());
            if(StringUtils.isNotEmpty(config.getScript())){
                try {
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
                    Map<String, Object> bindingMap = new HashMap<String, Object>();
                    bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, identity.getManagedSysId());
                    PopulationScript<ProvisionGroup> script = (PopulationScript<ProvisionGroup>) scriptRunner.instantiateClass(bindingMap, config.getScript());
                    int retval = script.execute(line, pGroup);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Set<Resource> resources = pGroup.getResources();

                Response grpResp = groupDataService.saveGroup(pGroup, "3000");
                String groupId = (String) grpResp.getResponseValue();
                for (Resource res : resources) {
                    if (res.getOperation() == AttributeOperationEnum.ADD) {
                        resourceDataService.addGroupToResource(res.getId(), groupId, "3000");
                    } else if (res.getOperation() == AttributeOperationEnum.DELETE) {
                        resourceDataService.removeGroupToResource(res.getId(), groupId, "3000");
                    }
                }
                ProvisionGroupResponse response = provisionService.modifyGroup(pGroup);
                return response.isSuccess();
            }
        return false;
    }

}
