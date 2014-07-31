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
package org.openiam.idm.srvc.synch.srcadapter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;
import org.openiam.idm.srvc.synch.dto.*;
import org.openiam.idm.srvc.synch.service.*;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.openiam.script.ScriptIntegration;
import org.springframework.stereotype.Component;

import java.util.List;

import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.Map;

/**
 * Gets data from a Webservice to use for synchronization
 * @author suneet
 *
 */
@Component
public class WSAdapter extends AbstractSrcAdapter { // implements SourceAdapter

	@Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

	private static final Log log = LogFactory.getLog(WSAdapter.class);

	private Connection con = null;

    private LineObject lineHeader;

    @Override
    public SyncResponse startSynch(final SynchConfig config) {
        return startSynch(config, null, null);
    }

    @Override
    public SyncResponse startSynch(SynchConfig config, SynchReviewEntity sourceReview, SynchReviewEntity resultReview) {

		Date mostRecentRecord = null;

		log.debug("WS SYNCH STARTED ^^^^^^^^");

        SyncResponse res = initializeScripts(config, sourceReview);
        if (ResponseStatus.FAILURE.equals(res.getStatus())) {
            return res;
        }

        if (sourceReview != null && !sourceReview.isSourceRejected()) {
            return startSynchReview(config, sourceReview, resultReview);
        }

		try {
            WSOperationCommand serviceCmd = getServiceCommand(  config.getWsScript() );
            if (serviceCmd == null) {
                SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
                return resp;
            }
            List<LineObject> lineObjectList =  serviceCmd.execute(config);

            if (CollectionUtils.isNotEmpty(lineObjectList)) {
                lineHeader = lineObjectList.get(0);
            }

            for (LineObject rowObj :  lineObjectList) {
                log.debug("-SYNCHRONIZING NEW RECORD ---" );
				if (mostRecentRecord == null) {
					mostRecentRecord = rowObj.getLastUpdate();

				} else {
					// if current record is newer than what we saved, then update the most recent record value
					if (mostRecentRecord.before(rowObj.getLastUpdate())) {
						log.debug("- MostRecentRecord value updated to=" + rowObj.getLastUpdate());
						mostRecentRecord.setTime(rowObj.getLastUpdate().getTime());
					}
				}

                processLineObject(rowObj, config, resultReview);

			}
						
		} catch(Exception se) {

			log.error(se);
			SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
			resp.setErrorCode(ResponseCode.SQL_EXCEPTION);
			resp.setErrorText(se.toString());
			return resp;

		} finally {
            if (resultReview != null) {
                if (CollectionUtils.isNotEmpty(resultReview.getReviewRecords())) { // add header row
                    resultReview.addRecord(generateSynchReviewRecord(lineHeader, true));
                }
            }
		}
		
		log.debug("WS SYNCH COMPLETE.^^^^^^^^");

		SyncResponse resp = new SyncResponse(ResponseStatus.SUCCESS);
		resp.setLastRecordTime(mostRecentRecord);
		return resp;
		
	}

    public Response testConnection(SynchConfig config) {
        WSOperationCommand serviceCmd = getServiceCommand(  config.getWsScript() );
        if (serviceCmd == null) {
            Response resp = new Response(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
            return resp;
        }
        Response resp = new Response(ResponseStatus.SUCCESS);
        return resp;
    }

    private WSOperationCommand getServiceCommand(String scriptName) {

        if (scriptName == null || scriptName.length() == 0) {
            return null;
        }
        try {
            return (WSOperationCommand)scriptRunner.instantiateClass(null, scriptName);

        } catch(Exception e) {
            log.error(e);
            e.printStackTrace();
            return null;
        }
    }
}
