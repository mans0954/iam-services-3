package org.openiam.connector.gapps.command.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.mvel2.optimizers.impl.refl.nodes.ArrayLength;
import org.openiam.connector.common.command.AbstractCommand;
import org.openiam.connector.gapps.GoogleAgent;
import org.openiam.connector.gapps.GooglePopulationScript;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysRuleEntity;
import org.openiam.provision.type.ExtensibleAddress;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.UserAddress;
import com.google.api.services.admin.directory.model.UserName;

public abstract class AbstractGoogleAppsCommand<Request extends RequestType, Response extends ResponseType>
        extends AbstractCommand<Request, Response> {
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;

    protected final static String APPLICATION_NAME = "APPLICATION_NAME";
    protected final static String CLIENT_SECRET = "CLIENT_SECRET";
    protected final static String TRANSFORM_GOOGLE_APPS_SCRIPT = "TRANSFORM_SCRIPT";

    protected Directory getGoogleAppsClient(List<ManagedSysRuleEntity> rules) {
        String applicationName = this.getRuleValue(rules, APPLICATION_NAME);
        String clientSecretPath = this.getRuleValue(rules, CLIENT_SECRET);
        if (applicationName == null || clientSecretPath == null) {
            return null;
        }
        return GoogleAgent.getClient(applicationName, clientSecretPath);
    }

    protected List<ManagedSysRuleEntity> getRules(ManagedSysEntity mSys) {
        List<ManagedSysRuleEntity> rules = mSys.getRules();
        if (rules == null) {
            rules = managedSysService.getRulesByManagedSysId(mSys
                    .getManagedSysId());
        }
        return rules;
    }

    protected String getRuleValue(List<ManagedSysRuleEntity> rules, String key) {
        if (CollectionUtils.isEmpty(rules) || !StringUtils.hasText(key))
            return null;
        for (ManagedSysRuleEntity rule : rules) {
            if (key == rule.getName()) {
                return rule.getValue();
            }
        }
        return null;
    }

    private void toGoogle(User googleUser, ExtensibleUser user) {
        UserName name = new UserName();
        for (ExtensibleAttribute attr : user.getAttributes()) {
            if (attr.getName() == "familyName") {
                name.setFamilyName(attr.getValue());
                continue;
            }
            if (attr.getName() == "fullName") {
                name.setFullName(attr.getValue());
                continue;
            }
            if (attr.getName() == "givenName") {
                name.setGivenName(attr.getValue());
                continue;
            }
            if (attr.getName() == "password") {
                googleUser.setPassword(attr.getValue());
                continue;
            }
        }
        googleUser.setName(name);
        // if (!CollectionUtils.isEmpty(user.getAddress())) {
        // List<UserAddress> addresses = new ArrayList<UserAddress>();
        // for (ExtensibleAddress address : user.getAddress()) {
        // UserAddress googleAddress = new UserAddress();
        // address.getAttributes();
        //
        // }
        // googleUser.setAddresses(addresses);
        // }
    }

    private void fromGoogle(User googleUser, ExtensibleUser user) {
        user.setAttributes(new ArrayList<ExtensibleAttribute>());
        user.getAttributes().add(
                new ExtensibleAttribute("familyName", googleUser.getName()
                        .getFamilyName()));
        user.getAttributes().add(
                new ExtensibleAttribute("fullName", googleUser.getName()
                        .getFullName()));
        user.getAttributes().add(
                new ExtensibleAttribute("givenName", googleUser.getName()
                        .getGivenName()));
        user.getAttributes().add(
                new ExtensibleAttribute("password", googleUser.getPassword()));
    }

    protected void convertToGoogleUser(User googleUser, ExtensibleUser user,
            List<ManagedSysRuleEntity> rules) {
        if (user == null)
            return;
        if (googleUser == null) {
            googleUser = new User();
        }
        GooglePopulationScript script = null;
        String transformScript = this.getRuleValue(rules,
                TRANSFORM_GOOGLE_APPS_SCRIPT);
        try {
            if (StringUtils.hasText(transformScript)) {
                script = (GooglePopulationScript) scriptRunner
                        .instantiateClass(null, transformScript);
                int retval = script.execute(googleUser, user, true);
            } else {
                this.toGoogle(googleUser, user);
            }
        } catch (IOException e1) {
            log.error(e1);
            return;
        }
    }

    protected void convertToExtensibleUser(User googleUser,
            ExtensibleUser user, List<ManagedSysRuleEntity> rules) {
        if (googleUser == null)
            return;
        if (user == null) {
            user = new ExtensibleUser();
        }
        GooglePopulationScript script = null;
        String transformScript = this.getRuleValue(rules,
                TRANSFORM_GOOGLE_APPS_SCRIPT);
        try {
            if (StringUtils.hasText(transformScript)) {
                script = (GooglePopulationScript) scriptRunner
                        .instantiateClass(null, transformScript);
                int retval = script.execute(googleUser, user, false);
            } else {
                this.fromGoogle(googleUser, user);
            }
        } catch (IOException e1) {
            log.error(e1);
            return;
        }
    }
}
