package org.openiam.connector.gapps.command.base;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.connector.common.command.AbstractCommand;
import org.openiam.connector.gapps.GoogleUtils;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleGroup;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.gdata.data.appsforyourdomain.generic.GenericEntry;
import com.google.gdata.data.codesearch.File;
import com.jcraft.jsch.jce.Random;

public abstract class AbstractGoogleAppsCommand<Request extends RequestType, Response extends ResponseType>
		extends AbstractCommand<Request, Response> {
	@Autowired
	@Qualifier("configurableGroovyScriptEngine")
	protected ScriptIntegration scriptRunner;
	protected final static String ALIAS = "aliasEmail";
	protected final static String GAM_OPERATION = "GAM_OPERATION_";

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

	protected void runGamCommands(String operation, Resource res, GenericEntry e) {

		String gamLocation = System.getProperty("confpath", "data/openiam").concat("/conf/gam/");
		if (res.getResourceProperty("GAM_LOCATION") != null) {
			gamLocation = res.getResourceProperty("GAM_LOCATION").getValue();
		} else {
			log.info("Use default gam location!");
		}

		if (res == null || StringUtils.isBlank(operation)) {
			log.warn(this.getClass() + " Resource or/and operator are null");
			return;
		}
		Set<ResourceProp> properties = res.getResourceProps();
		Pattern gamOperationPattern = Pattern.compile("(" + GAM_OPERATION
				+ ")[0-9a-zA-Z_]*");

		List<GoogleGamCommand> gamCommands = new ArrayList<GoogleGamCommand>();
		// get All GAMS commands
		for (ResourceProp p : properties) {
			if (gamOperationPattern.matcher(p.getName()).matches()) {
				gamCommands.add(new GoogleGamCommand(p.getValue()));
			}
		}
		Iterator<GoogleGamCommand> gamIter = gamCommands.iterator();
		while (gamIter.hasNext()) {
			GoogleGamCommand c = gamIter.next();
			if (StringUtils.isBlank(c.getCommand())
					|| CollectionUtils.isEmpty(c.getMethods())
					|| !c.getMethods().contains(operation)) {
				gamIter.remove();
			}
		}
		Collections.sort(gamCommands);
		// Run Gam commands
		String format = "python %sgam.py %s";
		Map<String, String> props = e.getAllProperties();
		String runnableFilePath = gamLocation
				+ RandomStringUtils.randomAlphabetic(12) + ".sh";
		java.io.File f = new java.io.File(runnableFilePath);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e1) {
				log.error("Can create file. Please share access! Can't run GAM commands! "
						+ runnableFilePath);
				return;
			}
		}
		for (GoogleGamCommand c : gamCommands) {
			String command = c.getCommand();
			for (String str : props.keySet()) {
				if (props.get(str) != null)
					command = command.replace(str, props.get(str));
			}

			String togo = String.format(format, gamLocation, command);
			try {
				PrintWriter writer = new PrintWriter(f);
				writer.println("#!/bin/bash");
				writer.println(togo);
				writer.close();
				Runtime rt = Runtime.getRuntime();
				Process proc = rt.exec("sh " + runnableFilePath);
				int exitVal = proc.waitFor();
				if (exitVal == 0) {
					log.info("Command: " + togo
							+ " was executed succesfully. RetVal=" + exitVal
							+ "\n Result: "
							+ this.convertStreamToString(proc.getInputStream()));
				} else {
					log.error("Command: " + togo
							+ " was executed with errors. RetVal=" + exitVal
							+ "\n Result: "
							+ this.convertStreamToString(proc.getInputStream()));
				}
			} catch (Exception exf) {
				log.error("Gam execution is broken!" + exf);
			}
		}
		f.delete();
	}

	private String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
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
