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
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.service.SourceAdapter;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Factory to create the scripts that are used in the synchronization process.
 * @author suneet
 *
 */
@Component
public class AdapterFactory {

	private static final Log log = LogFactory.getLog(AdapterFactory.class);

	@Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    @Autowired
    private CSVAdapter csvAdapter;

    @Autowired
    private RDBMSAdapter rdbmsAdapter;

    @Autowired
    private LdapAdapter ldapAdapter;

    @Autowired
    private WSAdapter wsAdapter;

    @Autowired
    private ActiveDirectoryAdapter activeDirectoryAdapter;

    public SourceAdapter create(SynchConfig config) throws ClassNotFoundException, IOException {

		String adapterType = config.getSynchAdapter();
		String customScript = config.getCustomAdatperScript();
		if (adapterType != null) {
				if (adapterType.equalsIgnoreCase("CUSTOM") &&
					( adapterType  != null &&  adapterType.length() > 0)) {
					// custom adapter- written groovy
					return (SourceAdapter)scriptRunner.instantiateClass(null, customScript);
				} else {
					// using standard adapter
					if (adapterType.equalsIgnoreCase("RDBMS")) {
						return rdbmsAdapter;
					}
					if (adapterType.equalsIgnoreCase("CSV")) {
						return csvAdapter;
					}				
					if (adapterType.equalsIgnoreCase("LDAP")) {
						return ldapAdapter;
					}
					if (adapterType.equalsIgnoreCase("AD")) {
						return activeDirectoryAdapter;
					}
                    if (adapterType.equalsIgnoreCase("WS")) {
						return wsAdapter;
					}
				}

		}

		return null;
	}

}
