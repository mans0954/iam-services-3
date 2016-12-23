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
package org.openiam.idm.srvc.synch.service;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.BulkMigrationConfig;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.base.request.SynchReviewRequest;
import org.openiam.concurrent.OpenIAMRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

/**
 * @author suneet
 */
@Component
public class AsynchIdentitySynchDataServiceImpl implements AsynchIdentitySynchDataService{

    protected ApplicationContext applicationContext;

    protected static final Log log = LogFactory.getLog(AsynchIdentitySynchDataServiceImpl.class);

    @Autowired
    private IdentitySynchService identitySynchService;
    @Autowired
    protected SynchReviewService synchReviewService;

    @Value("${openiam.service_base}")
    private String serviceHost;
    
    @Value("${openiam.idm.ws.path}")
    private String serviceContext;


    public void startSynchronization(
            final SynchConfig config) {


    	if(log.isDebugEnabled()) {
    		log.debug("A-START SYNCH CALLED...................");
    	}
    	
        Executors.newSingleThreadExecutor().execute(new OpenIAMRunnable(() -> {
        	try {
        		identitySynchService.startSynchronization(config);
        	} catch(Throwable e) {
        		log.error("EXCEPTION:AsynchIdentitySynchService:startSynchronization", e);
        	}
    	}, config));
        
        if(log.isDebugEnabled()) {
        	log.debug("A-START SYNCH END ---------------------");
        }
    }

    public void startCustomSynchronization(
            final SynchConfig config, final String additionalValues) {


        log.debug("A-START SYNCH CALLED...................");
            Executors.newSingleThreadExecutor().execute(new OpenIAMRunnable(() -> {
            	try {
            		identitySynchService.startCustomSynchronization(config, additionalValues);
            	} catch(Throwable e) {
            		log.error("EXCEPTION:AsynchIdentitySynchService:startCustomSynchronization", e);
            	}
            }, config));
        if(log.isDebugEnabled()) {
        	log.debug("A-START SYNCH END ---------------------");
        }
    }

    @Override
    public void executeSynchReview(
            final SynchReviewRequest synchReviewRequest) {
    	if(log.isDebugEnabled()) {
    		log.debug("START SYNCH REVIEW CALLED...................");
    	}
        Executors.newSingleThreadExecutor().execute(new OpenIAMRunnable(() -> {
        	try {
        		synchReviewService.executeSynchReview(synchReviewRequest);
        	} catch(Throwable e) {
        		log.error("EXCEPTION:AsynchIdentitySynchService:executeSynchReview", e);
        	}
        }, synchReviewRequest.getRequestorId(), null));
        if(log.isDebugEnabled()) {
        	log.debug("FINISHED SYNCH REVIEW ---------------------");
        }

    }

    @Override
    public void bulkUserMigration(final BulkMigrationConfig config) {
        Executors.newSingleThreadExecutor().execute(new OpenIAMRunnable(() -> {
            try {
                identitySynchService.bulkUserMigration(config);
            } catch(Throwable e) {
            	log.error("EXCEPTION:AsynchIdentitySynchService:bulkUserMigration", e);
            }
        }, config.getRequestorId(), null));
    }

    @Override
    public void resynchRole(final String roleId) {
        Executors.newSingleThreadExecutor().execute(new OpenIAMRunnable(() -> {
            try {
                identitySynchService.resynchRole(roleId);
            } catch(Throwable e) {
            	log.error("EXCEPTION:AsynchIdentitySynchService:resynchRole", e);
            }
        }));
    }

}
