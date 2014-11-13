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
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;
import org.openiam.idm.srvc.synch.dto.*;
import org.openiam.idm.srvc.synch.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.openiam.script.ScriptIntegration;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import java.io.IOException;
import java.sql.*;
import java.util.Date;

/**
 * Gets data from a Webservice to use for synchronization
 *
 * @author suneet
 */
@Component
public class WSAdapter extends AbstractSrcAdapter { // implements SourceAdapter

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    private static final Log log = LogFactory.getLog(WSAdapter.class);

    private Connection con = null;

    @Override
    public SyncResponse startSynch(final SynchConfig config) {
        return startSynch(config, null, null);
    }

    @Override
    public SyncResponse startSynch(SynchConfig config, SynchReviewEntity sourceReview, SynchReviewEntity resultReview) {

        LineObject lineHeader = null;
        Date mostRecentRecord = null;

        log.debug("WS SYNCH STARTED ^^^^^^^^");

        SyncResponse res = new SyncResponse(ResponseStatus.SUCCESS);
        SynchReview review = null;
        if (sourceReview != null) {
            review = synchReviewDozerConverter.convertToDTO(sourceReview, false);
        }
        LineObject rowHeaderForReport = null;
        InputStream input = null;

        try {
            final ValidationScript validationScript = org.mule.util.StringUtils.isNotEmpty(config.getValidationRule()) ? SynchScriptFactory.createValidationScript(config, review) : null;
            final List<TransformScript> transformScripts = SynchScriptFactory.createTransformationScript(config, review);
            final MatchObjectRule matchRule = matchRuleFactory.create(config.getCustomMatchRule()); // check if matchRule exists

            if (validationScript == null || transformScripts == null || matchRule == null) {
                res = new SyncResponse(ResponseStatus.FAILURE);
                res.setErrorText("The problem in initialization of RDBMSAdapter, please check validationScript= " + validationScript + ", transformScripts=" + transformScripts + ", matchRule=" + matchRule + " all must be set!");
                res.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
                return res;
            }


            if (sourceReview != null && !sourceReview.isSourceRejected()) {
                return startSynchReview(config, sourceReview, resultReview, validationScript, transformScripts, matchRule);
            }

            WSOperationCommand serviceCmd = getServiceCommand(config.getWsScript());
            if (serviceCmd == null) {
                SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
                return resp;
            }
            List<LineObject> lineObjectList = serviceCmd.execute(config);

            if (CollectionUtils.isNotEmpty(lineObjectList)) {
                lineHeader = lineObjectList.get(0);
            }

            for (LineObject rowObj : lineObjectList) {
                log.debug("-SYNCHRONIZING NEW RECORD ---");
                if (mostRecentRecord == null) {
                    mostRecentRecord = rowObj.getLastUpdate();

                } else {
                    // if current record is newer than what we saved, then update the most recent record value
                    if (mostRecentRecord.before(rowObj.getLastUpdate())) {
                        log.debug("- MostRecentRecord value updated to=" + rowObj.getLastUpdate());
                        mostRecentRecord.setTime(rowObj.getLastUpdate().getTime());
                    }
                }

                processLineObject(rowObj, config, resultReview, validationScript, transformScripts, matchRule);

            }

        } catch (ClassNotFoundException cnfe) {
            log.error(cnfe);
            res = new SyncResponse(ResponseStatus.FAILURE);
            res.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
            return res;
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
            log.error(fe);
//            auditBuilder.addAttribute(AuditAttributeName.DESCRIPTION, "FileNotFoundException: "+fe.getMessage());
//            auditLogProvider.persist(auditBuilder);
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FILE_EXCEPTION);
            log.debug("WS SYNCHRONIZATION COMPLETE WITH ERRORS ^^^^^^^^");
            return resp;
        } catch (IOException io) {
            io.printStackTrace();
            /*
            synchStartLog.updateSynchAttributes("FAIL", ResponseCode.IO_EXCEPTION.toString(), io.toString());
            auditHelper.logEvent(synchStartLog);
			*/
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.IO_EXCEPTION);
            log.debug("WS SYNCHRONIZATION COMPLETE WITH ERRORS ^^^^^^^^");
            return resp;

        } catch (Exception se) {

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
        WSOperationCommand serviceCmd = getServiceCommand(config.getWsScript());
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
            return (WSOperationCommand) scriptRunner.instantiateClass(null, scriptName);

        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            return null;
        }
    }
}
