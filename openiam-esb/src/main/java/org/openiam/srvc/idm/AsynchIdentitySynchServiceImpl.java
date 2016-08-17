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
import org.openiam.idm.srvc.synch.dto.BulkMigrationConfig;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.dto.SynchReviewRequest;
import org.openiam.idm.srvc.synch.service.AsynchIdentitySynchDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.jws.WebService;

/**
 * @author suneet
 */
@WebService(endpointInterface = "org.openiam.srvc.idm.AsynchIdentitySynchService",
        targetNamespace = "http://www.openiam.org/service/synch",
        portName = "AsynchIdentitySynchServicePort",
        serviceName = "AsynchIdentitySynchService")
@Component("asynchSynchServiceWS")
public class AsynchIdentitySynchServiceImpl implements AsynchIdentitySynchService {

    protected ApplicationContext applicationContext;

    protected static final Log log = LogFactory.getLog(AsynchIdentitySynchServiceImpl.class);

    @Autowired
    private AsynchIdentitySynchDataService identitySynchService;

    @Value("${openiam.service_base}")
    private String serviceHost;
    
    @Value("${openiam.idm.ws.path}")
    private String serviceContext;


    public void startSynchronization(final SynchConfig config) {
        identitySynchService.startSynchronization(config);
    }

    public void startCustomSynchronization(final SynchConfig config, final String additionalValues) {
        identitySynchService.startCustomSynchronization(config, additionalValues);
    }

    @Override
    public void executeSynchReview(final SynchReviewRequest synchReviewRequest) {
        identitySynchService.executeSynchReview(synchReviewRequest);
    }

    @Override
    public void bulkUserMigration(final BulkMigrationConfig config) {
        identitySynchService.bulkUserMigration(config);
    }

    @Override
    public void resynchRole(final String roleId) {
        identitySynchService.resynchRole(roleId);
    }

}
