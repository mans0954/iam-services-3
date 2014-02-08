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

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.util.StringUtils;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;

import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.audit.service.AuditLogProvider;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.synch.dto.Attribute;
import org.openiam.idm.srvc.synch.dto.LineObject;
import org.openiam.idm.srvc.synch.dto.SyncResponse;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
import org.openiam.idm.srvc.synch.service.SyncConstants;
import org.openiam.idm.srvc.synch.service.TransformScript;
import org.openiam.idm.srvc.synch.service.ValidationScript;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.util.RemoteFileStorageManager;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Reads a CSV file for use during the synchronization process
 *
 * @author suneet
 */
@Component
public class CSVAdapter extends AbstractSrcAdapter {

    private static final Log log = LogFactory.getLog(CSVAdapter.class);
    public static final String SYNC_DIR = "sync";

    // synchronization monitor
    private final Object mutex = new Object();

    @Value("${org.openiam.upload.root}")
    private String uploadRoot;
    
    @Value("${csvadapter.thread.count}")
    private int THREAD_COUNT;
    
    @Value("${csvadapter.thread.delay.beforestart}")
    private int THREAD_DELAY_BEFORE_START;

    @Value("${org.openiam.upload.remote.use}")
    private Boolean useRemoteFilestorage;
    @Autowired
    protected AuditLogService auditLogService;

    @Autowired
    private AuditLogProvider auditLogProvider;

    @Autowired
    private RemoteFileStorageManager remoteFileStorageManager;

