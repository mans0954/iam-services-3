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

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.util.StringUtils;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.synch.dto.Attribute;
import org.openiam.idm.srvc.synch.dto.LineObject;
import org.openiam.idm.srvc.synch.dto.SyncResponse;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
import org.openiam.idm.srvc.synch.service.TransformScript;
import org.openiam.idm.srvc.synch.service.ValidationScript;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Reads a CSV file for use during the synchronization process
 *
 * @author suneet
 */
public class CSVAdapter extends AbstractSrcAdapter {

    private static final Log log = LogFactory.getLog(CSVAdapter.class);

    // synchronization monitor
    private final Object mutex = new Object();

    @Value("${org.openiam.upload.root}")
    private String uploadRoot;
    
    @Value("${csvadapter.thread.count}")
    private int THREAD_COUNT;
    
    @Value("${csvadapter.thread.delay.beforestart}")
    private int THREAD_DELAY_BEFORE_START;

    public SyncResponse startSynch(final SynchConfig config) {
        log.debug("CSV startSynch CALLED.^^^^^^^^");
        System.out.println("CSV startSynch CALLED.^^^^^^^^");

        Reader reader = null;

        final ProvisionService provService = (ProvisionService) applicationContext.getBean("defaultProvision");

        String requestId = UUIDGen.getUUID();

        IdmAuditLog synchStartLog_ = new IdmAuditLog();
        synchStartLog_.setSynchAttributes("SYNCH_USER", config.getSynchConfigId(), "START", "SYSTEM", requestId);
        final IdmAuditLog synchStartLog = auditHelper.logEvent(synchStartLog_);

        try {
            File file = new File(uploadRoot +"/sync/"+ config.getFileName());
            reader = new FileReader(file);
            CSVParser parser = new CSVParser(reader, CSVStrategy.EXCEL_STRATEGY);

            String[][] rows = parser.getAllValues();

            //initialization if validation script config exists
            final ValidationScript validationScript = StringUtils.isNotEmpty(config.getValidationRule()) ? SynchScriptFactory.createValidationScript(config.getValidationRule()) : null;
            //initialization if transformation script config exists
            final List<TransformScript> transformScripts = SynchScriptFactory.createTransformationScript(config);
            //init match rules
            final MatchObjectRule matchRule = matchRuleFactory.create(config);
            //Get Header
            final LineObject rowHeader = populateTemplate(rows[0]);
            rows = Arrays.copyOfRange(rows, 1, rows.length);
            // Multithreading
            int allRowsCount = rows.length;
            if (allRowsCount > 0) {
                int threadCoount = THREAD_COUNT;
                int rowsInOneExecutors = allRowsCount / threadCoount;
                int remains = rowsInOneExecutors > 0 ? allRowsCount % (rowsInOneExecutors * threadCoount) : 0;
                if (remains != 0) {
                    threadCoount++;
                }
                log.debug("Thread count = " + threadCoount + "; Rows in one thread = " + rowsInOneExecutors + "; Remains rows = " + remains);
                System.out.println("Thread count = " + threadCoount + "; Rows in one thread = " + rowsInOneExecutors + "; Remains rows = " + remains);
                List<Future> results = new LinkedList<Future>();
                final ExecutorService service = Executors.newCachedThreadPool();
                for (int i = 0; i < threadCoount; i++) {
                    final int startIndex = i * rowsInOneExecutors;
                    int shiftIndex = threadCoount > THREAD_COUNT && i == threadCoount - 1 ? remains : rowsInOneExecutors;

                    final String[][] part = Arrays.copyOfRange(rows, startIndex, startIndex + shiftIndex);
                    results.add(service.submit(new Runnable() {
                        @Override
                        public void run() {
                            proccess(config, provService, synchStartLog, part, validationScript, transformScripts, matchRule, rowHeader, startIndex);
                        }
                    }));
                    //Give 30sec time for thread to be UP (load all cache and begin the work)
                    Thread.sleep(THREAD_DELAY_BEFORE_START);
                }
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        service.shutdown();
                        try {
                            if (!service.awaitTermination(SHUTDOWN_TIME, TimeUnit.MILLISECONDS)) { //optional *
                                log.warn("Executor did not terminate in the specified time."); //optional *
                                List<Runnable> droppedTasks = service.shutdownNow(); //optional **
                                log.warn("Executor was abruptly shut down. " + droppedTasks.size() + " tasks will not be executed."); //optional **
                            }
                        } catch (InterruptedException e) {
                            log.error(e);

                            synchStartLog.updateSynchAttributes("FAIL", ResponseCode.INTERRUPTED_EXCEPTION.toString(), e.toString());
                            auditHelper.logEvent(synchStartLog);

                            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
                            resp.setErrorCode(ResponseCode.INTERRUPTED_EXCEPTION);
                        }
                    }
                });
                waitUntilWorkDone(results);
            }
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();

            log.error(fe);

            synchStartLog.updateSynchAttributes("FAIL", ResponseCode.FILE_EXCEPTION.toString(), fe.toString());
            auditHelper.logEvent(synchStartLog);

            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FILE_EXCEPTION);
            return resp;

        } catch (ClassNotFoundException cnfe) {

            log.error(cnfe);

            synchStartLog.updateSynchAttributes("FAIL", ResponseCode.CLASS_NOT_FOUND.toString(), cnfe.toString());
            auditHelper.logEvent(synchStartLog);

            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
            return resp;
        } catch (IOException io) {
            io.printStackTrace();

            synchStartLog.updateSynchAttributes("FAIL", ResponseCode.IO_EXCEPTION.toString(), io.toString());
            auditHelper.logEvent(synchStartLog);

            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.IO_EXCEPTION);
            return resp;
        } catch (InterruptedException e) {
            log.error(e);

            synchStartLog.updateSynchAttributes("FAIL", ResponseCode.INTERRUPTED_EXCEPTION.toString(), e.toString());
            auditHelper.logEvent(synchStartLog);

            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.INTERRUPTED_EXCEPTION);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        log.debug("CSV SYNCHRONIZATION COMPLETE^^^^^^^^");

        return new SyncResponse(ResponseStatus.SUCCESS);
    }

    private void proccess(SynchConfig config, ProvisionService provService, IdmAuditLog synchStartLog, String[][] rows, final ValidationScript validationScript, final List<TransformScript> transformScripts, MatchObjectRule matchRule, LineObject rowHeader, int ctr) {
        for (String[] row : rows) {
            log.info("*** Record counter: " + ctr++);

            //populate the data object
            ProvisionUser pUser = new ProvisionUser();
            //Disable PRE and POST processors/performance optimizations
            pUser.setSkipPreprocessor(true);
            pUser.setSkipPostProcessor(true);

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

            User usr = matchRule.lookup(config, rowAttr);

            //@todo - Update lookup so that an exception is thrown
            // when lookup fails due to bad matching.

            log.info(" - Preparing transform script");

            // transform
            int retval = -1;
            if (transformScripts != null && transformScripts.size() > 0) {

                for (TransformScript transformScript : transformScripts) {
                    synchronized (mutex) {
                        transformScript.init();

                        // initialize the transform script
                        if (usr != null) {
                            transformScript.setNewUser(false);
                            transformScript.setUser(userDozerConverter.convertToDTO(userManager.getUser(usr.getUserId()), true));
                            transformScript.setPrincipalList(loginDozerConverter.convertToDTOList(loginManager.getLoginByUser(usr.getUserId()), true));
                            transformScript.setUserRoleList(roleDataService.getUserRolesAsFlatList(usr.getUserId()));

                        } else {
                            transformScript.setNewUser(true);
                            transformScript.setUser(null);
                            transformScript.setPrincipalList(null);
                            transformScript.setUserRoleList(null);
                        }

                        log.info(" - Execute transform script");

                        retval = transformScript.execute(rowObj, pUser);
                    }
                    log.info(" - Execute complete transform script");
                }

                pUser.setSessionId(synchStartLog.getSessionId());
                if (retval != -1) {
                    if (retval == TransformScript.DELETE && pUser.getUser() != null) {
                        provService.deleteByUserId(pUser, UserStatusEnum.DELETED, systemAccount);
                    } else {
                        // call synch
                        if (retval != TransformScript.DELETE) {
                            if (usr != null) {
                                log.info(" - Updating existing user");
                                pUser.setUserId(usr.getUserId());
                                try {
                                    provService.modifyUser(pUser);
                                } catch (Exception e) {
                                    log.error(e);
                                }

                            } else {
                                log.info(" - New user being provisioned");
                                pUser.setUserId(null);
                                try {
                                    provService.addUser(pUser);
                                } catch (Exception e) {
                                    log.error(e);
                                }
                            }
                        }
                    }
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
