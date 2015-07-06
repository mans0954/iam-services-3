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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.AttributeMapDozerConverter;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.dto.SynchReview;
import org.openiam.idm.srvc.synch.service.IdentitySynchService;
import org.openiam.idm.srvc.synch.service.PolicyMapTransformScript;
import org.openiam.idm.srvc.synch.service.TransformScript;
import org.openiam.idm.srvc.synch.service.ValidationScript;
import org.openiam.idm.srvc.synch.ws.IdentitySynchWebService;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.SpringContextProvider;
import java.io.IOException;
import java.util.*;

/**
 * Factory to create the scripts that are used in the synchronziation process.
 * @author suneet
 *
 */
public class SynchScriptFactory {

	private static final Log log = LogFactory.getLog(SynchScriptFactory.class);
	
	private static Object createScript(String scriptName, Map<String, Object> bindingMap) throws ClassNotFoundException, IOException {
		return SpringContextProvider.getBean("configurableGroovyScriptEngine", ScriptIntegration.class).instantiateClass(bindingMap, scriptName);
	}
	
	public static ValidationScript createValidationScript(SynchConfig config, SynchReview review) throws ClassNotFoundException, IOException {
        Map<String, Object> bindingMap = new HashMap<String, Object>();
        if (config != null) {
            bindingMap.put("config", config);
        }
        if (review != null) {
            bindingMap.put("review", review);
        }
		return (ValidationScript)createScript(config.getValidationRule(), bindingMap);
	}

	public static List<TransformScript> createTransformationScript(SynchConfig config, SynchReview review) throws ClassNotFoundException, IOException {

        Map<String, Object> bindingMap = new HashMap<String, Object>();
        bindingMap.put("config", config);
        bindingMap.put("synchConfigId", config.getId());

        if (review != null) {
            bindingMap.put("review", review);
        }

        LinkedList<TransformScript> scripts = new LinkedList<TransformScript>();

		if (config.getUsePolicyMap()) {
            IdentitySynchWebService synchService = (IdentitySynchWebService)SpringContextProvider.getBean("synchServiceWS");
            List<AttributeMap> attrMap =
                    synchService.getSynchConfigAttributeMaps(config.getId());
            TransformScript transformScript = new PolicyMapTransformScript(attrMap);
            scripts.add(transformScript);

        }
        if (config.getUseTransformationScript() && StringUtils.isNotBlank(config.getTransformationRule())) {
            TransformScript script = (TransformScript)createScript(config.getTransformationRule(), bindingMap);
            if (config.getPolicyMapBeforeTransformation()) {
                scripts.addLast(script);
            } else {
                scripts.addFirst(script);
            }
        }

        return scripts;
	}
}
