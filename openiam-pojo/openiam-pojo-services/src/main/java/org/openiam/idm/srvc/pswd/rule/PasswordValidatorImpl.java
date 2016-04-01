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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
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
import org.openiam.idm.srvc.policy.service.PolicyDefParamDAO;
import org.openiam.idm.srvc.policy.service.PolicyService;
import org.openiam.idm.srvc.pswd.dto.Password;
import org.openiam.idm.srvc.pswd.dto.PasswordRule;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * PasswordValidator validates a password against the password policy.
 *
 * @author suneet
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
    protected PolicyService policyService;

    @Autowired
    @Qualifier("cryptor")
    protected Cryptor cryptor;
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;
    @Autowired
    protected KeyManagementService keyManagementService;
    private static final Log log = LogFactory.getLog(PasswordValidatorImpl.class);

    @Override
    public void validate(Policy pswdPolicy, Password password)
            throws ObjectNotFoundException, IOException, PasswordRuleException {
// get the user object for the principal
        LoginEntity lg = loginDao.getRecord(password.getPrincipal(), password.getManagedSysId());
        UserEntity usr = userDao.findById(lg.getUserId());
        validateForUser(pswdPolicy, password, usr, lg);
    }

    @Override
    public List<PasswordRuleException> getAllViolatingRules(Policy pswdPolicy, Password password)
            throws ObjectNotFoundException, IOException {
        // get the user object for the principal
        LoginEntity lg = loginDao.getRecord(password.getPrincipal(), password.getManagedSysId());
        UserEntity usr = userDao.findById(lg.getUserId());

        return getAllPossibleFailures(pswdPolicy, password, usr, lg);
    }

    public List<PasswordRuleException> getAllPossibleFailures(Policy pswdPolicy, Password password, UserEntity user, LoginEntity login) throws ObjectNotFoundException, IOException {
        final List<PasswordRuleException> retVal = new LinkedList<>();
        final List<AbstractPasswordRule> rules = getRules(pswdPolicy, password, user, login);
        if (CollectionUtils.isNotEmpty(rules)) {
            for (final AbstractPasswordRule rule : rules) {
                try {
                    rule.validate();
                } catch (PasswordRuleException e) {
                    retVal.add(e);
                }
            }
        }
        return retVal;
    }

    public void validateForUser(Policy policy, Password password, UserEntity usr, LoginEntity lg, List<AbstractPasswordRule> pwdRules) throws ObjectNotFoundException, IOException, PasswordRuleException{
        if(CollectionUtils.isEmpty(pwdRules)){
            pwdRules = getRules(policy, password, usr, lg);
        }
        if (CollectionUtils.isNotEmpty(pwdRules)) {
            for (final AbstractPasswordRule rule : pwdRules) {
                rule.validate();
                log.info(String.format("Passed validation for: %s", rule));
            }
        }
    }

    @Override
    public void validateForUser(Policy pswdPolicy, Password password, UserEntity user, LoginEntity login)
            throws ObjectNotFoundException, IOException, PasswordRuleException {
        final List<AbstractPasswordRule> rules = getRules(pswdPolicy, password, user, login);
        if (CollectionUtils.isNotEmpty(rules)) {
            for (final AbstractPasswordRule rule : rules) {
                rule.validate();
                log.info(String.format("Passed validation for: %s", rule));
            }
        }
    }

    @Override
    public List<PasswordRule> getPasswordRules(final Policy policy, final Password password) throws ObjectNotFoundException, IOException {
        LoginEntity lg = loginDao.getRecord(password.getPrincipal(), password.getManagedSysId());
        UserEntity usr = userDao.findById(lg.getUserId());
        return getPasswordRules(policy, password, usr, lg);
    }

    @Override
    public List<PasswordRule> getPasswordRules(final Policy pswdPolicy, final Password password, final UserEntity user, final LoginEntity login)
            throws ObjectNotFoundException, IOException {
        final List<PasswordRule> exceptions = new LinkedList<>();
        final List<AbstractPasswordRule> rules = getRules(pswdPolicy, password, user, login);
        if (CollectionUtils.isNotEmpty(rules)) {
            for (final AbstractPasswordRule rule : rules) {
                final PasswordRule ex = rule.createRule();
                if (ex != null) {
                    exceptions.add(ex);
                }
                log.info(String.format("Passed validation for: %s", rule));
            }
        }
        return exceptions;
    }

    @Override
    public List<AbstractPasswordRule> getRules(Policy pswdPolicy, Password password, UserEntity user, LoginEntity login)
            throws ObjectNotFoundException, IOException {
        final List<AbstractPasswordRule> rules = new LinkedList<>();
        final List<PolicyDefParamEntity> defParam = policyDefParamDao.findPolicyDefParamByGroup(pswdPolicy.getPolicyDefId(), "PSWD_COMPOSITION");
        LoginEntity lg = login;
        if (lg == null) {
            lg = loginDao.getRecord(password.getPrincipal(), password.getManagedSysId());
        }
        UserEntity usr = user;
        if (usr == null) {
            usr = userDao.findById(lg.getUserId());
        }
        if (defParam != null) {
            for (PolicyDefParamEntity param : defParam) {
                if (policyToCheck(param.getId(), pswdPolicy)) {
                    AbstractPasswordRule rule = null;
                    String strRule = param.getPolicyParamHandler();
                    if (strRule != null && strRule.length() > 0) {
                        log.info("StrRule:" + strRule);
                        if (param.getHandlerLanguage() == null || param.getHandlerLanguage().equalsIgnoreCase("java")) {
                            try {
                                Class cls = Class.forName(strRule);
                                rule = (AbstractPasswordRule) cls.newInstance();
                            } catch (Exception c) {
                                log.info("Error creating object: " + strRule, c);
                                throw new ObjectNotFoundException();
                            }
                        } else {
                            rule = (AbstractPasswordRule) scriptRunner.instantiateClass(
                                    null, strRule);
                        }
                        rule.setSkipPasswordFrequencyCheck(password.isSkipPasswordFrequencyCheck());
                        rule.setPassword(password.getPassword());
                        rule.setPrincipal(password.getPrincipal());
                        rule.setManagedSysId(password.getManagedSysId());
                        rule.setUser(usr);
                        rule.setLg(lg);
                        rule.setPolicy(pswdPolicy);

                        if(rule instanceof PasswordHistoryRule){
                            PasswordHistoryRule pwdHistRule = (PasswordHistoryRule)rule;
                            pwdHistRule.setPasswordHistoryDao(passwordHistoryDao);
                            pwdHistRule.setCryptor(cryptor);
                            pwdHistRule.setKeyManagementService(keyManagementService);
                        }

                        rules.add(rule);
                    }
                }
            }
        }
        return rules;
    }

    private boolean policyToCheck(String defParamId, Policy pswdPolicy) {
        final Set<PolicyAttribute> attrSet = pswdPolicy.getPolicyAttributes();
        final Iterator<PolicyAttribute> atrIt = attrSet.iterator();
        while (atrIt.hasNext()) {
            final PolicyAttribute atr = atrIt.next();
            if (StringUtils.equals(atr.getDefParamId(), defParamId)) {
                return true;
            }
        }
        return false;
    }
}
