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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.synch.dto.Attribute;
import org.openiam.idm.srvc.synch.dto.LineObject;
import org.openiam.idm.srvc.synch.dto.SyncResponse;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
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

	public SyncResponse startSynch(SynchConfig config, AuditLogBuilder auditLogBuilder) {

		// rule used to match object from source system to data in IDM
		MatchObjectRule matchRule = null;
		String changeLog = null;
		Date mostRecentRecord = null;

		log.debug("WS SYNCH STARTED ^^^^^^^^");

        String requestId = UUIDGen.getUUID();

        /*
        IdmAuditLog synchStartLog = new IdmAuditLog();
        synchStartLog.setSynchAttributes("SYNCH_USER", config.getSynchConfigId(), "START", "SYSTEM", requestId);
        synchStartLog = auditHelper.logEvent(synchStartLog);
		*/
		try {
			matchRule = matchRuleFactory.create(config.getCustomMatchRule());
		} catch(ClassNotFoundException cnfe) {
			log.error(cnfe);
			/*
            synchStartLog.updateSynchAttributes("FAIL",ResponseCode.CLASS_NOT_FOUND.toString() , cnfe.toString());
            auditHelper.logEvent(synchStartLog);
			*/
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
            return resp;
		}

		Timestamp lastExec = null;
        boolean incremental = false;
		
		if (config.getLastExecTime() != null) {
			lastExec = new Timestamp( config.getLastExecTime().getTime() ) ;
		}
        if (config.getSynchType().equalsIgnoreCase("INCREMENTAL") && (lastExec != null)) {
            incremental = true;
	    }

		try {
            WSOperationCommand serviceCmd = getServiceCommand(  config.getWsScript() );
            if (serviceCmd == null) {
                SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
                return resp;
            }
            List<LineObject> lineObjectList =  serviceCmd.execute(config);

            for (LineObject rowObj :  lineObjectList) {
			//while ( rs.next()) {
                log.debug("-SYNCHRONIZING NEW RECORD ---" );
				// make sure we have a new object for each row

			//	LineObject rowObj = rowHeader.copy();
			//	DatabaseUtil.populateRowObject(rowObj, rs, changeLog);
				
			//	log.debug(" - Record update time=" + rowObj.getLastUpdate());
				
				if (mostRecentRecord == null) {
					mostRecentRecord = rowObj.getLastUpdate();
				}else {
					// if current record is newer than what we saved, then update the most recent record value
					
					if (mostRecentRecord.before(rowObj.getLastUpdate())) {
						log.debug("- MostRecentRecord value updated to=" + rowObj.getLastUpdate());
						mostRecentRecord.setTime(rowObj.getLastUpdate().getTime());
					}
				}
				
				// start the synch process 
				// 1) Validate the data
				// 2) Transform it
				// 3) if not delete - then match the object and determine if its a new object or its an udpate
				try {
					// validate
					if (config.getValidationRule() != null && config.getValidationRule().length() > 0) {
						ValidationScript script = SynchScriptFactory.createValidationScript(config.getValidationRule());
						int retval = script.isValid( rowObj );
						if (retval == ValidationScript.NOT_VALID ) {
							log.debug("Validation failed...");
							// log this object in the exception log
						}
						if (retval == ValidationScript.SKIP) {
							continue;
						}
					}
					
					// check if the user exists or not
					Map<String, Attribute> rowAttr = rowObj.getColumnMap();					
					//
					matchRule =  matchRuleFactory.create(config.getCustomMatchRule());
					User usr = matchRule.lookup(config, rowAttr);

					// transform
                    int retval = -1;
                    ProvisionUser pUser = new ProvisionUser();
                    List<TransformScript> transformScripts =  SynchScriptFactory.createTransformationScript(config);
                    if (transformScripts != null && transformScripts.size() > 0) {

                        for (TransformScript transformScript : transformScripts) {
                            // initialize the transform script
                            if (usr != null) {
                                transformScript.setNewUser(false);
                                User u = userManager.getUserDto(usr.getId());
                                pUser = new ProvisionUser(u);
                                setCurrentSuperiors(pUser);
                                transformScript.setUser(u);
                                transformScript.setPrincipalList(loginDozerConverter.convertToDTOList(loginManager.getLoginByUser(usr.getId()), false));
                                transformScript.setUserRoleList(roleDataService.getUserRolesAsFlatList(usr.getId()));

                            } else {
                                transformScript.setNewUser(true);
                                transformScript.setUser(null);
                                transformScript.setPrincipalList(null);
                                transformScript.setUserRoleList(null);
                            }
                            pUser.setSkipPreprocessor(true);
                            pUser.setSkipPostProcessor(true);
                            retval = transformScript.execute(rowObj, pUser);

                            log.debug("- Transform result=" + retval);
                        }

                        // show the user object
                        log.debug("- User After Transformation =" + pUser);
                        log.debug("- User = " + pUser.getId() + "-" + pUser.getFirstName() + " " + pUser.getLastName());
                        log.debug("- User Attributes = " + pUser.getUserAttributes());
                        /*
						pUser.setSessionId(synchStartLog.getSessionId());
						*/
						if (retval == TransformScript.DELETE && usr != null) {
							log.debug("deleting record - " + usr.getId());
							ProvisionUserResponse userResp = provService.deleteByUserId(usr.getId(), UserStatusEnum.DELETED, systemAccount);

						} else {
							// call synch

							if (retval != TransformScript.DELETE) {

                                log.debug("-Provisioning user=" + pUser.getLastName());

								if (usr != null) {
									log.debug("-updating existing user...systemId=" + pUser.getId());
									pUser.setId(usr.getId());

                                    modifyUser(pUser);
									
								} else {
									log.debug("-adding new user...");

									pUser.setId(null);
                                    addUser(pUser);
								}
							}
						}
					}

				} catch(ClassNotFoundException cnfe) {
					log.error(cnfe);
					/*
                    synchStartLog.updateSynchAttributes("FAIL",ResponseCode.CLASS_NOT_FOUND.toString() , cnfe.toString());
                    auditHelper.logEvent(synchStartLog);
					*/
					SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
					resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
                    resp.setErrorText(cnfe.toString());

					return resp;

				} catch (IOException fe ) {

                    log.error(fe);
                    /*
                    synchStartLog.updateSynchAttributes("FAIL",ResponseCode.FILE_EXCEPTION.toString() , fe.toString());
                    auditHelper.logEvent(synchStartLog);
					*/
                    SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
                    resp.setErrorCode(ResponseCode.FILE_EXCEPTION);
                    resp.setErrorText(fe.toString());

                    return resp;
                }
			}
						
		} catch(Exception se) {
			log.error(se);
			/*
            synchStartLog.updateSynchAttributes("FAIL",ResponseCode.SYNCHRONIZATION_EXCEPTION.toString() , se.toString());
            auditHelper.logEvent(synchStartLog);
			*/
			SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
			resp.setErrorCode(ResponseCode.SQL_EXCEPTION);
			resp.setErrorText(se.toString());
			return resp;
		} finally {
			// mark the end of the synch
			/*
			IdmAuditLog synchEndLog = new IdmAuditLog();
			synchEndLog.setSynchAttributes("SYNCH_USER", config.getSynchConfigId(), "END", "SYSTEM", synchStartLog.getSessionId());
			auditHelper.logEvent(synchEndLog);
			*/			
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
