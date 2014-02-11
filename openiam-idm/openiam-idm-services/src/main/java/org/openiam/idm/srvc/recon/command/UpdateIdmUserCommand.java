package org.openiam.idm.srvc.recon.command;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.PopulationScript;
import org.openiam.idm.srvc.recon.service.ReconciliationCommand;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.LookupUserResponse;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.service.ProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.SpringContextProvider;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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
        LookupUserResponse lookupResp =  provisionService.getTargetSystemUser(login.getLogin(), login.getManagedSysId(), attributes);
        if(lookupResp.getStatus() == ResponseStatus.FAILURE){
            log.debug("Can't update IDM user from non-existent resource...");
        } else {
            ProvisionUser pUser = new ProvisionUser(user);
            setCurrentSuperiors(pUser);
            pUser.setSrcSystemId(login.getManagedSysId());
            if(StringUtils.isNotEmpty(config.getScript())){
                try {
                    Map<String, String> line = new HashMap<String, String>();
                    for (ExtensibleAttribute attr : attributes) {
                        line.put(attr.getName(), attr.getValue());
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
        return false;
    }

    protected void setCurrentSuperiors(ProvisionUser pUser) {
        if (StringUtils.isNotEmpty(pUser.getId())) {

            ApplicationContext appContext = SpringContextProvider.getApplicationContext();
            UserDataService userMgr = (UserDataService)appContext.getBean("userManager");
            UserDozerConverter userDozerConverter = (UserDozerConverter)appContext.getBean("userDozerConverter");

            List<UserEntity> entities = userMgr.getSuperiors(pUser.getId(), -1, -1);
            List<User> superiors = userDozerConverter.convertToDTOList(entities, true);
            if (CollectionUtils.isNotEmpty(superiors)) {
                pUser.setSuperiors(new HashSet<User>(superiors));
            }
        }
    }
}
