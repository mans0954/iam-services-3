package org.openiam.idm.srvc.recon.command.grp;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.recon.command.BaseReconciliationCommand;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.PopulationScript;
import org.openiam.idm.srvc.recon.service.ReconciliationObjectCommand;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseReconciliationGroupCommand extends BaseReconciliationCommand implements ReconciliationObjectCommand<Group> {

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;

	@Override
	abstract public boolean execute(ReconciliationSituation config, String principal, String mSysID, Group group, List<ExtensibleAttribute> attributes);

	protected int executeScript(String scriptPath, List<ExtensibleAttribute> attributes, ProvisionGroup pGroup) throws Exception {
		if(StringUtils.isNotEmpty(scriptPath)) {
			Map<String, String> line = attributesToMap(attributes);
			Map<String, Object> bindingMap = new HashMap<String, Object>();
			bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, pGroup.getSrcSystemId());
			PopulationScript<ProvisionGroup> script = (PopulationScript<ProvisionGroup>)
					scriptRunner.instantiateClass(bindingMap, scriptPath);
			return script.execute(line, pGroup);
		}
		return 1;
	}
}