    public SyncResponse startSynch(final SynchConfig config, final AuditLogBuilder auditBuilder) {
        log.debug("CSV startSynch CALLED.^^^^^^^^");
        System.out.println("CSV startSynch CALLED.^^^^^^^^");

        auditBuilder.addAttribute(AuditAttributeName.DESCRIPTION, "CSV startSynch CALLED.^^^^^^^^");
        auditLogProvider.persist(auditBuilder);

        InputStreamReader isr = null;

        final ProvisionService provService = (ProvisionService) SpringContextProvider.getBean("defaultProvision");

        String requestId = UUIDGen.getUUID();

        try {
            CSVParser parser;
            String csvFileName = config.getFileName();
            if(useRemoteFilestorage) {
                isr = new InputStreamReader(remoteFileStorageManager.downloadFile(SYNC_DIR, csvFileName), "UTF-8");
                parser = new CSVParser(isr);

            } else {
                String fileName = uploadRoot + File.separator + SYNC_DIR + File.separator + csvFileName;
                isr = new InputStreamReader( new FileInputStream(fileName), "UTF-8");
                parser = new CSVParser(isr, CSVStrategy.EXCEL_STRATEGY);
            }

            String[][] rows = parser.getAllValues();

            //initialization if validation script config exists
            final ValidationScript validationScript = StringUtils.isNotEmpty(config.getValidationRule()) ? SynchScriptFactory.createValidationScript(config.getValidationRule()) : null;
            //initialization if transformation script config exists
            final List<TransformScript> transformScripts = SynchScriptFactory.createTransformationScript(config);
            //init match rules
            final MatchObjectRule matchRule = matchRuleFactory.create(config.getCustomMatchRule());
            //Get Header
            final LineObject rowHeader = populateTemplate(rows[0]);

            auditBuilder.addAttribute(AuditAttributeName.DESCRIPTION, "Rows for processing: "+rows.length);

            if (rows.length > 0) {
                proccess(config, provService, rows, validationScript, transformScripts, matchRule, rowHeader,0, auditBuilder);

            }
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();

            log.error(fe);
            auditBuilder.addAttribute(AuditAttributeName.DESCRIPTION, "FileNotFoundException: "+fe.getMessage());
            auditLogProvider.persist(auditBuilder);
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FILE_EXCEPTION);
            return resp;

        } catch (ClassNotFoundException cnfe) {

            log.error(cnfe);

            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
            return resp;
        } catch (IOException io) {
            io.printStackTrace();
            /*
            synchStartLog.updateSynchAttributes("FAIL", ResponseCode.IO_EXCEPTION.toString(), io.toString());
            auditHelper.logEvent(synchStartLog);
			*/
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.IO_EXCEPTION);
            return resp;
        } catch (SftpException sftpe) {
            log.error(sftpe);
            /*
            synchStartLog.updateSynchAttributes("FAIL", ResponseCode.FILE_EXCEPTION.toString(), sftpe.toString());
            auditHelper.logEvent(synchStartLog);
			*/
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FILE_EXCEPTION);
            sftpe.printStackTrace();
        } catch (JSchException jsche) {
            log.error(jsche);
            /*
            synchStartLog.updateSynchAttributes("FAIL", ResponseCode.FILE_EXCEPTION.toString(), jsche.toString());
            auditHelper.logEvent(synchStartLog);
			*/
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FILE_EXCEPTION);
            jsche.printStackTrace();
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        log.debug("CSV SYNCHRONIZATION COMPLETE^^^^^^^^");

        auditBuilder.addAttribute(AuditAttributeName.DESCRIPTION, "CSV SYNCHRONIZATION COMPLETE^^^^^^^^");
        return new SyncResponse(ResponseStatus.SUCCESS);
    }

    private void proccess(SynchConfig config, ProvisionService provService, String[][] rows, final ValidationScript validationScript, final List<TransformScript> transformScripts, MatchObjectRule matchRule, LineObject rowHeader, int ctr,  AuditLogBuilder auditLogBuilder) {
        for (String[] row : rows) {
            log.info("*** Record counter: " + ctr++);
            //populate the data object

            LineObject rowObj = rowHeader.copy();

            populateRowObject(rowObj, row);
            log.info(" - Validation being called");

            // validate
            if (validationScript != null) {
                synchronized (mutex) {
                    int retval = validationScript.isValid(rowObj);
                    if (retval == ValidationScript.NOT_VALID) {
                        log.info(" - Validation failed...transformation will not be called.");
                        continue;
                    }
                    if (retval == ValidationScript.SKIP) {
                        continue;
                    }
                }
            }

            log.info(" - Getting column map...");

            // check if the user exists or not
            Map<String, Attribute> rowAttr = rowObj.getColumnMap();

            log.info(" - Row Attr..." + rowAttr);
            //
            StringBuilder rowAsStr = new StringBuilder();
            for(String col : row) {
               rowAsStr.append(col).append(",");
            }
            auditLogBuilder.addAttribute(AuditAttributeName.DESCRIPTION, " - Row Attrs:" + rowAsStr.toString());

            User usr = matchRule.lookup(config, rowAttr);
            auditLogBuilder.addAttribute(AuditAttributeName.DESCRIPTION, " Lookup User in repository: " + usr != null ? "FOUND" : "NOT FOUND");
            //@todo - Update lookup so that an exception is thrown
            // when lookup fails due to bad matching.

            log.info(" - Preparing transform script");

            // transform
            int retval = -1;
            ProvisionUser pUser = new ProvisionUser();

            if (transformScripts != null && transformScripts.size() > 0) {
                for (TransformScript transformScript : transformScripts) {
                    synchronized (mutex) {
                        transformScript.init();
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

                        log.info(" - Execute transform script");

                        //Disable PRE and POST processors/performance optimizations
                        pUser.setSkipPreprocessor(true);
                        pUser.setSkipPostProcessor(true);
                        retval = transformScript.execute(rowObj, pUser);
                        log.debug("Transform result=" + retval);
                    }
                    log.info(" - Execute complete transform script");
                }
                pUser.setParentAuditLogId(auditLogBuilder.getEntity().getId());
                if (retval != -1) {
                    if (retval == TransformScript.DELETE && pUser.getUser() != null) {
                        auditLogBuilder.addAttribute(AuditAttributeName.DESCRIPTION, "User login: "+(pUser.getFirstName()+" "+pUser.getLastName())+" [REMOVED]");
                        auditLogProvider.persist(auditLogBuilder);
                        provService.deleteByUserId(pUser.getId(), UserStatusEnum.REMOVE, systemAccount);
                    } else {
                        // call synch
                        if (retval != TransformScript.DELETE) {
                            if (usr != null) {
                                log.info(" - Updating existing user");
                                pUser.setId(usr.getId());
                                try {
                                    pUser.setParentAuditLogId(auditLogBuilder.getEntity().getId());
                                    provService.modifyUser(pUser);
                                } catch (Exception e) {
                                    auditLogBuilder.addAttribute(AuditAttributeName.DESCRIPTION, "Error: User login: " +(pUser.getFirstName()+" "+pUser.getLastName())+" [MODIFY] " + e.getMessage());
                                    auditLogProvider.persist(auditLogBuilder);
                                    log.error(e);
                                }
                                auditLogBuilder.addAttribute(AuditAttributeName.DESCRIPTION, "User login: " +(pUser.getFirstName()+" "+pUser.getLastName())+" [MODIFY] ");
                            } else {
                                log.info(" - New user being provisioned");
                                pUser.setId(null);
                                try {
                                    provService.addUser(pUser);
                                } catch (Exception e) {
                                    auditLogBuilder.addAttribute(AuditAttributeName.DESCRIPTION, "Error: User login: " +(pUser.getFirstName()+" "+pUser.getLastName())+" [ADD] " + e.getMessage());
                                    log.error(e);
                                }
                                auditLogBuilder.addAttribute(AuditAttributeName.DESCRIPTION, "User: " +(pUser.getFirstName() + " " + pUser.getLastName())+" [ADD] ");
                            }
                        }
                    }
                } else {
                    auditLogBuilder.addAttribute(AuditAttributeName.DESCRIPTION, "Fail: User login: " +pUser.getLogin());
                }
            }
            // show the user object
            ctr++;
            //ADD the sleep pause to give other threads possibility to be alive
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                log.error("The thread was interrupted when sleep paused after row [" + row + "] execution.", e);
            }
        }
    }

    public Response testConnection(SynchConfig config) {
        Reader reader = null;
        try {
            String csvFileName = config.getFileName();
            if (useRemoteFilestorage) {
                InputStream is = remoteFileStorageManager.downloadFile(SYNC_DIR, csvFileName);
                reader = new InputStreamReader(is);
            } else {
                File file = new File(uploadRoot + File.separator + SYNC_DIR + File.separator + csvFileName);
                reader = new FileReader(file);
            }

        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
            log.error(fe);
            Response resp = new Response(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FILE_EXCEPTION);
            resp.setErrorText(fe.getMessage());
            return resp;

        } catch (SftpException sftpe) {
            log.error(sftpe);
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FILE_EXCEPTION);
            sftpe.printStackTrace();

        } catch (JSchException jsche) {
            log.error(jsche);
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FILE_EXCEPTION);
            jsche.printStackTrace();

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // This can safely be ignored. The file was opened successfully at this point.
                }
            }
        }
        Response resp = new Response(ResponseStatus.SUCCESS);
        return resp;
    }

    private LineObject populateTemplate(String[] lineAry) {
        LineObject rowHeader = new LineObject();
        int ctr = 0;
        for (String s : lineAry) {
            Attribute a = new Attribute(s, null);
            a.setType("STRING");
            a.setColumnNbr(ctr);
            rowHeader.put(a.getName(), a);
            ctr++;
        }
        return rowHeader;
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
            String colValue = lineAry[colNbr];

            attr.setValue(colValue);
        }
    }
}
