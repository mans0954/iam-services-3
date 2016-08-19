/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
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
package org.openiam.srvc.idm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.response.SyncResponse;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.service.generic.GenericObjectSynchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;

/**
 * @author suneet
 *
 */
@WebService(endpointInterface = "org.openiam.srvc.idm.GenericObjectSynchWebService",
		targetNamespace = "http://www.openiam.org/service/synch", 
		portName = "GenericObjectSynchWebServicePort",
		serviceName = "GenericObjectSynchWebService")
@Component("genericObjSynchServiceWS")
public class GenericObjectSynchWebServiceImpl implements GenericObjectSynchWebService{
    @Autowired
	protected GenericObjectSynchService synchService;
	protected static final Log log = LogFactory.getLog(GenericObjectSynchWebServiceImpl.class);


    public SyncResponse startSynchronization(SynchConfig config) {
        return synchService.startSynchronization(config);
    }

}
