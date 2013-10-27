package org.openiam.idm.srvc.recon.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.PopulationScript;
import org.openiam.idm.srvc.recon.service.ReconciliationCommand;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.LookupUserResponse;
import org.openiam.provision.service.ProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.script.ScriptIntegration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateIdmUserCommand implements ReconciliationCommand {
    private ProvisionService provisionService;
    private ReconciliationSituation config;
    private static final Log log = LogFactory.getLog(UpdateIdmUserCommand.class);
    private final ScriptIntegration scriptRunner;

    public UpdateIdmUserCommand(ProvisionService provisionService, ReconciliationSituation config, ScriptIntegration scriptRunner) {
        this.provisionService = provisionService;
        this.config = config;
        this.scriptRunner = scriptRunner;
    }

    public boolean execute(Login login, User user, List<ExtensibleAttribute> attributes) {
        log.debug("Entering UpdateIdmUserCommand");
        LookupUserResponse lookupResp =  provisionService.getTargetSystemUser(login.getLogin(), login.getManagedSysId());
        if(lookupResp.getStatus() == ResponseStatus.FAILURE){
            log.debug("Can't update IDM user from non-existent resource...");
        } else {
            Map<String, String> line = new HashMap<String, String>();
            for(ExtensibleAttribute attr: attributes){
                line.put(attr.getName(), attr.getValue());
            }
            try {
                PopulationScript script = (PopulationScript)scriptRunner.instantiateClass(null, config.getScript());
                ProvisionUser pUser = new ProvisionUser(user);
                pUser.setSrcSystemId(login.getManagedSysId());
                int retval = script.execute(line, pUser);
                if(retval == 0){
                    provisionService.modifyUser(pUser);
                }else{
                    log.debug("Couldn't populate ProvisionUser. User not modified");
                    return false;
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
