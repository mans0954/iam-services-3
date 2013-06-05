/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the Lesser GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.idm.srvc.synch.srcadapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.synch.service.TransformScript;
import org.openiam.idm.srvc.synch.service.ValidationScript;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Factory to create the scripts that are used in the synchronziation process.
 * @author suneet
 *
 */
public class SynchScriptFactory {

	private static final Log log = LogFactory.getLog(SynchScriptFactory.class);
	
	private static Object createScript(String scriptName) throws ClassNotFoundException, IOException {
		return SpringContextProvider.getBean("configurableGroovyScriptEngine", ScriptIntegration.class).instantiateClass(null, scriptName);
	}
	
	public static ValidationScript createValidationScript(String scriptName) throws ClassNotFoundException, IOException {
		return (ValidationScript)createScript(scriptName);
		
	}

	
	public static TransformScript createTransformationScript(String scriptName) throws ClassNotFoundException, IOException {
		
		return (TransformScript)createScript(scriptName);
		
	}
}
