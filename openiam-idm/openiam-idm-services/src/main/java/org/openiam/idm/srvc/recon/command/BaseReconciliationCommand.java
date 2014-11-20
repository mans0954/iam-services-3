package org.openiam.idm.srvc.recon.command;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.BaseAttribute;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.PopulationScript;
import org.openiam.idm.srvc.recon.service.ReconciliationObjectCommand;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public abstract class BaseReconciliationCommand {

	protected static final char MULTIPLE_VALUES_DELIMITER = '^';
	protected static final String DEFAULT_REQUESTER_ID = "3000";
	public static final String OPENIAM_MANAGED_SYS_ID = "0";

	protected Map<String, String> attributesToMap(List<ExtensibleAttribute> attributes) {
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
						value.append(MULTIPLE_VALUES_DELIMITER);
					} else {
						isFirst = false;
					}
					value.append(ba.getValue());
				}
				line.put(attr.getName(), value.toString());
			}
		}
		return line;
    }
}
