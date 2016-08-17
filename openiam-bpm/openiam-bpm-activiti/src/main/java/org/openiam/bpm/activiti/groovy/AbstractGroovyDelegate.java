package org.openiam.bpm.activiti.groovy;

import java.util.Map;

import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;

public abstract class AbstractGroovyDelegate extends AbstractActivitiJob {
	
	protected AbstractGroovyDelegate() {
		super();
	}
	
	public final void init(final Map<String, Object> bindingMap) {
		postInit(bindingMap);
	}
	
	protected abstract void postInit(final Map<String, Object> bindingMap);
}
