package org.openiam.idm.srvc.auth.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AuthenticationUtils {
    @Value("${org.openiam.core.login.login.module.default}")
    private String defaultLoginModule;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    @Value("${org.openiam.auth.credentials.validator.groovy.script}")
    protected String authCredentialsValidatorScript;

    @Value("${org.openiam.auth.credentials.validator.groovy.script.cache}")
    protected boolean cacheCredentialsValidatorEnable;
    private static final Log log = LogFactory.getLog(AuthenticationUtils.class);

    @Autowired
    protected AuthCredentialsValidator defaultAuthCredentialsValidator;

    private AuthCredentialsValidator validator;

     @PostConstruct
    public void populateCredentialsValidator() {
        if(validator == null) {
            if (cacheCredentialsValidatorEnable) {
                try {
                    if (StringUtils.isNotBlank(authCredentialsValidatorScript)) {
                        validator = (AuthCredentialsValidator)scriptRunner.instantiateClass(null, authCredentialsValidatorScript);
                        if(log.isDebugEnabled()) {
                        	log.debug("Using custom credentials validator " + authCredentialsValidatorScript);
                        }
                    } else {
                        validator = defaultAuthCredentialsValidator;
                    }
                } catch (Exception exc) {
                    log.error(exc);
                }
            }
        }
    }


    public AuthCredentialsValidator getCredentialsValidator() {
        if(validator == null) {
            try {
                if (StringUtils.isNotBlank(authCredentialsValidatorScript)) {
                    validator = (AuthCredentialsValidator)scriptRunner.instantiateClass(null, authCredentialsValidatorScript);
                    if(log.isDebugEnabled()) {
                    	log.debug("Using custom credentials validator " + authCredentialsValidatorScript);
                    }
                } else {
                    validator = defaultAuthCredentialsValidator;
                }
            } catch (Exception exc) {
                log.error(exc);
            }
        }
        return validator;
    }


}
