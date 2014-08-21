package org.openiam.idm.srvc.recon.command.grp;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.openiam.base.BaseAttribute;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.PopulationScript;
import org.openiam.idm.srvc.recon.service.ReconciliationObjectCommand;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.resp.ProvisionGroupResponse;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.provision.service.GroupProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.MuleContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("deleteResourceGroupCommand")
public class DeleteResourceGroupCommand  implements ReconciliationObjectCommand<Group> {
    private static final Log log = LogFactory.getLog(DeleteResourceGroupCommand.class);

    @Autowired
    @Qualifier("groupProvision")
    private GroupProvisionService provisionService;

    @Autowired
    private ManagedSystemWebService managedSysService;

    @Autowired
    private ConnectorAdapter connectorAdapter;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    public DeleteResourceGroupCommand() {
    }

    @Override
    public boolean execute(ReconciliationSituation config, IdentityDto identity, Group group, List<ExtensibleAttribute> attributes) {
        log.debug("Entering DeleteResourceGroupCommand");
        log.debug("Do delete for Group :" + identity.getIdentity());
        if(group == null) {
            ManagedSysDto mSys = managedSysService.getManagedSys(identity.getManagedSysId());

            log.debug("Calling delete with Remote connector");
            CrudRequest<ExtensibleUser> request = new CrudRequest<ExtensibleUser>();
            request.setObjectIdentity(identity.getIdentity());
            request.setTargetID(identity.getManagedSysId());
            request.setHostLoginId(mSys.getUserId());
            request.setHostLoginPassword(mSys.getDecryptPassword());
            request.setHostUrl(mSys.getHostUrl());
            request.setScriptHandler(mSys.getDeleteHandler());
            log.debug("Calling delete local connector");
            connectorAdapter.deleteRequest(mSys, request,  MuleContextProvider.getCtx());

            return true;
        }


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
                //Reset source system flag from User to avoid ignoring Provisioning for this resource
                pGroup.setSrcSystemId(null);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ProvisionGroupResponse response = provisionService.deleteGroup(identity.getManagedSysId(), pGroup.getId(), UserStatusEnum.DELETED,  "3000");
        return response.isSuccess();
    }

}
