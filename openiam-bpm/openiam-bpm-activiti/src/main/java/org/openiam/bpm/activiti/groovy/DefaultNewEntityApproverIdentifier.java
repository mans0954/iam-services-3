package org.openiam.bpm.activiti.groovy;

import java.util.HashSet;
import java.util.Set;

import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Value;

public class DefaultNewEntityApproverIdentifier {
	
	@Value("${org.openiam.idm.activiti.default.approver.user}")
	protected String defaultApproverUser;

	public DefaultNewEntityApproverIdentifier() {
		SpringContextProvider.autowire(this);
		SpringContextProvider.resolveProperties(this);
	}
	
	public Set<String> getApprovers() {
		final Set<String> approvers = new HashSet<String>();
		approvers.add(defaultApproverUser);
		return approvers;
	}
}
