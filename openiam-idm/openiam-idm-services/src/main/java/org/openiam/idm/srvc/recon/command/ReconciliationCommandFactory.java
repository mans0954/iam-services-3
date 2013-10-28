package org.openiam.idm.srvc.recon.command;

import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.ReconciliationCommand;
import org.openiam.idm.srvc.recon.service.ReconciliationSituationResponseOptions;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.provision.service.ProvisionService;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.MuleContextProvider;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component("reconciliationFactory")
public class ReconciliationCommandFactory {
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;

    public ReconciliationCommand createCommand(String name, ReconciliationSituation config, String managedSysId) {
        ReconciliationCommand reconCommand = null;
        ApplicationContext applicationContext = SpringContextProvider.getApplicationContext();
        if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.NOTHING.name())){
            reconCommand = new DoNothingCommand();
        } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.DELETE_FROM_RES.name())){
            reconCommand = new DeleteResourceAccountCommand((ProvisionService) applicationContext.getBean("defaultProvision"),
                    (ManagedSystemWebService)applicationContext.getBean("managedSysService"),
                     MuleContextProvider.getCtx(),
                    managedSysId,
                    (ConnectorAdapter)applicationContext.getBean("connectorAdapter"));
        } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.DISABLE_IN_IDM.name())){
            reconCommand = new DisableIdmAccountCommand((ProvisionService) applicationContext.getBean("defaultProvision"));
        } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.REMOVE_FROM_IDM.name())){
            reconCommand = new RemoveIdmUserCommand((ProvisionService) applicationContext.getBean("defaultProvision"));
        } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.DELETE_FROM_IDM.name())){
            reconCommand = new DeleteIdmUserExcludeTargetCommand((ProvisionService) applicationContext.getBean("defaultProvision"));
        } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.ADD_TO_RES.name())){
            reconCommand = new CreateResourceAccountCommand((ProvisionService) applicationContext.getBean("defaultProvision"));
        } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.ADD_TO_IDM.name())){
            reconCommand = new CreateIdmAccountCommand((ProvisionService) applicationContext.getBean("defaultProvision"), config, scriptRunner);
        } else if(name.equalsIgnoreCase(ReconciliationSituationResponseOptions.UPDATE_IDM_FROM_RES.name())){
            reconCommand = new UpdateIdmUserCommand((ProvisionService) applicationContext.getBean("defaultProvision"), config, scriptRunner);
        }
        return reconCommand;
    }

}
