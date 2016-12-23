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
import org.openiam.concurrent.OpenIAMRunnable;
import org.openiam.idm.srvc.prov.request.dto.BulkOperationRequest;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.util.concurrent.Executors;

/**
 * @author suneet
 */
@Component("asynchProvisonDataService")
public class AsynchUserProvisioningDataServiceImpl implements AsynchUserProvisionDataService {

    protected static final Log log = LogFactory.getLog(AsynchUserProvisioningDataServiceImpl.class);
    
    @Autowired
    protected ProvisioningDataService provisionService;

    /* (non-Javadoc)
      * @see org.openiam.srvc.idm.ProvisionService#addUser(org.openiam.provision.dto.ProvisionUser)
      */
    @Override
    public void addUser(final ProvisionUser user) {
    	if(log.isDebugEnabled()) {
    		log.debug("START PROVISIONING - ADD USER CALLED...................");
    	}
    	Executors.newSingleThreadExecutor().execute(
	        new OpenIAMRunnable(() -> {
                try {
                    provisionService.addUser(user);
                } catch (Throwable e) {
                    log.error("EXCEPTION:AsynchUserProvisionService.addUser", e);
                }
	        }, user)
    	);
    	if(log.isDebugEnabled()) {
    		log.debug("END PROVISIONING - ADD USER ---------------------");
    	}
    }
    /* (non-Javadoc)
      * @see org.openiam.srvc.idm.ProvisionService#modifyUser(org.openiam.provision.dto.ProvisionUser)
      */
        @Override
        public void modifyUser (ProvisionUser user){
            if (log.isDebugEnabled()) {
                log.debug("START PROVISIONING - MODIFY USER CALLED...................");
            }
            Executors.newSingleThreadExecutor().execute(
    	        new OpenIAMRunnable(() -> {
                    try {
                    	provisionService.modifyUser(user);
                    } catch (Throwable e) {
                        log.error("EXCEPTION:AsynchUserProvisionService.modifyUser", e);
                    }
    	        }, user)
        	);

            if (log.isDebugEnabled()) {
                log.debug("END PROVISIONING - MODIFY USER ---------------------");
            }
        }

        @Override
        public void startBulkOperation ( final BulkOperationRequest bulkRequest){
            if (log.isDebugEnabled()) {
                log.debug("START BULK OPERATION CALLED...................");
            }
            Executors.newSingleThreadExecutor().execute(
    	        new OpenIAMRunnable(() -> {
                    try {
                    	provisionService.startBulkOperation(bulkRequest);
                    } catch (Throwable e) {
                        log.error("EXCEPTION:AsynchUserProvisionService.modifyUser", e);
                    }
    	        }, bulkRequest.getRequesterId(), null)
        	);

            log.debug("END BULK OPERATION CALLED ---------------------");
        }

    }
