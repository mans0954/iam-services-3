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
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
import org.openiam.idm.srvc.synch.service.TransformScript;
import org.openiam.idm.srvc.synch.service.ValidationScript;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsResponseControl;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Scan Ldap for any new records, changed users, or delete operations and then synchronizes them back into OpenIAM.
 *
 * @author suneet
 */
@Component
public class LdapAdapter extends AbstractSrcAdapter { // implements SourceAdapter

    /*
     * The flags for the running tasks are handled by this Thread-Safe Set.
     * It stores the taskIds of the currently executing tasks.
     * This is faster and as reliable as storing the flags in the database,
     * if the tasks are only launched from ONE host in a clustered environment.
     * It is unique for each class-loader, which means unique per war-deployment.
     */
    private static Set<String> runningTask = Collections.newSetFromMap(new ConcurrentHashMap());

    @Value("${KEYSTORE}")
    private String keystore;

    private LdapContext ctx = null;

    private static final Log log = LogFactory.getLog(LdapAdapter.class);

    public SyncResponse startSynch(SynchConfig config, AuditLogBuilder auditLogBuilder) {
        // rule used to match object from source system to data in IDM
        MatchObjectRule matchRule = null;
       // String changeLog = null;
       // Date mostRecentRecord = null;
        long mostRecentRecord = 0L;
        String lastRecProcessed = null;
        //java.util.Date lastExec = null;

        log.debug("LDAP startSynch CALLED.^^^^^^^^");

        String requestId = UUIDGen.getUUID();
        /*
        IdmAuditLog synchStartLog = new IdmAuditLog();
        synchStartLog.setSynchAttributes("SYNCH_USER", config.getSynchConfigId(), "START", "SYSTEM", requestId);
        synchStartLog = auditHelper.logEvent(synchStartLog);
		*/
        // This needs to be synchronized, because the check for the taskId and the insertion need to
        // happen atomically. It is possible for two threads, started by Quartz, to reach this point at
        // the same time for the same task.
        synchronized (runningTask) {
            if(runningTask.contains(config.getSynchConfigId())) {
                log.debug("**** Synchronization Configuration " + config.getName() + " is already running");

                SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.FAIL_PROCESS_ALREADY_RUNNING);
                return resp;
            }
            runningTask.add(config.getSynchConfigId());
        }

        try {

            if (!connect(config)) {

                runningTask.remove(config.getSynchConfigId());

                SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.FAIL_CONNECTION);
                return resp;
            }

            try {
                matchRule = matchRuleFactory.create(config.getCustomMatchRule());
            } catch (ClassNotFoundException cnfe) {

                runningTask.remove(config.getSynchConfigId());

                log.error(cnfe);
                /*
                synchStartLog.updateSynchAttributes("FAIL",ResponseCode.CLASS_NOT_FOUND.toString() , cnfe.toString());
                auditHelper.logEvent(synchStartLog);
				*/
                SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
                return resp;
            }
            // get the last execution time
            if (config.getLastRecProcessed() != null) {
			    lastRecProcessed =  config.getLastRecProcessed() ;
		    }

            // get change log field
            if (config.getSynchType().equalsIgnoreCase("INCREMENTAL")) {
                if (lastRecProcessed != null) {
                    // update the search filter so that it has the new time
                    String ldapFilterQuery =  config.getQuery();
                    // replace wildcards with the last exec time

                    config.setQuery(  ldapFilterQuery.replace("?", lastRecProcessed ) );

                    log.debug("Updated ldap filter = " + config.getQuery());
                }
            }

            int ctr = 0;
           //pagging int PAGE_SIZE = 100;      //Y

            List<String> ouByParent = new LinkedList<String>();
            if(config.getBaseDn().contains(";")) {
              for (String basedn : config.getBaseDn().split(";")){
                  ouByParent.add(basedn.trim());
              }
            } else {
                ouByParent.add(config.getBaseDn().trim());
            }
            int pageSize = 0;
            int totalRecords = 0;
            int successRecords = 0;

