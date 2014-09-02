package org.openiam.bpm.activiti.groovy;

import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.bpm.activiti.ActivitiService;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class AbstractGroovyDelegate extends AbstractActivitiJob {
	
	@Autowired
	protected ActivitiService activitiService;
	
    @Autowired
    @Qualifier("transactionManager")
    protected PlatformTransactionManager transactionManager;
	
	protected AbstractGroovyDelegate() {
		SpringContextProvider.autowire(this);
		SpringContextProvider.resolveProperties(this);
	}
	
	public final void init(final Map<String, Object> bindingMap) {
		postInit(bindingMap);
	}
	
	public abstract void execute(DelegateExecution execution) throws Exception;
	protected abstract void postInit(final Map<String, Object> bindingMap);
}
