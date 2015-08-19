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
import org.openiam.idm.srvc.synch.dto.BulkMigrationConfig;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.dto.SynchReviewRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.util.concurrent.Executors;

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

    @Autowired
    @Qualifier("synchServiceWS")
    private IdentitySynchWebService identitySynchWebService;

    @Value("${openiam.service_base}")
    private String serviceHost;
    
    @Value("${openiam.idm.ws.path}")
    private String serviceContext;


    public void startSynchronization(
            final SynchConfig config) {


        log.debug("A-START SYNCH CALLED...................");
        try {
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                public void run() {
                    identitySynchWebService.startSynchronization(config);
                }
            });
        } catch (Exception e) {
            log.debug("EXCEPTION:AsynchIdentitySynchService:startSynchronization");
            log.error(e);
        }
        log.debug("A-START SYNCH END ---------------------");
    }

    public void startCustomSynchronization(
            final SynchConfig config, final String additionalValues) {


        log.debug("A-START SYNCH CALLED...................");
        try {
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                public void run() {
                    identitySynchWebService.startCustomSynchronization(config, additionalValues);
                }
            });
        } catch (Exception e) {
            log.debug("EXCEPTION:AsynchIdentitySynchService:startCustomSynchronization");
            log.error(e);
        }
        log.debug("A-START SYNCH END ---------------------");
    }

    @Override
    public void executeSynchReview(
            final SynchReviewRequest synchReviewRequest) {
        log.debug("START SYNCH REVIEW CALLED...................");
        try {
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                public void run() {
                    identitySynchWebService.executeSynchReview(synchReviewRequest);
                }
            });
        } catch (Exception e) {
            log.debug("EXCEPTION:AsynchIdentitySynchService:executeSynchReview");
            log.error(e);
        }
        log.debug("FINISHED SYNCH REVIEW ---------------------");

    }

    @Override
    public void bulkUserMigration(final BulkMigrationConfig config) {
        try {

            Executors.newSingleThreadExecutor().execute(new Runnable() {
                public void run() {
                    identitySynchWebService.bulkUserMigration(config);
                }
            });
        } catch (Exception e) {
            log.debug("EXCEPTION:AsynchIdentitySynchService:bulkUserMigration");
            log.error(e);
        }
    }

    @Override
    public void resynchRole(final String roleId) {
        try {

            Executors.newSingleThreadExecutor().execute(new Runnable() {
                public void run() {
                    identitySynchWebService.resynchRole(roleId);
                }
            });
        } catch (Exception e) {
            log.debug("EXCEPTION:AsynchIdentitySynchService:resynchRole");
            log.error(e);
        }
    }

}
