package org.openiam.connector.gapps.command.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openiam.connector.common.command.AbstractCommand;
import org.openiam.connector.gapps.GoogleUtils;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleGroup;
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
protected final static String ALIAS = "aliasEmail";
	protected ExtensibleUser googleUserToExtensibleAttributes(
			Map<String, String> googleUser) {
		ExtensibleUser user = new ExtensibleUser();
		user.setAttributes(new ArrayList<ExtensibleAttribute>());
		for (String key : googleUser.keySet()) {
			user.getAttributes().add(
					new ExtensibleAttribute(key, googleUser.get(key)));
		}
		return user;
	}

	protected ExtensibleGroup googleGroupToExtensibleAttributes(
			Map<String, String> googleGroup) {
		ExtensibleGroup gr = new ExtensibleGroup();
		gr.setAttributes(new ArrayList<ExtensibleAttribute>());
		for (String key : googleGroup.keySet()) {
			gr.getAttributes().add(
					new ExtensibleAttribute(key, googleGroup.get(key)));
			if ("groupId".equals(key)) {
				String id = googleGroup.get(key);
				String[] strs = id.split("@");
				if (strs != null && strs.length > 1) {
					gr.setObjectId(strs[0]);
				}
			}
		}
		return gr;
	}

	protected Map<String, String> extensibleUserToGoogle(ExtensibleObject user,
			String id, String domain) {
		Map<String, String> googleUser = new HashMap<String, String>();
		for (ExtensibleAttribute a : user.getAttributes()) {
			if (a.getValue() != null && !a.isMultivalued())
				googleUser.put(a.getName(), a.getValue());
		}
		googleUser.put("userEmail",
				GoogleUtils.makeGoogleId(id.toLowerCase(), domain));
		return googleUser;
	}

	protected Map<String, String> extensibleGroupToGoogle(
			ExtensibleGroup group, String id, String domain) {
		Map<String, String> googleUser = new HashMap<String, String>();
		for (ExtensibleAttribute a : group.getAttributes()) {
			googleUser.put(a.getName(), a.getValue());
		}
		return googleUser;
	}
}
