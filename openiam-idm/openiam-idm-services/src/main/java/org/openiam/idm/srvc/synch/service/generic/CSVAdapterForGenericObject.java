/*
 * Copyright 2009, OpenIAM LLC This file is part of the OpenIAM Identity and
 * Access Management Suite
 * 
 * OpenIAM Identity and Access Management Suite is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License version 3 as published by the Free Software Foundation.
 * 
 * OpenIAM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the Lesser GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenIAM. If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.idm.srvc.synch.service.generic;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.parser.csv.CSVHelper;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.synch.dto.Attribute;
import org.openiam.idm.srvc.synch.dto.LineObject;
import org.openiam.idm.srvc.synch.dto.SyncResponse;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.service.SourceAdapter;
import org.openiam.idm.srvc.synch.service.ValidationScript;
import org.openiam.idm.srvc.synch.srcadapter.MatchRuleFactory;
import org.openiam.idm.srvc.synch.srcadapter.SynchScriptFactory;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Reads a CSV file for use during the synchronization process
 * @author suneet
 *
 */
@Component("genericObjCsvAdapter")
public class CSVAdapterForGenericObject implements SourceAdapter {
    @Autowired
    ObjectAdapterMap adapterMap;
    protected LineObject rowHeader = new LineObject();
    protected ProvisionUser pUser = new ProvisionUser();
    @Autowired
    protected LoginDataService loginManager;
    @Autowired
    protected RoleDataService roleDataService;
    @Autowired
    protected UserDataService userMgr;
    @Autowired
    @Qualifier("systemAccount")
    String systemAccount;
    @Autowired
    MatchRuleFactory matchRuleFactory;

    private static final Log log = LogFactory
            .getLog(CSVAdapterForGenericObject.class);

