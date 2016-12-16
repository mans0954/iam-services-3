package org.openiam.bpm.activiti.delegate.entitlements;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.el.FixedValue;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.activiti.groovy.AbstractGroovyDelegate;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class GroovyScriptDelegate extends AbstractActivitiJob {

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;
	
	private FixedValue scriptSrc;
	
	public GroovyScriptDelegate() {
		super();
	}
	
	@Override
	protected void doExecute(DelegateExecution execution) throws Exception {
		final IdmAuditLogEntity idmAuditLog = createNewAuditLog(execution);
		idmAuditLog.setAction(AuditAction.ACTIVITI_GROOVY_SCRIPT.value());
		try {
			if(scriptSrc == null) {
				throw new IllegalArgumentException("scriptSrc is required");
			}
			
			if(StringUtils.isBlank(scriptSrc.getExpressionText())) {
				throw new IllegalArgumentException("scriptSrc is required");
			}
			
			final String script = StringUtils.trimToNull(scriptSrc.getExpressionText());
			idmAuditLog.setGroovyScript(script);
			
			final Map<String, Object> bindingMap = new HashMap<>();
			final AbstractGroovyDelegate object = (AbstractGroovyDelegate)scriptRunner.instantiateClass(null, script);
			object.init(bindingMap);
			object.execute(execution);
			idmAuditLog.succeed();
		} catch(Throwable e) {
 			idmAuditLog.setException(e);
 			idmAuditLog.fail();
 			throw new RuntimeException(e);
 		} finally {
 			addAuditLogChild(execution, idmAuditLog);
 		}
	}
}
