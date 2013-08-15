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
import org.openiam.idm.srvc.synch.service.IdentitySynchService;
import org.openiam.idm.srvc.synch.service.PolicyMapTransformScript;
import org.openiam.idm.srvc.synch.service.TransformScript;
import org.openiam.idm.srvc.synch.service.ValidationScript;
import org.openiam.idm.srvc.synch.ws.IdentitySynchWebService;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.SpringContextProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

	public static List<TransformScript> createTransformationScript(SynchConfig config) throws ClassNotFoundException, IOException {

        LinkedList<TransformScript> scripts = new LinkedList<TransformScript>();

		if (config.getUsePolicyMap()) {
            AttributeMapDozerConverter attributeMapDozerConverter =
                    (AttributeMapDozerConverter)SpringContextProvider.getBean("attributeMapDozerConverter");
            IdentitySynchService synchService = (IdentitySynchService)SpringContextProvider.getBean("synchService");
            List<AttributeMapEntity> attrMapEntity =
                    synchService.getSynchConfigAttributeMaps(config.getSynchConfigId());
            List<AttributeMap> attrMap = attributeMapDozerConverter.convertToDTOList(attrMapEntity, true);

            TransformScript transformScript = new PolicyMapTransformScript(attrMap);
            transformScript.setApplicationContext(SpringContextProvider.getApplicationContext());
            scripts.add(transformScript);

        }
        if (config.getUseTransformationScript() && StringUtils.isNotBlank(config.getTransformationRule())) {
            if (config.getPolicyMapBeforeTransformation()) {
                scripts.addLast((TransformScript)createScript(config.getTransformationRule()));
            } else {
                scripts.addFirst((TransformScript)createScript(config.getTransformationRule()));
            }
        }

        return scripts;
	}
}
