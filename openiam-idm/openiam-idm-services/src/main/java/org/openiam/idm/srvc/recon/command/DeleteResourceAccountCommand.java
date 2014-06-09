package org.openiam.idm.srvc.recon.command;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttribute;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.PopulationScript;
import org.openiam.idm.srvc.recon.service.ReconciliationCommand;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.provision.service.ProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteResourceAccountCommand implements ReconciliationCommand {
    private ProvisionService provisionService;
    private static final Log log = LogFactory.getLog(DeleteResourceAccountCommand.class);
    private ManagedSystemWebService managedSysService;
    private MuleContext muleContext;
    private String managedSysId;
    private ConnectorAdapter connectorAdapter;
    private final ReconciliationSituation config;

    private final ScriptIntegration scriptRunner;

    public DeleteResourceAccountCommand(ProvisionService provisionService,
                                        ManagedSystemWebService managedSysService,
                                        MuleContext muleContext,
                                        String managedSysId,
                                        ConnectorAdapter connectorAdapter,
                                        ReconciliationSituation config,
                                        ScriptIntegration scriptRunner) {
        this.provisionService = provisionService;
        this.managedSysService = managedSysService;
        this.muleContext = muleContext;
        this.managedSysId = managedSysId;
        this.connectorAdapter = connectorAdapter;
        this.scriptRunner = scriptRunner;
        this.config = config;
    }

    public boolean execute(Login login, User user, List<ExtensibleAttribute> attributes) {
        log.debug("Entering DeleteResourceAccountCommand");
        if(user == null) {
            ManagedSysDto mSys = managedSysService.getManagedSys(managedSysId);

            log.debug("Calling delete with Remote connector");
            CrudRequest<ExtensibleUser> request = new CrudRequest<ExtensibleUser>();
            request.setObjectIdentity(login.getLogin());
            request.setTargetID(login.getManagedSysId());
            request.setHostLoginId(mSys.getUserId());
            request.setHostLoginPassword(mSys.getDecryptPassword());
            request.setHostUrl(mSys.getHostUrl());
            request.setScriptHandler(mSys.getDeleteHandler());
            log.debug("Calling delete local connector");
            connectorAdapter.deleteRequest(mSys, request, muleContext);

            return true;
        }
        List<Login> principleList = user.getPrincipalList();
        for(Login l : principleList){
            if(l.getLoginId().equals(login.getLoginId())){
                l.setOperation(AttributeOperationEnum.DELETE);
                break;
            }
        }

        ProvisionUser pUser = new ProvisionUser(user);
        pUser.setPrincipalList(principleList);
        pUser.setSrcSystemId(login.getManagedSysId());
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
                bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, login.getManagedSysId());
                PopulationScript script = (PopulationScript) scriptRunner.instantiateClass(bindingMap, config.getScript());
                int retval = script.execute(line, pUser);
                //Reset source system flag from User to avoid ignoring Provisioning for this resource
                pUser.setSrcSystemId(null);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ProvisionUserResponse response = provisionService.modifyUser(pUser);
        return response.isSuccess();
    }
}
