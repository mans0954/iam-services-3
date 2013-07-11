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
import org.openiam.provision.resp.ProvisionUserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Scan Ldap for any new records, changed users, or delete operations and then synchronizes them back into OpenIAM.
 *
 * @author suneet
 */
public class LdapAdapter extends AbstractSrcAdapter { // implements SourceAdapter

    /*
     * The flags for the running tasks are handled by this Thread-Safe Set.
     * It stores the taskIds of the currently executing tasks.
     * This is faster and as reliable as storing the flags in the database,
     * if the tasks are only launched from ONE host in a clustered environment.
     * It is unique for each class-loader, which means unique per war-deployment.
     */
    private static Set<String> runningTask = Collections.newSetFromMap(new ConcurrentHashMap());

    protected LineObject rowHeader = new LineObject();
    protected ProvisionUser pUser = new ProvisionUser();
    
    @Value("${KEYSTORE}")
    private String keystore;

    private LdapContext ctx = null;

    private static final Log log = LogFactory.getLog(LdapAdapter.class);

    public SyncResponse startSynch(SynchConfig config) {
        // rule used to match object from source system to data in IDM
        MatchObjectRule matchRule = null;
       // String changeLog = null;
       // Date mostRecentRecord = null;
        long mostRecentRecord = 0L;
        String lastRecProcessed = null;
        //java.util.Date lastExec = null;
        IdmAuditLog synchUserStartLog = null;

        log.debug("LDAP startSynch CALLED.^^^^^^^^");

        String requestId = UUIDGen.getUUID();

        IdmAuditLog synchStartLog = new IdmAuditLog();
        synchStartLog.setSynchAttributes("SYNCH_USER", config.getSynchConfigId(), "START", "SYSTEM", requestId);
        synchStartLog = auditHelper.logEvent(synchStartLog);

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
                matchRule = matchRuleFactory.create(config);
            } catch (ClassNotFoundException cnfe) {

                runningTask.remove(config.getSynchConfigId());

                log.error(cnfe);

                synchStartLog.updateSynchAttributes("FAIL",ResponseCode.CLASS_NOT_FOUND.toString() , cnfe.toString());
                auditHelper.logEvent(synchStartLog);

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

            NamingEnumeration results = search(config);
            while (results != null && results.hasMore()) {
                SearchResult sr = (SearchResult) results.next();
                Attributes attrs = sr.getAttributes();

                pUser = new ProvisionUser();
                LineObject rowObj = new LineObject();

                log.debug("-New Row to Synchronize --" + ctr++);

                if (attrs != null) {
                   // try {
                        for (NamingEnumeration ae = attrs.getAll(); ae.hasMore();) {

                            javax.naming.directory.Attribute attr = (javax.naming.directory.Attribute) ae.next();

                            List<String> valueList = new ArrayList<String>();

                            String key = attr.getID();

                            log.debug("attribute id=: " + key);

                            for (NamingEnumeration e = attr.getAll(); e.hasMore();) {
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
                            }else {
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
                    matchRule = matchRuleFactory.create(config);
                    User usr = matchRule.lookup(config, rowAttr);

                    // transform
                    int retval = -1;
                    List<TransformScript> transformScripts = SynchScriptFactory.createTransformationScript(config);
                    if (transformScripts != null && transformScripts.size() > 0) {

                        for (TransformScript transformScript : transformScripts) {
                            // initialize the transform script
                            if (usr != null) {
                                transformScript.setNewUser(false);
                                transformScript.setUser(userDozerConverter.convertToDTO(userManager.getUser(usr.getUserId()), true));
                                transformScript.setPrincipalList(loginDozerConverter.convertToDTOList(loginManager.getLoginByUser(usr.getUserId()), true));
                                transformScript.setUserRoleList(roleDataService.getUserRolesAsFlatList(usr.getUserId()));

                            } else {
                                transformScript.setNewUser(true);
                            }

                            retval = transformScript.execute(rowObj, pUser);

                            log.debug("Transform result=" + retval);
                        }

                        pUser.setSessionId(synchStartLog.getSessionId());

                        if (retval == TransformScript.DELETE && usr != null) {
                            log.debug("deleting record - " + usr.getUserId());
                            ProvisionUserResponse userResp = provService.deleteByUserId(new ProvisionUser(usr), UserStatusEnum.DELETED, systemAccount);

                        } else {
                            // call synch
                            if (retval != TransformScript.DELETE) {
                                System.out.println("Provisioning user=" + pUser.getUser().getLastName());
                                if (usr != null) {
                                    log.debug("updating existing user...systemId=" + pUser.getUser().getUserId());
                                    pUser.getUser().setUserId(usr.getUserId());
                                    ProvisionUserResponse userResp = provService.modifyUser(pUser);

                                } else {
                                    log.debug("adding new user...");
                                    pUser.getUser().setUserId(null);
                                    ProvisionUserResponse userResp = provService.addUser(pUser);
                                }
                            }
                        }
                    }
                    // show the user object

                } catch (ClassNotFoundException cnfe) {

                    if(runningTask.contains(config.getSynchConfigId())) {
                        runningTask.remove(config.getSynchConfigId());
                    }

                    log.error(cnfe);

                    synchStartLog.updateSynchAttributes("FAIL",ResponseCode.CLASS_NOT_FOUND.toString() , cnfe.toString());
                    auditHelper.logEvent(synchStartLog);

                    SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
                    resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
                    resp.setErrorText(cnfe.toString());
                    return resp;
                }  catch (IOException fe ) {

                    if(runningTask.contains(config.getSynchConfigId())) {
                        runningTask.remove(config.getSynchConfigId());
                    }

                    log.error(fe);

                    synchStartLog.updateSynchAttributes("FAIL",ResponseCode.FILE_EXCEPTION.toString() , fe.toString());
                    auditHelper.logEvent(synchStartLog);

                    SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
                    resp.setErrorCode(ResponseCode.FILE_EXCEPTION);
                    resp.setErrorText(fe.toString());
                    return resp;

                } catch (Exception e ) {

                    if(runningTask.contains(config.getSynchConfigId())) {
                        runningTask.remove(config.getSynchConfigId());
                    }

                    log.error(e);

                    synchStartLog.updateSynchAttributes("FAIL",ResponseCode.FAIL_OTHER.toString() , e.toString());
                    auditHelper.logEvent(synchStartLog);

                    SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
                    resp.setErrorCode(ResponseCode.FAIL_OTHER);
                    resp.setErrorText(e.toString());
                    return resp;
                }
            }

        } catch (NamingException ne) {

            if(runningTask.contains(config.getSynchConfigId())) {
                runningTask.remove(config.getSynchConfigId());
            }

            log.error(ne);

            synchStartLog.updateSynchAttributes("FAIL",ResponseCode.DIRECTORY_NAMING_EXCEPTION.toString() , ne.toString());
            auditHelper.logEvent(synchStartLog);

            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
            resp.setErrorText(ne.toString());
            return resp;

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
            if(connect(config)){
                closeConnection();
                Response resp = new Response(ResponseStatus.SUCCESS);
                return resp;
            }else{
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

    private LastRecordTime getRowTime(LineObject rowObj)  {
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
            lrt.mostRecentRecord =  Long.parseLong( s.substring(0,i) );
            lrt.generalizedTime = atr.getValue();
            return lrt;

        }
        lrt.mostRecentRecord =  Long.parseLong( s );
        lrt.generalizedTime = atr.getValue();

        return lrt;
    }

    private NamingEnumeration search(SynchConfig config) throws NamingException {

       // String attrIds[] = {"1.1", "+", "*"};

        String attrIds[] = {"1.1", "+", "*", "accountUnlockTime", "aci", "aclRights", "aclRightsInfo", "altServer", "attributeTypes", "changeHasReplFixupOp", "changeIsReplFixupOp", "copiedFrom", "copyingFrom", "createTimestamp", "creatorsName", "deletedEntryAttrs", "dITContentRules", "dITStructureRules", "dncomp", "ds-pluginDigest", "ds-pluginSignature", "ds6ruv", "dsKeyedPassword", "entrydn", "entryid", "hasSubordinates", "idmpasswd", "isMemberOf", "ldapSchemas", "ldapSyntaxes", "matchingRules", "matchingRuleUse", "modDNEnabledSuffixes", "modifiersName", "modifyTimestamp", "nameForms", "namingContexts", "nsAccountLock", "nsBackendSuffix", "nscpEntryDN", "nsds5ReplConflict", "nsIdleTimeout", "nsLookThroughLimit", "nsRole", "nsRoleDN", "nsSchemaCSN", "nsSizeLimit", "nsTimeLimit", "nsUniqueId", "numSubordinates", "objectClasses", "parentid", "passwordAllowChangeTime", "passwordExpirationTime", "passwordExpWarned", "passwordHistory", "passwordPolicySubentry", "passwordRetryCount", "pwdAccountLockedTime", "pwdChangedTime", "pwdFailureTime", "pwdGraceUseTime", "pwdHistory", "pwdLastAuthTime", "pwdPolicySubentry", "pwdReset", "replicaIdentifier", "replicationCSN", "retryCountResetTime", "subschemaSubentry", "supportedControl", "supportedExtension", "supportedLDAPVersion", "supportedSASLMechanisms", "supportedSSLCiphers", "targetUniqueId", "vendorName", "vendorVersion"};

        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchCtls.setReturningAttributes(attrIds);

        String searchFilter = config.getQuery();

        return ctx.search(config.getBaseDn(), searchFilter, searchCtls);
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

        if (hostUrl.contains("ldaps")) {

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
