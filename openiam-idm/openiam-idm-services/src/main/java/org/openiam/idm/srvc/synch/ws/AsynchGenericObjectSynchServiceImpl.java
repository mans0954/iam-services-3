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
package org.openiam.idm.srvc.synch.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.module.client.MuleClient;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.util.MuleContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.util.HashMap;
import java.util.Map;


/**
 * @author suneet
 *
 */
@WebService(endpointInterface = "org.openiam.idm.srvc.synch.ws.AsynchGenericObjectSynchService",
		targetNamespace = "http://www.openiam.org/service/synch", 
		portName = "AsynchGenericObjectSynchServicePort",
		serviceName = "AsynchGenericObjectSynchService")
@Component("asynchGenericObjSynchServiceWS")
public class AsynchGenericObjectSynchServiceImpl implements AsynchGenericObjectSynchService {
    @Autowired
	protected GenericObjectSynchWebService synchService;

    protected static final Log log = LogFactory.getLog(AsynchGenericObjectSynchServiceImpl.class);

    @Value("${openiam.service_base}")
    private String serviceHost;
    
    @Value("${openiam.idm.ws.path}")
    private String serviceContext;


    public void startSynchronization(SynchConfig config) {
    	if(log.isDebugEnabled()) {
    		log.debug("A-START SYNCH CALLED...................");
    	}

        try {

            Map<String, Object> msgPropMap = new HashMap<String, Object>();
            msgPropMap.put("SERVICE_HOST", serviceHost);
            msgPropMap.put("SERVICE_CONTEXT", serviceContext);


            //Create the client with the context
            MuleClient client = new MuleClient(MuleContextProvider.getCtx());
            client.sendAsync("vm://genericObjectSynchronizationMessage", (SynchConfig) config, msgPropMap);


        } catch (Exception e) {
        	if(log.isDebugEnabled()) {
        		log.debug("EXCEPTION:AsynchIdentitySynchService");
        	}
            log.error(e);
            //e.printStackTrace();
        }
        if(log.isDebugEnabled()) {
        	log.debug("A-START SYNCH END ---------------------");
        }
    }

}
