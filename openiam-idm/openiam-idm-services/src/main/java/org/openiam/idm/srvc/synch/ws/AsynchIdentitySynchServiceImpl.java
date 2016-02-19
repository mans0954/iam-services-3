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
import org.openiam.idm.srvc.synch.dto.BulkMigrationConfig;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.dto.SynchReviewRequest;
import org.openiam.util.MuleContextProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.HashMap;
import java.util.Map;

/**
 * @author suneet
 */
@WebService(endpointInterface = "org.openiam.idm.srvc.synch.ws.AsynchIdentitySynchService",
        targetNamespace = "http://www.openiam.org/service/synch",
        portName = "AsynchIdentitySynchServicePort",
        serviceName = "AsynchIdentitySynchService")
@Component("asynchSynchServiceWS")
public class AsynchIdentitySynchServiceImpl implements AsynchIdentitySynchService {

    protected ApplicationContext applicationContext;

    protected static final Log log = LogFactory.getLog(AsynchIdentitySynchServiceImpl.class);

    @Value("${openiam.service_base}")
    private String serviceHost;
    
    @Value("${openiam.idm.ws.path}")
    private String serviceContext;

    public void startSynchronization(
            SynchConfig config) {

        //	MuleMessage msg = null;

    	if(log.isDebugEnabled()) {
    		log.debug("A-START SYNCH CALLED...................");
    	}
        try {

            Map<String, String> msgPropMap = new HashMap<String, String>();
            msgPropMap.put("SERVICE_HOST", serviceHost);
            msgPropMap.put("SERVICE_CONTEXT", serviceContext);

            //Create the client with the context
            MuleClient client = new MuleClient(MuleContextProvider.getCtx());
            client.sendAsync("vm://synchronizationMessage", (SynchConfig) config, msgPropMap);

        } catch (Exception e) {
        	if(log.isDebugEnabled()) {
        		log.debug("EXCEPTION:AsynchIdentitySynchService:startSynchronization");
        	}
            log.error(e);
            //e.printStackTrace();
        }
        if(log.isDebugEnabled()) {
        	log.debug("A-START SYNCH END ---------------------");
        }
    }

    @Override
    public void executeSynchReview(
            SynchReviewRequest synchReviewRequest) {
    	if(log.isDebugEnabled()) {
    		log.debug("START SYNCH REVIEW CALLED...................");
    	}
        try {

            Map<String, String> msgPropMap = new HashMap<String, String>();
            msgPropMap.put("SERVICE_HOST", serviceHost);
            msgPropMap.put("SERVICE_CONTEXT", serviceContext);

            //Create the client with the context
            MuleClient client = new MuleClient(MuleContextProvider.getCtx());
            client.sendAsync("vm://synchronizationReviewMessage", (SynchReviewRequest) synchReviewRequest, msgPropMap);

        } catch (Exception e) {
        	if(log.isDebugEnabled()) {
        		log.debug("EXCEPTION:AsynchIdentitySynchService:executeSynchReview");
        	}
            log.error(e);
            //e.printStackTrace();
        }
        if(log.isDebugEnabled()) {
        	log.debug("FINISHED SYNCH REVIEW ---------------------");
        }

    }

    @Override
    public void bulkUserMigration(BulkMigrationConfig config) {
        try {

            Map<String, String> msgPropMap = new HashMap<String, String>();
            msgPropMap.put("SERVICE_HOST", serviceHost);
            msgPropMap.put("SERVICE_CONTEXT", serviceContext);

            //Create the client with the context
            MuleClient client = new MuleClient(MuleContextProvider.getCtx());
            client.sendAsync("vm://bulkUserMigrationMessage", (BulkMigrationConfig) config, msgPropMap);

        } catch (Exception e) {
        	if(log.isDebugEnabled()) {
        		log.debug("EXCEPTION:AsynchIdentitySynchService:bulkUserMigration");
        	}
            log.error(e);
        }
    }

    @Override
    public void resynchRole(final String roleId) {
        try {

            Map<String, String> msgPropMap = new HashMap<String, String>();
            msgPropMap.put("SERVICE_HOST", serviceHost);
            msgPropMap.put("SERVICE_CONTEXT", serviceContext);

            //Create the client with the context
            MuleClient client = new MuleClient(MuleContextProvider.getCtx());
            client.sendAsync("vm://resynchRoleMessage", roleId, msgPropMap);

        } catch (Exception e) {
        	if(log.isDebugEnabled()) {
        		log.debug("EXCEPTION:AsynchIdentitySynchService:resynchRole");
        	}
            log.error(e);
        }
    }

}
