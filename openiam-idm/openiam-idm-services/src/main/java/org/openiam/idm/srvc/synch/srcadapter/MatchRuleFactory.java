package org.openiam.idm.srvc.synch.srcadapter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Instantiates the appropriate matching rule object for use in the synchronization request
 * @author suneet
 *
 */
@Component("matchRuleFactory")
public class MatchRuleFactory {

	private static final Log log = LogFactory.getLog(MatchRuleFactory.class);
	
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    @Autowired
    private MatchObjectRule defaultMatchRule;

	public MatchObjectRule create(String customMatchRule) throws ClassNotFoundException {

		if (StringUtils.isBlank(customMatchRule)) {
			return defaultMatchRule;
		}
		// instantiate a rule via script

		try {
			return (MatchObjectRule)scriptRunner.instantiateClass(null, customMatchRule);
		} catch(Exception e) {
			log.error(e);
			return null;
		}
	}
}