    public SyncResponse startSynch(SynchConfig config) {

        log.debug("Starting to Sync CSV File..^^^^^^^^");

        String requestId = UUIDGen.getUUID();
        /*
        IdmAuditLog synchStartLog = new IdmAuditLog();
        synchStartLog.setSynchAttributes("SYNCH_GENERIC_OBJECT",
                config.getSynchConfigId(), "START", "SYSTEM", requestId);
        synchStartLog = auditHelper.logEvent(synchStartLog);
		*/
        /*
         * MatchObjectRule matchRule = null; provService =
         * (ProvisionService)ac.getBean("defaultProvision");
         * 
         * try { matchRule = matchRuleFactory.create(config);
         * }catch(ClassNotFoundException cnfe) { log.error(cnfe);
         * 
         * cnfe.printStackTrace();
         * 
         * synchStartLog.updateSynchAttributes("FAIL",ResponseCode.CLASS_NOT_FOUND
         * .toString() , cnfe.toString()); auditHelper.logEvent(synchStartLog);
         * 
         * 
         * SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
         * resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND); return resp; }
         */

        File file = new File(config.getFileName());
        InputStream input = null;

        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();

            log.error(fe);
            /*
            synchStartLog.updateSynchAttributes("FAIL",
                    ResponseCode.FILE_EXCEPTION.toString(), fe.toString());
            auditHelper.logEvent(synchStartLog);
			*/
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FILE_EXCEPTION);
            return resp;

        }

        try {
            CSVHelper parser = new CSVHelper(input, CSVStrategy.EXCEL_STRATEGY);
            String[][] fileContentAry = parser.getAllValues();

            int ctr = 0;
            for (String[] lineAry : fileContentAry) {
                log.debug("File Row #= " + lineAry[0]);

                if (ctr == 0) {
                    populateTemplate(lineAry);
                    ctr++;
                } else {
                    // populate the data object
                    pUser = new ProvisionUser();

                    LineObject rowObj = rowHeader.copy();
                    populateRowObject(rowObj, lineAry);

                    try {

                        // validate
                        if (config.getValidationRule() != null
                                && config.getValidationRule().length() > 0) {
                            ValidationScript script = SynchScriptFactory
                                    .createValidationScript(config
                                            .getValidationRule());
                            int retval = script.isValid(rowObj);
                            if (retval == ValidationScript.NOT_VALID) {
                                log.debug("Validation failed...");
                                // log this object in the exception log
                            }
                            if (retval == ValidationScript.SKIP) {
                                continue;
                            }
                        }

                        System.out.println("Getting column map...");

                        // check if the user exists or not
                        Map<String, Attribute> rowAttr = rowObj.getColumnMap();

                        //

                        // show the user object

                    } catch (ClassNotFoundException cnfe) {
                        log.error(cnfe);
                        /*
                        synchStartLog.updateSynchAttributes("FAIL",
                                ResponseCode.CLASS_NOT_FOUND.toString(),
                                cnfe.toString());
                        auditHelper.logEvent(synchStartLog);
						*/
                        SyncResponse resp = new SyncResponse(
                                ResponseStatus.FAILURE);
                        resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
                        return resp;
                    }

                }

            }

        } catch (IOException io) {

            io.printStackTrace();
            /*
            synchStartLog.updateSynchAttributes("FAIL",
                    ResponseCode.IO_EXCEPTION.toString(), io.toString());
            auditHelper.logEvent(synchStartLog);
			*/
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.IO_EXCEPTION);
            return resp;

        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        log.debug("CSV SYNCHRONIZATION COMPLETE^^^^^^^^");

        SyncResponse resp = new SyncResponse(ResponseStatus.SUCCESS);
        return resp;

    }

    public Response testConnection(SynchConfig config) {
        File file = new File(config.getFileName());
        FileReader reader = null;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();

            log.error(fe);

            Response resp = new Response(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FILE_EXCEPTION);
            resp.setErrorText(fe.getMessage());
            return resp;

        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    // This can safely be ignored. The file was opened
                    // successfully at this point.
                }
        }
        Response resp = new Response(ResponseStatus.SUCCESS);
        return resp;
    }

    private void populateTemplate(String[] lineAry) {
        Map<String, Attribute> columnMap = new HashMap<String, Attribute>();

        int ctr = 0;
        for (String s : lineAry) {
            Attribute a = new Attribute(s, null);
            a.setType("STRING");
            a.setColumnNbr(ctr);
            columnMap.put(a.getName(), a);
            ctr++;
        }
        rowHeader.setColumnMap(columnMap);
    }

    private void populateRowObject(LineObject rowObj, String[] lineAry) {
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        Map<String, Attribute> attrMap = rowObj.getColumnMap();
        Set<String> keySet = attrMap.keySet();
        Iterator<String> it = keySet.iterator();

        while (it.hasNext()) {
            String key = it.next();
            Attribute attr = rowObj.get(key);
            int colNbr = attr.getColumnNbr();
            String colValue = lineAry.length > colNbr ? lineAry[colNbr] : "";

            attr.setValue(colValue);
        }

    }

    public LoginDataService getLoginManager() {
        return loginManager;
    }

    public void setLoginManager(LoginDataService loginManager) {
        this.loginManager = loginManager;
    }

    public RoleDataService getRoleDataService() {
        return roleDataService;
    }

    public void setRoleDataService(RoleDataService roleDataService) {
        this.roleDataService = roleDataService;
    }

    public UserDataService getUserMgr() {
        return userMgr;
    }

    public void setUserMgr(UserDataService userMgr) {
        this.userMgr = userMgr;
    }

    public String getSystemAccount() {
        return systemAccount;
    }

    public void setSystemAccount(String systemAccount) {
        this.systemAccount = systemAccount;
    }

    public MatchRuleFactory getMatchRuleFactory() {
        return matchRuleFactory;
    }

    public void setMatchRuleFactory(MatchRuleFactory matchRuleFactory) {
        this.matchRuleFactory = matchRuleFactory;
    }

    public ObjectAdapterMap getAdapterMap() {
        return adapterMap;
    }

    public void setAdapterMap(ObjectAdapterMap adapterMap) {
        this.adapterMap = adapterMap;
    }
}
