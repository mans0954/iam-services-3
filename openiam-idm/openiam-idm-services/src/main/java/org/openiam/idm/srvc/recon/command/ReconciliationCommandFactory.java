package org.openiam.idm.srvc.recon.command;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.ReconciliationCommand;
import org.openiam.idm.srvc.recon.service.ReconciliationObjectCommand;
import org.openiam.idm.srvc.recon.service.ReconciliationSituationResponseOptions;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.service.ProvisionService;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component("reconciliationFactory")
public class ReconciliationCommandFactory {

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;

    @Autowired
    @Qualifier("doNothingGroupCommand")
    private ReconciliationObjectCommand doNothingGroupCommandDefault;

    @Autowired
    @Qualifier("deleteResourceGroupCommand")
    private ReconciliationObjectCommand deleteResourceGroupCommandDefault;

    @Autowired
    @Qualifier("removeIdmGroupCommand")
    private ReconciliationObjectCommand removeIdmGroupCommandDefault;

    @Autowired
    @Qualifier("deleteIdmExcludeTargetGroupCommand")
    private ReconciliationObjectCommand deleteIdmExcludeTargetGroupCommandDefault;

    @Autowired
    @Qualifier("createResourceGroupCommand")
    private ReconciliationObjectCommand createResourceGroupCommandDefault;

    @Autowired
    @Qualifier("createIdmGroupCommand")
    private ReconciliationObjectCommand createIdmGroupCommandDefault;

    @Autowired
    @Qualifier("updateIdmGroupCommand")
    private ReconciliationObjectCommand updateIdmGroupCommandDefault;

    @Autowired
    @Qualifier("doNothingUserCommand")
    private ReconciliationCommand doNothingUserCommand;

    @Autowired
    @Qualifier("createIdmAccountUserCommand")
    private ReconciliationCommand createIdmAccountUserCommand;

    @Autowired
    @Qualifier("deleteResourceAccountUserCommand")
    private ReconciliationCommand deleteResourceAccountUserCommand;

    @Autowired
    @Qualifier("disableIdmAccountUserCommand")
    private ReconciliationCommand disableIdmAccountUserCommand;

    @Autowired
    @Qualifier("removeIdmUserCommand")
    private ReconciliationCommand removeIdmUserCommand;

    @Autowired
    @Qualifier("deleteIdmUserExcludeTargetCommand")
    private ReconciliationCommand deleteIdmUserExcludeTargetCommand;

    @Autowired
    @Qualifier("createResourceAccountUserCommand")
    private ReconciliationCommand createResourceAccountUserCommand;

    @Autowired
    @Qualifier("updateIdmUserCommand")
    private ReconciliationCommand updateIdmUserCommand;

    public ReconciliationCommand createUserCommand(String name, ReconciliationSituation config, String managedSysId) throws IOException {
        ReconciliationCommand reconCommand = null;
        ApplicationContext applicationContext = SpringContextProvider.getApplicationContext();

        if(StringUtils.isNotEmpty(config.getCustomCommandScript())) {
            Map<String, Object> bindingMap = new HashMap<String, Object>();
            bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, managedSysId);
            reconCommand = (ReconciliationCommand) scriptRunner.instantiateClass(bindingMap, config.getCustomCommandScript());
        } else {
            if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.NOTHING.name())){
                reconCommand = doNothingUserCommand;
            } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.DELETE_FROM_RES.name())){
                reconCommand = deleteResourceAccountUserCommand;
            } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.DISABLE_IN_IDM.name())){
                reconCommand = disableIdmAccountUserCommand;
            } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.REMOVE_FROM_IDM.name())){
                reconCommand = removeIdmUserCommand;
            } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.DELETE_FROM_IDM.name())){
                reconCommand = deleteIdmUserExcludeTargetCommand;
            } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.ADD_TO_RES.name())){
                reconCommand = createResourceAccountUserCommand;
            } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.ADD_TO_IDM.name())){
                reconCommand = createIdmAccountUserCommand;
            } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.UPDATE_IDM_FROM_RES.name())){
                reconCommand = updateIdmUserCommand;
            }
        }
        return reconCommand;
    }

    public ReconciliationObjectCommand<Group> createGroupCommand(String name, ReconciliationSituation config, String managedSysId) throws IOException {
        ReconciliationObjectCommand<Group> reconCommand = null;
        if(StringUtils.isNotEmpty(config.getCustomCommandScript())) {
            Map<String, Object> bindingMap = new HashMap<String, Object>();
            bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, managedSysId);
            reconCommand = (ReconciliationObjectCommand<Group>) scriptRunner.instantiateClass(bindingMap, config.getCustomCommandScript());
        } else {
            if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.NOTHING.name())) {
                reconCommand = doNothingGroupCommandDefault;
            } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.DELETE_FROM_RES.name())){
                reconCommand = deleteResourceGroupCommandDefault;
            } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.DISABLE_IN_IDM.name())){
                // nothing
            } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.REMOVE_FROM_IDM.name())){
                reconCommand = removeIdmGroupCommandDefault;
            } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.DELETE_FROM_IDM.name())){
                reconCommand = deleteIdmExcludeTargetGroupCommandDefault;
            } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.ADD_TO_RES.name())){
                reconCommand = createResourceGroupCommandDefault;
            } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.ADD_TO_IDM.name())){
                reconCommand = createIdmGroupCommandDefault;
            } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.UPDATE_IDM_FROM_RES.name())){
                reconCommand = updateIdmGroupCommandDefault;
            }
        }
        return reconCommand;
    }
}
