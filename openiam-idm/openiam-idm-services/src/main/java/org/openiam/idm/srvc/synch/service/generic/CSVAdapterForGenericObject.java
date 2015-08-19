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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.csv.CSVStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.SynchReviewDozerConverter;
import org.openiam.idm.parser.csv.CSVHelper;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;
import org.openiam.idm.srvc.synch.domain.SynchReviewRecordEntity;
import org.openiam.idm.srvc.synch.domain.SynchReviewRecordValueEntity;
import org.openiam.idm.srvc.synch.dto.*;
import org.openiam.idm.srvc.synch.service.SourceAdapter;
import org.openiam.idm.srvc.synch.service.ValidationScript;
import org.openiam.idm.srvc.synch.srcadapter.MatchRuleFactory;
import org.openiam.idm.srvc.synch.srcadapter.SynchScriptFactory;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
   
    @Value("${org.openiam.idm.system.user.id}")
    private String systemAccount;
    @Autowired
    MatchRuleFactory matchRuleFactory;
    @Autowired
    protected SynchReviewDozerConverter synchReviewDozerConverter;

    private Map<String, Object> attributeMap = new HashMap<String, Object>();

    private static final Log log = LogFactory
            .getLog(CSVAdapterForGenericObject.class);

    @Override
    public SyncResponse startSynch(SynchConfig config) {
        return startSynch(config, null, null);
    }

    @Override
    public SyncResponse startSynch(SynchConfig config, SynchReviewEntity sourceReview, SynchReviewEntity resultReview) {

        log.debug("Starting to Sync CSV File..^^^^^^^^");

        File file = new File(config.getFileName());
        InputStream input = null;

        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();

            log.error(fe);
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

                            SynchReview review = null;
                            if (sourceReview != null) {
                                review = synchReviewDozerConverter.convertToDTO(sourceReview, false);
                            }

                            ValidationScript script = SynchScriptFactory
                                    .createValidationScript(config, review);
                            int retval = script.isValid(rowObj);
                            if (retval == ValidationScript.NOT_VALID) {
                                log.debug("Validation failed...");
                                // log this object in the exception log
                            }
                            if (retval == ValidationScript.SKIP) {
                                continue;
                            } else if (retval == ValidationScript.SKIP_TO_REVIEW) {
                                if (resultReview != null) {
                                    resultReview.addRecord(generateSynchReviewRecord(rowObj));
                                }
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
                        SyncResponse resp = new SyncResponse(
                                ResponseStatus.FAILURE);
                        resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
                        return resp;
                    }

                }

            }

        } catch (IOException io) {

            io.printStackTrace();
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.IO_EXCEPTION);
            return resp;

        } finally {

            if (resultReview != null) {
                if (CollectionUtils.isNotEmpty(resultReview.getReviewRecords())) { // add header row
                    resultReview.addRecord(generateSynchReviewRecord(rowHeader, true));
                }
            }

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

    protected SynchReviewRecordEntity generateSynchReviewRecord(LineObject rowObj) {
        return generateSynchReviewRecord(rowObj, false);
    }

    protected SynchReviewRecordEntity generateSynchReviewRecord(LineObject rowObj, boolean isHeader) {
        if (rowObj != null) {
            SynchReviewRecordEntity record = new SynchReviewRecordEntity();
            record.setHeader(isHeader);
            Map<String, Attribute> columnsMap = rowObj.getColumnMap();
            for (String key : columnsMap.keySet()) {
                SynchReviewRecordValueEntity reviewValue = new SynchReviewRecordValueEntity();
                if (!isHeader) {
                    Attribute attribute = columnsMap.get(key);
                    if (attribute != null) {
                        reviewValue.setValue(attribute.getValue());
                    }
                } else {
                    reviewValue.setValue(key);
                }
                record.addValue(reviewValue);
            }
            return record;
        }
        return null;
    }

    @Override
    public void setAttributeMap(Map attributeMap) {
        this.attributeMap = attributeMap;
    }
}
