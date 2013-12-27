package org.openiam.connector.gapps.command.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openiam.connector.common.command.AbstractCommand;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractGoogleAppsCommand<Request extends RequestType, Response extends ResponseType>
        extends AbstractCommand<Request, Response> {
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;

    protected ExtensibleUser googleToExtensibleAttributes(
            Map<String, String> googleUser) {
        ExtensibleUser user = new ExtensibleUser();
        user.setAttributes(new ArrayList<ExtensibleAttribute>());
        for (String key : googleUser.keySet()) {
            user.getAttributes().add(
                    new ExtensibleAttribute(key, googleUser.get(key)));
            if ("userEmail".equals(key)) {
                String email = googleUser.get(key);
                String[] strs = email.split("@");
                if (strs != null && strs.length > 1) {
                    user.setObjectId(strs[0]);
                }
                user.getAttributes().add(
                        new ExtensibleAttribute("login", strs[0]));
            }
        }
        return user;
    }

    protected Map<String, String> extensibleUserToGoogle(ExtensibleObject user,
            String id, String domain) {
        Map<String, String> googleUser = new HashMap<String, String>();
        for (ExtensibleAttribute a : user.getAttributes()) {
            googleUser.put(a.getName(), a.getValue());
        }
        googleUser.put("userEmail", id.toLowerCase() + "@" + domain);
        return googleUser;
    }
}
