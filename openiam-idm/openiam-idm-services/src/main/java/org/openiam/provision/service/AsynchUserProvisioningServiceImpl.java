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
package org.openiam.provision.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.module.client.MuleClient;
import org.openiam.idm.srvc.prov.request.dto.BulkOperationRequest;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.util.MuleContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.util.*;

/**
 * @author suneet
 */
@WebService(endpointInterface = "org.openiam.provision.service.AsynchUserProvisionService",
        targetNamespace = "http://www.openiam.org/service/provision",
        portName = "DefaultProvisionControllerServicePort",
        serviceName = "AsynchUserProvisionService")
@Component("asynchProvisonWS")
public class AsynchUserProvisioningServiceImpl implements AsynchUserProvisionService {

    protected static final Log log = LogFactory.getLog(AsynchUserProvisioningServiceImpl.class);
    @Autowired
    @Qualifier("defaultProvision")
    protected ProvisionService provisionService;

    @Value("${openiam.service_base}")
    private String serviceHost;
    
    @Value("${openiam.idm.ws.path}")
    private String serviceContext;

    /* (non-Javadoc)
      * @see org.openiam.provision.service.ProvisionService#addUser(org.openiam.provision.dto.ProvisionUser)
      */
    @Override
    public void addUser(ProvisionUser user) {
    	if(log.isDebugEnabled()) {
    		log.debug("START PROVISIONING - ADD USER CALLED...................");
    	}
		try {

			Map<String,String> msgPropMap =  new HashMap<String,String>();
			msgPropMap.put("SERVICE_HOST", serviceHost);
			msgPropMap.put("SERVICE_CONTEXT", serviceContext);


			//Create the client with the context
			MuleClient client = new MuleClient(MuleContextProvider.getCtx());
			client.sendAsync("vm://provisionServiceAddMessage", (ProvisionUser)user, msgPropMap);

		}catch(Exception e) {
			if(log.isDebugEnabled()) {
				log.debug("EXCEPTION:AsynchIdentitySynchService");
			}
			log.error(e);
			//e.printStackTrace();
		}
		if(log.isDebugEnabled()) {
			log.debug("END PROVISIONING - ADD USER ---------------------");
		}
    }


    /* (non-Javadoc)
      * @see org.openiam.provision.service.ProvisionService#modifyUser(org.openiam.provision.dto.ProvisionUser)
      */
    @Override
    public void modifyUser(ProvisionUser user) {
    	if(log.isDebugEnabled()) {
    		log.debug("START PROVISIONING - MODIFY USER CALLED...................");
    	}

            try {

                Map<String,String> msgPropMap =  new HashMap<String,String>();
                msgPropMap.put("SERVICE_HOST", serviceHost);
                msgPropMap.put("SERVICE_CONTEXT", serviceContext);


                //Create the client with the context
                MuleClient client = new MuleClient(MuleContextProvider.getCtx());
                client.sendAsync("vm://provisionServiceModifyMessage", (ProvisionUser)user, msgPropMap);

            }catch(Exception e) {
            	if(log.isDebugEnabled()) {
            		log.debug("EXCEPTION:AsynchIdentitySynchService");
            	}
                log.error(e);
                //e.printStackTrace();
            }
            if(log.isDebugEnabled()) {
            	log.debug("END PROVISIONING - MODIFY USER ---------------------");
            }

    }

    @Override
    public void startBulkOperation(BulkOperationRequest bulkRequest) {
    	if(log.isDebugEnabled()) {
    		log.debug("START BULK OPERATION CALLED...................");
    	}

        try {

            Map<String,String> msgPropMap =  new HashMap<String,String>();
            msgPropMap.put("SERVICE_HOST", serviceHost);
            msgPropMap.put("SERVICE_CONTEXT", serviceContext);

            //Create the client with the context
            MuleClient client = new MuleClient(MuleContextProvider.getCtx());
            client.sendAsync("vm://provisionServiceBulkOperationMessage", bulkRequest, msgPropMap);

        }catch(Exception e) {
        	if(log.isDebugEnabled()) {
        		log.debug("EXCEPTION:AsynchIdentitySynchService");
        	}
            log.error(e);
            //e.printStackTrace();
        }
        if(log.isDebugEnabled()) {
        	log.debug("END BULK OPERATION CALLED ---------------------");
        }
    }

}
