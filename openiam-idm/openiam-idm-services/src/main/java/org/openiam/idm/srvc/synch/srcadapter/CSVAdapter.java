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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.csv.CSVStrategy;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.parser.csv.CSVHelper;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;
import org.openiam.idm.srvc.synch.dto.*;
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Reads a CSV file for use during the synchronization process
 *
 * @author suneet
 */
@Component
public class CSVAdapter extends AbstractSrcAdapter {

    private static final Log log = LogFactory.getLog(CSVAdapter.class);
    public static final String SYNC_DIR = "sync";

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
    private RemoteFileStorageManager remoteFileStorageManager;

    @Override
    public SyncResponse startSynch(final SynchConfig config) {
        return startSynch(config, null, null);
    }

    @Override
    public SyncResponse startSynch(final SynchConfig config, SynchReviewEntity sourceReview, final SynchReviewEntity resultReview) {

        log.debug("CSV startSynch CALLED.^^^^^^^^");
        System.out.println("CSV startSynch CALLED.^^^^^^^^");

        SyncResponse res = new SyncResponse(ResponseStatus.SUCCESS);

        SynchReview review = null;
        if (sourceReview != null) {
            review = synchReviewDozerConverter.convertToDTO(sourceReview, false);
        }
        LineObject rowHeaderForReport = null;
        InputStream input = null;

        try {
            final ValidationScript validationScript = StringUtils.isNotEmpty(config.getValidationRule()) ? SynchScriptFactory.createValidationScript(config, review) : null;
            final List<TransformScript> transformScripts = SynchScriptFactory.createTransformationScript(config, review);
            final MatchObjectRule matchRule = matchRuleFactory.create(config.getCustomMatchRule()); // check if matchRule exists

            if (validationScript == null || transformScripts == null || matchRule == null) {
                res = new SyncResponse(ResponseStatus.FAILURE);
                res.setErrorText("The problem in initialization of CSVAdapter, please check validationScript= " + validationScript + ", transformScripts=" + transformScripts + ", matchRule=" + matchRule + " all must be set!");
                res.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
                return res;
            }


            if (sourceReview != null && !sourceReview.isSourceRejected()) {
                return startSynchReview(config, sourceReview, resultReview, validationScript, transformScripts, matchRule);
            }


            CSVHelper parser;
            String csvFileName = config.getFileName();
            if (useRemoteFilestorage) {
                input = remoteFileStorageManager.downloadFile(SYNC_DIR, csvFileName);
                parser = new CSVHelper(input, "UTF-8");
            } else {
                String fileName = uploadRoot + File.separator + SYNC_DIR + File.separator + csvFileName;
                input = new FileInputStream(fileName);
                parser = new CSVHelper(input, "UTF-8", CSVStrategy.EXCEL_STRATEGY);
            }

            final String[][] rows = parser.getAllValues();

            //Get Header
            final LineObject rowHeader = populateTemplate(rows[0]);
            rowHeaderForReport = rowHeader;
            if (rows.length > 1) {


                int part = rows.length / THREAD_COUNT;
                int remains = rows.length - part * THREAD_COUNT;

                List<Part> partsList = new ArrayList<Part>();
                for (int i = 0; i < THREAD_COUNT; i++) {
                    if (i != THREAD_COUNT - 1) {
                        partsList.add(new Part(i * part, (i + 1) * part));
                    } else {
                        partsList.add(new Part(i * part, (i + 1) * part + remains));
                    }
                }

                final Counter counter = new Counter();
                ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
                List<Future<Integer>> list = new ArrayList<Future<Integer>>();

                final String[][] rowsWithoutHeader = Arrays.copyOfRange(rows, 1, rows.length);
                for (final Part p : partsList) {
                    Callable<Integer> worker = new Callable<Integer>() {
                        @Override
                        public Integer call() throws Exception {
                            System.out.println("======= CSV Adapter Part [" + p.getStartIndx() + "; " + p.getEndIndx() + "] started.");

                            int number = 0;
                            String[][] rowsForProcessing = Arrays.copyOfRange(rowsWithoutHeader, p.getStartIndx(), p.getEndIndx());
                            for (String[] row : rowsForProcessing) {
                                LineObject rowObj = rowHeader.copy();
                                populateRowObject(rowObj, row);
                                processLineObject(rowObj, config, resultReview, validationScript, transformScripts, matchRule);
                                number = counter.increment();
                                System.out.println("======= CSV Adapter Part [" + p.getStartIndx() + "; " + p.getEndIndx() + "]  counter.increment = " + number);
                            }
                            System.out.println("======= CSV Adapter Part [" + p.getStartIndx() + "; " + p.getEndIndx() + "] finished.");
                            return number;
                        }
                    };
                    Future<Integer> submit = executor.submit(worker);
                    list.add(submit);
                }

                // This will make the executor accept no new threads
                // and finish all existing threads in the queue
                executor.shutdown();
                // Wait until all threads are finish
                while (!executor.isTerminated()) {
                }
                Integer set = 0;
                for (Future<Integer> future : list) {
                    try {
                        set += future.get();
                    } catch (InterruptedException e) {
                        log.warn(e.getMessage());
                    } catch (ExecutionException e) {
                        log.warn("CSVAdapter: future.get() throw problem message");
                    }
                }
                System.out.println("CSV ================= All Processed records = " + set);
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
            log.debug("CSV SYNCHRONIZATION COMPLETE WITH ERRORS ^^^^^^^^");
            return resp;

        } catch (IOException io) {
            io.printStackTrace();
            /*
            synchStartLog.updateSynchAttributes("FAIL", ResponseCode.IO_EXCEPTION.toString(), io.toString());
            auditHelper.logEvent(synchStartLog);
			*/
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.IO_EXCEPTION);
            log.debug("CSV SYNCHRONIZATION COMPLETE WITH ERRORS ^^^^^^^^");
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
            log.debug("CSV SYNCHRONIZATION COMPLETE WITH ERRORS ^^^^^^^^");
        } catch (JSchException jsche) {
            log.error(jsche);
            /*
            synchStartLog.updateSynchAttributes("FAIL", ResponseCode.FILE_EXCEPTION.toString(), jsche.toString());
            auditHelper.logEvent(synchStartLog);
			*/
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FILE_EXCEPTION);
            jsche.printStackTrace();
            log.debug("CSV SYNCHRONIZATION COMPLETE WITH ERRORS ^^^^^^^^");
        } finally {
            if (resultReview != null) {
                if (CollectionUtils.isNotEmpty(resultReview.getReviewRecords())) { // add header row
                    resultReview.addRecord(generateSynchReviewRecord(rowHeaderForReport, true));
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

//        auditBuilder.addAttribute(AuditAttributeName.DESCRIPTION, "CSV SYNCHRONIZATION COMPLETE^^^^^^^^");
        return new SyncResponse(ResponseStatus.SUCCESS);
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
            String colValue = lineAry.length > colNbr ? lineAry[colNbr] : "";
            attr.setValue(colValue);
        }
    }


    class Part {
        int startIndx = -1;
        int endIndx = -1;

        Part(int startIndx, int endIndx) {
            this.startIndx = startIndx;
            this.endIndx = endIndx;
        }

        public int getStartIndx() {
            return startIndx;
        }

        public void setStartIndx(int startIndx) {
            this.startIndx = startIndx;
        }

        public int getEndIndx() {
            return endIndx;
        }

        public void setEndIndx(int endIndx) {
            this.endIndx = endIndx;
        }
    }


    public class Counter {
        private AtomicInteger value = new AtomicInteger();

        public int getValue() {
            return value.get();
        }

        public int increment() {
            return value.incrementAndGet();
        }

        // Alternative implementation as increment but just make the
        // implementation explicit
        public int incrementLongVersion() {
            int oldValue = value.get();
            while (!value.compareAndSet(oldValue, oldValue + 1)) {
                oldValue = value.get();
            }
            return oldValue + 1;
        }

    }

}
