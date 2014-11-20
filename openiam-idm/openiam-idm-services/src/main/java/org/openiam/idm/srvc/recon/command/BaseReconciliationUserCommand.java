package org.openiam.idm.srvc.recon.command;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.PopulationScript;
import org.openiam.idm.srvc.recon.service.ReconciliationObjectCommand;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseReconciliationUserCommand extends BaseReconciliationCommand implements ReconciliationObjectCommand<User> {

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;

	@Override
	abstract public boolean execute(ReconciliationSituation config, String principal, String mSysID, User user, List<ExtensibleAttribute> attributes);

	protected int executeScript(String scriptPath, List<ExtensibleAttribute> attributes, ProvisionUser pUser) throws Exception {
		if(StringUtils.isNotEmpty(scriptPath)) {
			Map<String, String> line = attributesToMap(attributes);
			Map<String, Object> bindingMap = new HashMap<String, Object>();
			bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, pUser.getSrcSystemId());
			PopulationScript<ProvisionUser> script = (PopulationScript<ProvisionUser>)
					scriptRunner.instantiateClass(bindingMap, scriptPath);
			return script.execute(line, pUser);
		}
		return 1;
	}
}
