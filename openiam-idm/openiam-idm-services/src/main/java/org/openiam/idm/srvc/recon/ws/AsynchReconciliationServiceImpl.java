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
package org.openiam.idm.srvc.recon.ws;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.module.client.MuleClient;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.service.ReconciliationService;
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
@WebService(endpointInterface = "org.openiam.idm.srvc.recon.ws.AsynchReconciliationService",
		targetNamespace = "http://www.openiam.org/service/recon",
		portName = "AsynchReconciliationWebServicePort",
		serviceName = "AsynchReconciliationWebService")
@Component("asyncReconciliationServiceWS")
public class AsynchReconciliationServiceImpl {

	@Autowired
	protected ReconciliationService reconService;

	protected static final Log log = LogFactory.getLog(AsynchReconciliationServiceImpl.class);
	
	@Value("${openiam.service_base}")
	private String serviceHost;
	
	@Value("${openiam.idm.ws.path}")
	private String serviceContext;
	
	public void startReconciliation(
			ReconciliationConfig config) {
		
	//	MuleMessage msg = null;
		
		if(log.isDebugEnabled()) {
			log.debug("A-RECONCILIATION STARTED.............");
		}
		
		try {
			if(log.isDebugEnabled()) {
				log.debug("MuleContext = " + MuleContextProvider.getCtx());
			}
			

			Map<String,String> msgPropMap =  new HashMap<String,String>(); 
			msgPropMap.put("SERVICE_HOST", serviceHost);
			msgPropMap.put("SERVICE_CONTEXT", serviceContext);

			
			//Create the client with the context
			MuleClient client = new MuleClient(MuleContextProvider.getCtx());
			client.sendAsync("vm://reconciliationMessage", (ReconciliationConfig)config, msgPropMap);


			
		}catch(Exception e) {
			if(log.isDebugEnabled()) {
				log.debug("EXCEPTION:AsynchReconciliationServiceImpl");
			}
			log.error(e);
			//e.printStackTrace();
		}
		if(log.isDebugEnabled()) {
			log.debug("A-RECONCILIATION COMPLETED ---------------------");
		}
	}

}
