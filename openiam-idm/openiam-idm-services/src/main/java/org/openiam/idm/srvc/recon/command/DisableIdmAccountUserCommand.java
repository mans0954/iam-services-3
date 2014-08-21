package org.openiam.idm.srvc.recon.command;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.BaseAttribute;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.PopulationScript;
import org.openiam.idm.srvc.recon.service.ReconciliationCommand;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.service.ProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("disableIdmAccountUserCommand")
public class DisableIdmAccountUserCommand implements ReconciliationCommand {
    private static final Log log = LogFactory.getLog(DisableIdmAccountUserCommand.class);

    @Autowired
    @Qualifier("defaultProvision")
    private ProvisionService provisionService;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;


    public DisableIdmAccountUserCommand(){
    }

    public boolean execute(ReconciliationSituation config, Login login, User user, List<ExtensibleAttribute> attributes) {
        List<Login> principleList = user.getPrincipalList();
        for(Login l : principleList){
            if(l.getLoginId().equals(login.getLoginId())){
                l.setStatus(LoginStatusEnum.INACTIVE);
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
