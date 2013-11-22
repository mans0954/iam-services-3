package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.impl.el.FixedValue;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.activiti.delegate.core.AbstractDelegate;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractEntitlementsDelegate extends AbstractDelegate {
	
	private FixedValue operation;
	
	@Autowired
	@Qualifier("defaultProvision")
	protected ProvisionService provisionService;

	@Autowired
	private UserDataService userDataService;
	
	protected AbstractEntitlementsDelegate() {
		super();
	}
	
	public String getOperation() {
		return (operation != null) ? StringUtils.trimToNull(operation.getExpressionText()) : null;
	}
	
	protected User getUser(final String userId) {
		return userDataService.getUserDto(userId);
	}
}
