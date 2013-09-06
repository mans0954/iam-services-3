package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.impl.el.FixedValue;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.activiti.delegate.core.AbstractDelegate;

public abstract class AbstractEntitlementsDelegate extends AbstractDelegate {
	
	private FixedValue operation;

	protected AbstractEntitlementsDelegate() {
		super();
	}
	
	public String getOperation() {
		return (operation != null) ? StringUtils.trimToNull(operation.getExpressionText()) : null;
	}
}
