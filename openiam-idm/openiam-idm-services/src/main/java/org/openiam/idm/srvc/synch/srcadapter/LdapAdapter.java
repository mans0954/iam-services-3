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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttribute;
import org.openiam.base.BaseAttributeContainer;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.ObjectValue;
import org.openiam.idm.srvc.mngsys.service.AttributeNamesLookupService;
import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;
import org.openiam.idm.srvc.synch.dto.*;
import org.openiam.idm.srvc.synch.dto.Attribute;
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
import org.openiam.idm.srvc.synch.service.TransformScript;
import org.openiam.idm.srvc.synch.service.ValidationScript;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.*;
import java.io.IOException;
import java.util.*;

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

    protected LineObject lineHeader;

    @Value("${KEYSTORE}")
    private String keystore;

    private final static int PAGE_SIZE = 1000;

    private LdapContext ctx = null;

    private static final Log log = LogFactory.getLog(LdapAdapter.class);

    @Override
    public SyncResponse startSynch(final SynchConfig config) {
        return startSynch(config, null, null);
    }

    @Override
    public SyncResponse startSynch(SynchConfig config, SynchReviewEntity sourceReview, SynchReviewEntity resultReview) {

        long mostRecentRecord = 0L;
        String lastRecProcessed = null;

        log.debug("LDAP startSynch CALLED.^^^^^^^^");

        SyncResponse res = initializeScripts(config, sourceReview);
        if (ResponseStatus.FAILURE.equals(res.getStatus())) {
            return res;
        }

        if (sourceReview != null && !sourceReview.isSourceRejected()) {
            return startSynchReview(config, sourceReview, resultReview);
        }

        try {

            if (!connect(config)) {
                SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.FAIL_CONNECTION);
                return resp;
            }

            try {
                matchRuleFactory.create(config.getCustomMatchRule());

            } catch (ClassNotFoundException cnfe) {
                log.error(cnfe);
                SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
                return resp;
            }
            // get the last execution time
            if (config.getLastRecProcessed() != null) {
                lastRecProcessed = config.getLastRecProcessed();
            }

            // get change log field
            if (config.getSynchType().equalsIgnoreCase("INCREMENTAL")) {
                if (lastRecProcessed != null) {
                    // update the search filter so that it has the new time
                    String ldapFilterQuery = config.getQuery();
                    // replace wildcards with the last exec time

                    config.setQuery(ldapFilterQuery.replace("?", lastRecProcessed));

                    log.debug("Updated ldap filter = " + config.getQuery());
                }
            }

            int ctr = 0;
            List<String> ouByParent = new LinkedList<String>();
            if (config.getBaseDn().contains(";")) {
                for (String basedn : config.getBaseDn().split(";")) {
                    ouByParent.add(basedn.trim());
                }
            } else {
                ouByParent.add(config.getBaseDn().trim());
            }
            int totalRecords = 0;
            for (String baseou : ouByParent) {
                int recordsInOUCounter = 0;

                log.debug("========== Processed: " + totalRecords + " records");
                //TimeOut Error String attrIds[] = {"objectClass",""1.1,"+","*"};
                //  TimeOut Error String attrIds[] = {"objectClass", "*", "accountUnlockTime", "aci", "aclRights", "aclRightsInfo", "altServer", "attributeTypes", "changeHasReplFixupOp", "changeIsReplFixupOp", "copiedFrom", "copyingFrom", "createTimestamp", "creatorsName", "deletedEntryAttrs", "dITContentRules", "dITStructureRules", "dncomp", "ds-pluginDigest", "ds-pluginSignature", "ds6ruv", "dsKeyedPassword", "entrydn", "entryid", "hasSubordinates", "idmpasswd", "isMemberOf", "ldapSchemas", "ldapSyntaxes", "matchingRules", "matchingRuleUse", "modDNEnabledSuffixes", "modifiersName", "modifyTimestamp", "nameForms", "namingContexts", "nsAccountLock", "nsBackendSuffix", "nscpEntryDN", "nsds5ReplConflict", "nsIdleTimeout", "nsLookThroughLimit", "nsRole", "nsRoleDN", "nsSchemaCSN", "nsSizeLimit", "nsTimeLimit", "nsUniqueId", "numSubordinates", "objectClasses", "parentid", "passwordAllowChangeTime", "passwordExpirationTime", "passwordExpWarned", "passwordHistory", "passwordPolicySubentry", "passwordRetryCount", "pwdAccountLockedTime", "pwdChangedTime", "pwdFailureTime", "pwdGraceUseTime", "pwdHistory", "pwdLastAuthTime", "pwdPolicySubentry", "pwdReset", "replicaIdentifier", "replicationCSN", "retryCountResetTime", "subschemaSubentry", "supportedControl", "supportedExtension", "supportedLDAPVersion", "supportedSASLMechanisms", "supportedSSLCiphers", "targetUniqueId", "vendorName", "vendorVersion"};

                String[] attrIds = getAttributeIds(config);

                SearchControls searchCtls = new SearchControls();

                ctx.setRequestControls(new Control[]{new PagedResultsControl(PAGE_SIZE, Control.NONCRITICAL)});

                searchCtls.setTimeLimit(0);
                searchCtls.setCountLimit(10000);
                searchCtls.setSearchScope(config.getSearchScope().ordinal());
                searchCtls.setReturningAttributes(attrIds);

                log.debug("Search: base dn=" + baseou + ", filter= " + config.getQuery() + ", attributes=" + attrIds);
                byte[] cookie = null;
                int pageCounter = 0;
                int pageRowCount = 0;
                do {
                    pageCounter++;
                    pageRowCount = 0;
                    NamingEnumeration results = ctx.search(baseou, config.getQuery(), searchCtls);

                    while (results != null && results.hasMoreElements()) {
                        pageRowCount++;
                        totalRecords++;
                        SearchResult sr = (SearchResult) results.nextElement();
                        log.debug("SearchResultElement   : " + sr.getName());
                        log.debug("Attributes: " + sr.getAttributes());
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

                        if (lineHeader == null) {
                            lineHeader = rowObj; // get first row
                        }

                        processLineObject(rowObj, config, resultReview);

                    }
                    log.debug("LDAP Search PAGE RESULT: Page=" + pageCounter + ", rows= " + pageRowCount + " have been processed.");
                    Control[] controls = ctx.getResponseControls();
                    if (controls != null) {
                        for (Control c : controls) {
                            if (c instanceof PagedResultsResponseControl) {
                                PagedResultsResponseControl prrc = (PagedResultsResponseControl) c;
                                cookie = prrc.getCookie();
                                break;
                            }
                        }
                    }
                    ctx.setRequestControls(new Control[]{new PagedResultsControl(PAGE_SIZE, cookie, Control.CRITICAL)});
                } while (cookie != null);

                log.debug("Search ldap result OU=" + baseou + " found = " + recordsInOUCounter + " records.");
            }

        } catch (NamingException ne) {
            log.error(ne);
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
            resp.setErrorText(ne.toString());
            return resp;

        } catch (IOException eioex) {
            log.error(eioex);
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.INTERNAL_ERROR);
            resp.setErrorText(eioex.toString());
            return resp;

        } finally {
            if (resultReview != null) {
                if (CollectionUtils.isNotEmpty(resultReview.getReviewRecords())) { // add header row
                    resultReview.addRecord(generateSynchReviewRecord(lineHeader, true));
                }
            }

            closeConnection();
        }

        log.debug("LDAP SYNCHRONIZATION COMPLETE^^^^^^^^");

        SyncResponse resp = new SyncResponse(ResponseStatus.SUCCESS);
        resp.setLastRecProcessed(lastRecProcessed);
        return resp;

    }

    private String[] getAttributeIds(SynchConfig config) {
        String attrIds[] = {"*", "modifyTimestamp", "createTimestamp"};
        if (StringUtils.isNotEmpty(config.getAttributeNamesLookup())) {
            Object attrNames = new ArrayList<String>();
            if (StringUtils.isNotBlank(config.getAttributeNamesLookup())) {
                try {
                    Map<String, Object> binding = new HashMap<String, Object>();
                    binding.put("config", config);
                    Map<String, Object> bindingMap = new HashMap<String, Object>();
                    bindingMap.put("binding", binding);
                    AttributeNamesLookupService lookupScript =
                            (AttributeNamesLookupService) scriptRunner.instantiateClass(bindingMap,
                                    config.getAttributeNamesLookup());
                    attrNames = lookupScript.lookupPolicyMapAttributes(bindingMap);
                } catch (Exception e) {
                    log.error("Can't execute script", e);
                }
            }

            List<String> attributeNames = new ArrayList<String>();
            if (attrNames instanceof List) {
                attributeNames = (List) attrNames;
            } else if (attrNames instanceof Map) {
                Map<String, String> attrNamesMap = (Map<String, String>) attrNames;
                attributeNames = new ArrayList(attrNamesMap.keySet());
            }

            if (CollectionUtils.isNotEmpty(attributeNames)) {
                attrIds = attributeNames.toArray(new String[0]);
            }
        }
        return attrIds;
    }

    public Response testConnection(SynchConfig config) {
        try {
            if (connect(config)) {
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

        } finally {
            closeConnection();
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
