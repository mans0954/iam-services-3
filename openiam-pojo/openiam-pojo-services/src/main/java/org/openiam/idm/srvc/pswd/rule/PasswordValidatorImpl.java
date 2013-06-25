/*
 * Copyright 2009, OpenIAM LLC This file is part of the OpenIAM Identity and
 * Access Management Suite
 * 
 * OpenIAM Identity and Access Management Suite is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License version 3 as published by the Free Software Foundation.
 * 
 * OpenIAM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the Lesser GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenIAM. If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.idm.srvc.pswd.rule;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.exception.ObjectNotFoundException;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.policy.domain.PolicyDefParamEntity;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
import org.openiam.idm.srvc.policy.service.PolicyDefParamDAO;
import org.openiam.idm.srvc.pswd.dto.Password;
import org.openiam.idm.srvc.pswd.dto.PasswordValidationCode;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.secdomain.service.SecurityDomainDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * PasswordValidator validates a password against the password policy.
 * @author suneet
 *
 */
@Service("passwordValidator")
public class PasswordValidatorImpl implements PasswordValidator {

    @Autowired
    private PolicyDefParamDAO policyDefParamDao;
	
    @Autowired
    protected UserDAO userDao;
    
    @Autowired
    protected LoginDAO loginDao;
    
    @Autowired
    protected PasswordHistoryDAO passwordHistoryDao;
    
    @Autowired
    @Qualifier("cryptor")
    protected Cryptor cryptor;
    
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;
    
    @Autowired
    protected KeyManagementService keyManagementService;

    private static final Log log = LogFactory.getLog(PasswordValidatorImpl.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.pswd.rule.PasswordValidator#validate(org.openiam
     * .idm.srvc.pswd.dto.Password)
     */
    public PasswordValidationCode validate(Policy pswdPolicy, Password password)
            throws ObjectNotFoundException, IOException {
        // get the user object for the principal
        LoginEntity lg = loginDao.getRecord(password.getPrincipal(), password.getManagedSysId(), password.getDomainId());
        UserEntity usr = userDao.findById(lg.getUserId());

        return validateForUser(pswdPolicy, password, usr, lg);
    }

    @Override
    public PasswordValidationCode validateForUser(Policy pswdPolicy,
            Password password, UserEntity user, LoginEntity login)
            throws ObjectNotFoundException, IOException {
        Class cls = null;
        AbstractPasswordRule rule = null;

        // get the password policy for this domain
        // SecurityDomain securityDomain = secDomainService.getSecurityDomain(
        // password.getDomainId() );
        // Policy pswdPolicy = policyDataService.getPolicy(
        // securityDomain.getPasswordPolicyId() ) ;
        // get the list of rules for password validation
        
        final List<PolicyDefParamEntity> defParam =  policyDefParamDao.findPolicyDefParamByGroup(pswdPolicy.getPolicyDefId(), "PSWD_COMPOSITION");

        // get the user object for the principal if they are null
        LoginEntity lg = login;
        if (lg == null) {
        	lg = loginDao.getRecord(password.getPrincipal(), password.getManagedSysId(), password.getDomainId());
        }
        UserEntity usr = user;
        if (usr == null) {
            usr = userDao.findById(lg.getUserId());
        }

        // for each rule
        if (defParam != null) {
            for (PolicyDefParamEntity param : defParam) {
                // check if this is parameter that is the policy that we need to
                // check
                if (policyToCheck(param.getDefParamId(), pswdPolicy)) {
                    //
                    String strRule = param.getPolicyParamHandler();
                    if (strRule != null && strRule.length() > 0) {
                        // -- instantiate the rule class
                        log.info("StrRule:" + strRule);
                        if (param.getHandlerLanguage() == null
                                || param.getHandlerLanguage().equalsIgnoreCase(
                                        "java")) {
                            try {
                                cls = Class.forName(strRule);
                                rule = (AbstractPasswordRule) cls.newInstance();
                            } catch (Exception c) {
                                log.info("Error creating object: " + strRule);
                                log.error(c);
                                throw new ObjectNotFoundException();
                            }
                        } else {
                            rule = (AbstractPasswordRule) scriptRunner.instantiateClass(
                                    null, strRule);

                        }
                    }
                    
                    // -- set the parameters
                    rule.setDomainId(password.getDomainId());
                    rule.setSkipPasswordFrequencyCheck(password.isSkipPasswordFrequencyCheck());
                    rule.setPassword(password.getPassword());
                    rule.setPrincipal(password.getPrincipal());
                    rule.setManagedSysId(password.getManagedSysId());
                    rule.setUser(usr);
                    rule.setLg(lg);
                    rule.setPolicy(pswdPolicy);
                    rule.setPasswordHistoryDao(passwordHistoryDao);
                    rule.setCryptor(cryptor);
                    rule.setKeyManagementService(keyManagementService);
                    // -- check if valid
                    PasswordValidationCode retval = rule.isValid();

                    if (retval != PasswordValidationCode.SUCCESS) {
                        log.info("Password failed validation check for rule:"
                                + strRule);
                        return retval;
                    } else {
                        log.info("Passed validation for:" + strRule);
                    }
                }

            }
        }

        return PasswordValidationCode.SUCCESS;
    }

    private boolean policyToCheck(String defParamId, Policy pswdPolicy) {

        final Set<PolicyAttribute> attrSet = pswdPolicy.getPolicyAttributes();
        final Iterator<PolicyAttribute> atrIt = attrSet.iterator();
        while (atrIt.hasNext()) {
        	final PolicyAttribute atr = atrIt.next();
        	if(StringUtils.equals(atr.getDefParamId(), defParamId)) {
                return true;
            }
        }
        return false;

    }
}
