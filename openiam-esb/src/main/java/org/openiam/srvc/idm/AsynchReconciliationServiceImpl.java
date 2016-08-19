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
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.service.ReconciliationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.util.concurrent.Executors;


/**
 * @author suneet
 *
 */
@WebService(endpointInterface = "org.openiam.srvc.idm.AsynchReconciliationService",
		targetNamespace = "http://www.openiam.org/service/recon",
		portName = "AsynchReconciliationWebServicePort",
		serviceName = "AsynchReconciliationWebService")
@Component("asyncReconciliationServiceWS")
public class AsynchReconciliationServiceImpl implements AsynchReconciliationService {

	@Autowired
	protected ReconciliationService reconService;

	protected static final Log log = LogFactory.getLog(AsynchReconciliationServiceImpl.class);
	
	@Value("${openiam.service_base}")
	private String serviceHost;
	
	@Value("${openiam.idm.ws.path}")
	private String serviceContext;
	
	public void startReconciliation(
			final ReconciliationConfig config) {

		log.debug("A-RECONCILIATION STARTED.............");
		
		try {
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                public void run() {
                    reconService.startReconciliation(config);
                    System.out.println("Asynchronous task");
                }
            });
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