            for (String baseou : ouByParent) {
                byte[] cookie = null;
                int recordsInOUCounter = 0;

                    pageSize++;
                    recordsInOUCounter++;
                    log.debug("========== New Page number " + pageSize + " for processing, Processed: "+totalRecords+" records");
                    NamingEnumeration results = search(baseou, config.getQuery());

                    while (results != null && results.hasMoreElements()) {
                        totalRecords++;

                        SearchResult sr = (SearchResult) results.nextElement();
                        log.debug("SearchResultElement   : " + sr.getName());
                        log.debug("      Attributes: " + sr.getAttributes());
                        LineObject rowObj = new LineObject();

                        log.debug("-New Row to Synchronize --" + ctr++);
                        Attributes attrs = sr.getAttributes();


                        if (attrs != null) {

                            for (NamingEnumeration ae = attrs.getAll(); ae.hasMore(); ) {

                                javax.naming.directory.Attribute attr = (javax.naming.directory.Attribute) ae.next();

                                List<String> valueList = new ArrayList<String>();

                                String key = attr.getID();

                                log.debug("attribute id=: " + key);

                                for (NamingEnumeration e = attr.getAll(); e.hasMore(); ) {
                                    Object o = e.next();
                                    if (o.toString() != null) {
                                        valueList.add(o.toString());
                                        log.debug("- value:=" + o.toString());
                                    }
                                }
                                if (valueList.size() > 0) {
                                    org.openiam.idm.srvc.synch.dto.Attribute rowAttr = new org.openiam.idm.srvc.synch.dto.Attribute();
                                    rowAttr.populateAttribute(key, valueList);
                                    rowObj.put(key, rowAttr);
                                } else {
                                    log.debug("- value is null");
                                }
                            }
                        }

                        LastRecordTime lrt = getRowTime(rowObj);

                        if (mostRecentRecord < lrt.mostRecentRecord) {
                            mostRecentRecord = lrt.mostRecentRecord;
                            lastRecProcessed = lrt.generalizedTime;
                        }


                        log.debug("STarting validation and transformation..");

                        // start the synch process
                        // 1) Validate the data
                        // 2) Transform it
                        // 3) if not delete - then match the object and determine if its a new object or its an udpate
                       try {
                            // validate
                            if (config.getValidationRule() != null && config.getValidationRule().length() > 0) {
                                ValidationScript script = SynchScriptFactory.createValidationScript(config.getValidationRule());
                                int retval = script.isValid(rowObj);
                                if (retval == ValidationScript.NOT_VALID) {
                                    log.error("Row Object Faied Validation=" + rowObj.toString());
                                    // log this object in the exception log

                                    continue;
                                }
                                if (retval == ValidationScript.SKIP) {
                                    continue;
                                }
                            }

                            // check if the user exists or not
                            Map<String, Attribute> rowAttr = rowObj.getColumnMap();
                            //
                            matchRule = matchRuleFactory.create(config.getCustomMatchRule());
                            User usr = matchRule.lookup(config, rowAttr);

                            // transform
                            int retval = -1;
                            ProvisionUser pUser = new ProvisionUser();
                            List<TransformScript> transformScripts = SynchScriptFactory.createTransformationScript(config);
                            if (transformScripts != null && transformScripts.size() > 0) {
                                for (TransformScript transformScript : transformScripts) {
                                    transformScript.init();
                                    pUser = new ProvisionUser();
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

                                if (retval != -1) {
                                    successRecords++;
                                    if (retval == TransformScript.DELETE && usr != null) {
                                        log.debug("deleting record - " + usr.getId());
                                        ProvisionUserResponse userResp = provService.deleteByUserId(usr.getId(), UserStatusEnum.DELETED, systemAccount);

                                    } else {
                                        // call synch
                                        if (retval != TransformScript.DELETE) {
                                            System.out.println("Provisioning user=" + pUser.getLastName());
                                            if (usr != null) {
                                                log.debug("updating existing user...systemId=" + pUser.getId());
                                                pUser.setId(usr.getId());
                                                ProvisionUserResponse userResp = provService.modifyUser(pUser);

                                            } else {
                                                log.debug("adding new user...");
                                                pUser.setId(null);
                                                ProvisionUserResponse userResp = provService.addUser(pUser);
                                            }
                                        }
                                    }
                                }
                            }
                            // show the user object

                        } catch (ClassNotFoundException cnfe) {

                            if (runningTask.contains(config.getSynchConfigId())) {
                                runningTask.remove(config.getSynchConfigId());
                            }

                            log.error(cnfe);
                    /*
                    synchStartLog.updateSynchAttributes("FAIL",ResponseCode.CLASS_NOT_FOUND.toString() , cnfe.toString());
                    auditHelper.logEvent(synchStartLog);
					*/
                            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
                            resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
                            resp.setErrorText(cnfe.toString());
                            return resp;
                        } catch (IOException fe) {

                            if (runningTask.contains(config.getSynchConfigId())) {
                                runningTask.remove(config.getSynchConfigId());
                            }

                            log.error(fe);
                    /*
                    synchStartLog.updateSynchAttributes("FAIL",ResponseCode.FILE_EXCEPTION.toString() , fe.toString());
                    auditHelper.logEvent(synchStartLog);
					*/
                            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
                            resp.setErrorCode(ResponseCode.FILE_EXCEPTION);
                            resp.setErrorText(fe.toString());
                            return resp;

                        } catch (Exception e) {

                            if (runningTask.contains(config.getSynchConfigId())) {
                                runningTask.remove(config.getSynchConfigId());
                            }

                            log.error(e);
                    /*
                    synchStartLog.updateSynchAttributes("FAIL",ResponseCode.FAIL_OTHER.toString() , e.toString());
                    auditHelper.logEvent(synchStartLog);
					*/
                            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
                            resp.setErrorCode(ResponseCode.FAIL_OTHER);
                            resp.setErrorText(e.toString());
                            return resp;
                        }
                    }

// Examine the paged results control response
                    Control[] controls = ctx.getResponseControls();
                    if (controls != null) {
                        log.debug("Controls size = "+controls.length);
                        for (Control c : controls) {
                            log.debug("Control = "+c);
                            if (c instanceof PagedResultsResponseControl) {
                                PagedResultsResponseControl prrc = (PagedResultsResponseControl)c;
                                log.debug("PagedResultsResponseControl = [" + prrc.getID() + "," + prrc.getCookie() + "," + prrc.getResultSize() + "," + prrc.getEncodedValue() + "," + prrc.isCritical() + "]");
                                cookie = prrc.getCookie();
//                                break;
                            }
                        }
                    } else {
                        //  log.debug("Controls is NULL reset cookie");
                        //  cookie = null;
                    }
                    log.debug("Search page result cookie = "+cookie);
                    if(cookie != null) {
         //               ctx.setRequestControls(new Control[]{ new PagedResultsControl(PAGE_SIZE, cookie, Control.NONCRITICAL) });
                    }

                    log.debug("========== Finished processing of Page number " + pageSize + ", Processed: "+totalRecords+" records, "+" Success Records: "+successRecords +"");
            //    } while (cookie != null);

                log.debug("Search ldap result OU=" + baseou + " found = " + recordsInOUCounter + " records.");
            }
            ctx.close();
        } catch (NamingException ne) {

            if (runningTask.contains(config.getSynchConfigId())) {
                runningTask.remove(config.getSynchConfigId());
            }

            log.error(ne);
            /*
            synchStartLog.updateSynchAttributes("FAIL",ResponseCode.DIRECTORY_NAMING_EXCEPTION.toString() , ne.toString());
            auditHelper.logEvent(synchStartLog);
			*/
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
            resp.setErrorText(ne.toString());
            return resp;

        } catch (IOException eioex) {
            if (runningTask.contains(config.getSynchConfigId())) {
                runningTask.remove(config.getSynchConfigId());
            }

            log.error(eioex);
            /*
            synchStartLog.updateSynchAttributes("FAIL",ResponseCode.DIRECTORY_NAMING_EXCEPTION.toString() , ne.toString());
            auditHelper.logEvent(synchStartLog);
			*/
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.INTERNAL_ERROR);
            resp.setErrorText(eioex.toString());
            return resp;
        } finally {
            try {
                ctx.close();
            } catch (NamingException ne) {
                if (runningTask.contains(config.getSynchConfigId())) {
                    runningTask.remove(config.getSynchConfigId());
                }
                log.error(ne);
            }
        }

        runningTask.remove(config.getSynchConfigId());

        log.debug("LDAP SYNCHRONIZATION COMPLETE^^^^^^^^");

        SyncResponse resp = new SyncResponse(ResponseStatus.SUCCESS);
        //resp.setLastRecordTime(mostRecentRecord);
        resp.setLastRecProcessed(lastRecProcessed);
        return resp;

    }

    public Response testConnection(SynchConfig config) {
        try {
            if (connect(config)) {
                closeConnection();
                Response resp = new Response(ResponseStatus.SUCCESS);
                return resp;
            } else {
                Response resp = new Response(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.FAIL_CONNECTION);
                return resp;
            }

        } catch (NamingException e) {
            e.printStackTrace();
            log.error(e);

            Response resp = new Response(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_CONNECTION);
            resp.setErrorText(e.getMessage());
            return resp;
        }
    }

    private LastRecordTime getRowTime(LineObject rowObj) {
        Attribute atr = rowObj.get("modifyTimestamp");

        if (atr != null && atr.getValue() != null) {
            return getTime(atr);
        }
        atr = rowObj.get("createTimestamp");

        if (atr != null && atr.getValue() != null) {
            return getTime(atr);
        }

        return new LastRecordTime();
    }

    private LastRecordTime getTime(Attribute atr) {
        LastRecordTime lrt = new LastRecordTime();

        String s = atr.getValue();
        int i = s.indexOf("Z");
        if (i == -1) {
            i = s.indexOf("-");
        }
        if (i > 0) {
            lrt.mostRecentRecord = Long.parseLong(s.substring(0, i));
            lrt.generalizedTime = atr.getValue();
            return lrt;

        }
        lrt.mostRecentRecord = Long.parseLong(s);
        lrt.generalizedTime = atr.getValue();

        return lrt;
    }

    private NamingEnumeration search(String baseDn, String searchFilter) throws NamingException, IOException {

      //  String attrIds[] = {"1.1", "+", "*"};
       String attrIds[] = {"objectClass"};
        // String attrIds[] = {"1.1", "+", "*", "accountUnlockTime", "aci", "aclRights", "aclRightsInfo", "altServer", "attributeTypes", "changeHasReplFixupOp", "changeIsReplFixupOp", "copiedFrom", "copyingFrom", "createTimestamp", "creatorsName", "deletedEntryAttrs", "dITContentRules", "dITStructureRules", "dncomp", "ds-pluginDigest", "ds-pluginSignature", "ds6ruv", "dsKeyedPassword", "entrydn", "entryid", "hasSubordinates", "idmpasswd", "isMemberOf", "ldapSchemas", "ldapSyntaxes", "matchingRules", "matchingRuleUse", "modDNEnabledSuffixes", "modifiersName", "modifyTimestamp", "nameForms", "namingContexts", "nsAccountLock", "nsBackendSuffix", "nscpEntryDN", "nsds5ReplConflict", "nsIdleTimeout", "nsLookThroughLimit", "nsRole", "nsRoleDN", "nsSchemaCSN", "nsSizeLimit", "nsTimeLimit", "nsUniqueId", "numSubordinates", "objectClasses", "parentid", "passwordAllowChangeTime", "passwordExpirationTime", "passwordExpWarned", "passwordHistory", "passwordPolicySubentry", "passwordRetryCount", "pwdAccountLockedTime", "pwdChangedTime", "pwdFailureTime", "pwdGraceUseTime", "pwdHistory", "pwdLastAuthTime", "pwdPolicySubentry", "pwdReset", "replicaIdentifier", "replicationCSN", "retryCountResetTime", "subschemaSubentry", "supportedControl", "supportedExtension", "supportedLDAPVersion", "supportedSASLMechanisms", "supportedSSLCiphers", "targetUniqueId", "vendorName", "vendorVersion"};

        SearchControls searchCtls = new SearchControls();
        searchCtls.setTimeLimit(0);
        searchCtls.setCountLimit(10000);
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchCtls.setReturningAttributes(attrIds);

        log.debug("Search: base dn=" + baseDn + ", filter= " + searchFilter);
        return ctx.search(baseDn, searchFilter, searchCtls);
    }

    private boolean connect(SynchConfig config) throws NamingException {

        Hashtable<String, String> envDC = new Hashtable();
        System.setProperty("javax.net.ssl.trustStore", keystore);

        String hostUrl = config.getSrcHost(); //   managedSys.getHostUrl();
        log.debug("Directory host url:" + hostUrl);

        envDC.put(Context.PROVIDER_URL, hostUrl);
        envDC.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        envDC.put(Context.SECURITY_AUTHENTICATION, "simple"); // simple
        envDC.put(Context.SECURITY_PRINCIPAL, config.getSrcLoginId());  //"administrator@diamelle.local"
        envDC.put(Context.SECURITY_CREDENTIALS, config.getSrcPassword());
    //    envDC.put(Context.BATCHSIZE, "100");
     //   envDC.put("com.sun.jndi.ldap.read.timeout", "60000");

        if (hostUrl.toLowerCase().contains("ldaps")) {
            envDC.put(Context.SECURITY_PROTOCOL, "SSL");
        }

        ctx = new InitialLdapContext(envDC, null);
        if (ctx != null) {
            return true;
        }

        return false;
    }

    private void closeConnection() {
        try {
            if (ctx != null) {
                ctx.close();
            }

        } catch (NamingException ne) {
            log.error(ne.getMessage(), ne);
            ne.printStackTrace();
        }
    }

    private class LastRecordTime {
        long mostRecentRecord;
        String generalizedTime;
    }

}
