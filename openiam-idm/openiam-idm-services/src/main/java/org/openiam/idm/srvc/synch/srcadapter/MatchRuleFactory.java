package org.openiam.idm.srvc.synch.srcadapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
import org.openiam.idm.srvc.synch.service.SourceAdapter;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Instantiates the appropriate matching rule object for use in the synchronization request
 * @author suneet
 *
 */
public class MatchRuleFactory implements  ApplicationContextAware {
	public static ApplicationContext ac;
	private static final Log log = LogFactory.getLog(MatchRuleFactory.class);
	
	@Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ac = applicationContext;
	}
	
	public MatchObjectRule create(SynchConfig config) throws ClassNotFoundException {

		if (config.getCustomMatchRule() == null || config.getCustomMatchRule().length() == 0 ) {
			return (MatchObjectRule)ac.getBean("defaultMatchRule");		
		}
		// instantiate a rule via script
		String matchRule = config.getCustomMatchRule();
		if (matchRule == null || matchRule.length() ==0) {
			return null;
		}
		try {
			return (MatchObjectRule)scriptRunner.instantiateClass(null, matchRule);
		}catch(Exception e) {
			log.error(e);
			return null;
		}
	}
}
