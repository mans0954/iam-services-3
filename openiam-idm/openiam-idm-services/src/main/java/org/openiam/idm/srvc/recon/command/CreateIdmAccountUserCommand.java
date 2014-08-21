package org.openiam.idm.srvc.recon.command;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttribute;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.PopulationScript;
import org.openiam.idm.srvc.recon.service.ReconciliationCommand;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
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

/**
 * Created with IntelliJ IDEA.
 * User: Pascal
 * Date: 27.04.12
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
@Component("createIdmAccountUserCommand")
public class CreateIdmAccountUserCommand implements ReconciliationCommand {
    private static final Log log = LogFactory.getLog(CreateIdmAccountUserCommand.class);

    public static final String OPENIAM_MANAGED_SYS_ID = "0";

    @Autowired
    @Qualifier("defaultProvision")
    private ProvisionService provisionService;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    public CreateIdmAccountUserCommand() {
    }

    public boolean execute(ReconciliationSituation config, Login login, User user, List<ExtensibleAttribute> attributes)  {
        log.debug("Entering CreateIdmAccountCommand");
        if(attributes == null){
            log.debug("Can't create IDM user without attributes");
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
                bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, login.getManagedSysId());
                PopulationScript script = (PopulationScript)scriptRunner.instantiateClass(bindingMap, config.getScript());
                ProvisionUser pUser = new ProvisionUser(user);
                pUser.setSrcSystemId(login.getManagedSysId());
                int retval = script.execute(line, pUser);
                if(retval == 0){
                    if(login != null) {
                        Login idmLogin = null;
                        for(Login pr : user.getPrincipalList()) {
                           if(OPENIAM_MANAGED_SYS_ID.equalsIgnoreCase(pr.getManagedSysId())) {
                               idmLogin = pr;
                           }
                        }
                        if(idmLogin == null){
                            idmLogin = new Login();
                            idmLogin.setOperation(AttributeOperationEnum.ADD);
                            idmLogin.setLogin(login.getLogin());
                            idmLogin.setManagedSysId("0");
                            pUser.getPrincipalList().add(idmLogin);
                        }
                    }
                    provisionService.addUser(pUser);
                }else{
                    log.debug("Couldn't populate ProvisionUser. User not added");
                    return false;
                }
                return true;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
