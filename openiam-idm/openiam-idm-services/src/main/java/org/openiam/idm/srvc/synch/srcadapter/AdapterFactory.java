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
import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.service.SourceAdapter;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;

/**
 * Factory to create the scripts that are used in the synchronization process.
 * @author suneet
 *
 */
public class AdapterFactory implements ApplicationContextAware, MuleContextAware {

	private static final Log log = LogFactory.getLog(AdapterFactory.class);

	public static ApplicationContext applicationContext;

    protected MuleContext muleContext;
	
	@Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    @Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
	}

    @Override
    public void setMuleContext(MuleContext muleContext) {
        this.muleContext = muleContext;
    }

    public SourceAdapter create(SynchConfig config) throws ClassNotFoundException, IOException {
		SourceAdapter adpt = null;
		
		String adapterType = config.getSynchAdapter();
		String customScript = config.getCustomAdatperScript();
		if (adapterType != null) {
			try {
				if (adapterType.equalsIgnoreCase("CUSTOM") && 
					( adapterType  != null &&  adapterType.length() > 0)) {
					// custom adapter- written groovy
					adpt =  (SourceAdapter)scriptRunner.instantiateClass(null, customScript);
	
				} else {
					// using standard adapter
					Class cls = null;
					if (adapterType.equalsIgnoreCase("RDBMS")) {
						return (SourceAdapter)applicationContext.getBean("rdbmsAdapter");
					}
					if (adapterType.equalsIgnoreCase("CSV")) {
						return (SourceAdapter)applicationContext.getBean("csvAdapter");
					}				
					if (adapterType.equalsIgnoreCase("LDAP")) {
						return (SourceAdapter)applicationContext.getBean("ldapAdapter");
					}
					if (adapterType.equalsIgnoreCase("AD")) {
						return (SourceAdapter)applicationContext.getBean("activeDirAdapter");
					}
                    if (adapterType.equalsIgnoreCase("WS")) {
						return (SourceAdapter)applicationContext.getBean("wsAdapter");
					}
					adpt = (SourceAdapter)cls.newInstance();
				}

				return adpt;
				
			} catch(IllegalAccessException ia) {
				log.error(ia.getMessage(),ia);
				
			} catch(InstantiationException ie) {
				log.error(ie.getMessage(),ie);
			}
		}
		
		return null;
	}

}
