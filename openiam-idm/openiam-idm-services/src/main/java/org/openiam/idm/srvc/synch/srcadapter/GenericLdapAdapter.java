package org.openiam.idm.srvc.synch.srcadapter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.mngsys.service.AttributeNamesLookupService;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;
import org.openiam.idm.srvc.synch.dto.LineObject;
import org.openiam.idm.srvc.synch.dto.SyncResponse;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.*;
import java.io.IOException;
import java.util.*;

public abstract class GenericLdapAdapter extends AbstractSrcAdapter {

        /*
     * The flags for the running tasks are handled by this Thread-Safe Set.
     * It stores the taskIds of the currently executing tasks.
     * This is faster and as reliable as storing the flags in the database,
     * if the tasks are only launched from ONE host in a clustered environment.
     * It is unique for each class-loader, which means unique per war-deployment.
     */

    @Value("${KEYSTORE}")
    private String keystore;

    private final static int PAGE_SIZE = 1000;
    private final static int SIZE_LIMIT = 10000;
    private final static int TIME_LIMIT = 0;    //indefinitely

    private LdapContext ctx = null;

    private static final Log log = LogFactory.getLog(LdapAdapter.class);

    @Override
    public SyncResponse startSynch(final SynchConfig config) {
        return startSynch(config, null, null);
    }

    @Override
    public SyncResponse startSynch(SynchConfig config, SynchReviewEntity sourceReview, SynchReviewEntity resultReview) {

        LineObject lineHeader = null;
        double mostRecentRecord = 0L;
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
            if (StringUtils.isNotBlank(config.getLastRecProcessed())) {
                lastRecProcessed = config.getLastRecProcessed();
                // get change log field
                if ("INCREMENTAL".equalsIgnoreCase(config.getSynchType())) {
                    // update the search filter so that it has the new time
                    String ldapFilterQuery = config.getQuery();
                    // replace wildcards with the last exec time
                    //looking for filter like (&(objectclass=user)(modifyTimeStamp>=?))
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
            long startTime = System.currentTimeMillis();

            //Cash for records from LDAP
            // we need it to same LDAP connection
            List<LineObject> processingData = new LinkedList<LineObject>();
            for (String baseou : ouByParent) {
                int recordsInOUCounter = 0;

                log.debug("========== Processed: " + totalRecords + " records");
                //TimeOut Error String attrIds[] = {"objectClass",""1.1,"+","*"};
                //  TimeOut Error String attrIds[] = {"objectClass", "*", "accountUnlockTime", "aci", "aclRights", "aclRightsInfo", "altServer", "attributeTypes", "changeHasReplFixupOp", "changeIsReplFixupOp", "copiedFrom", "copyingFrom", "createTimestamp", "creatorsName", "deletedEntryAttrs", "dITContentRules", "dITStructureRules", "dncomp", "ds-pluginDigest", "ds-pluginSignature", "ds6ruv", "dsKeyedPassword", "entrydn", "entryid", "hasSubordinates", "idmpasswd", "isMemberOf", "ldapSchemas", "ldapSyntaxes", "matchingRules", "matchingRuleUse", "modDNEnabledSuffixes", "modifiersName", "modifyTimestamp", "nameForms", "namingContexts", "nsAccountLock", "nsBackendSuffix", "nscpEntryDN", "nsds5ReplConflict", "nsIdleTimeout", "nsLookThroughLimit", "nsRole", "nsRoleDN", "nsSchemaCSN", "nsSizeLimit", "nsTimeLimit", "nsUniqueId", "numSubordinates", "objectClasses", "parentid", "passwordAllowChangeTime", "passwordExpirationTime", "passwordExpWarned", "passwordHistory", "passwordPolicySubentry", "passwordRetryCount", "pwdAccountLockedTime", "pwdChangedTime", "pwdFailureTime", "pwdGraceUseTime", "pwdHistory", "pwdLastAuthTime", "pwdPolicySubentry", "pwdReset", "replicaIdentifier", "replicationCSN", "retryCountResetTime", "subschemaSubentry", "supportedControl", "supportedExtension", "supportedLDAPVersion", "supportedSASLMechanisms", "supportedSSLCiphers", "targetUniqueId", "vendorName", "vendorVersion"};

                String[] attrIds = getAttributeIds(config);

                SearchControls searchCtls = new SearchControls();

                ctx.setRequestControls(new Control[]{new PagedResultsControl(PAGE_SIZE, Control.NONCRITICAL)});

                searchCtls.setTimeLimit(TIME_LIMIT);
                searchCtls.setCountLimit(SIZE_LIMIT);
                searchCtls.setSearchScope(config.getSearchScope().ordinal());
                searchCtls.setReturningAttributes(attrIds);

                log.debug("Search: base dn=" + baseou + ", filter= " + config.getQuery() + ", attributes=" + attrIds);
                byte[] cookie = null;
                int pageCounter = 0;
                int pageRowCount = 0;
                NamingEnumeration results = null;
                do {
                    pageCounter++;
                    pageRowCount = 0;
                    try{
                        results = ctx.search(baseou, config.getQuery(), searchCtls);
                    } catch(ServiceUnavailableException sux){
                        log.error(sux);
                        break;
                    }
                    while (results != null && results.hasMoreElements()) {
                        pageRowCount++;
                        totalRecords++;
                        recordsInOUCounter++;
                        System.out.println("LAST LDAP SYNC COUNTERS: TotalRecords="+totalRecords+"");
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
                        if (mostRecentRecord < lrt.getMostRecentRecord()) {
                            mostRecentRecord = lrt.getMostRecentRecord();
                            lastRecProcessed = lrt.getGeneralizedTime();
                        }

                        if (lineHeader == null) {
                            lineHeader = rowObj; // get first row
                        }
                        processingData.add(rowObj);

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
            for (LineObject rowObj : processingData) {
                processLineObject(rowObj, config, resultReview);
                Thread.sleep(100);
            }
            System.out.println("EXECUTION TIME: "+(System.currentTimeMillis()-startTime));

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

        } catch (InterruptedException e) {
            e.printStackTrace();
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

    abstract protected String[] getDirAttrIds();

    protected String[] getAttributeIds(SynchConfig config) {
        String attrIds[] = getDirAttrIds();
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
                attributeNames = (List<String>) attrNames;
            } else if (attrNames instanceof Map) {
                Map<String, String> attrNamesMap = (Map<String, String>) attrNames;
                attributeNames = new ArrayList<String>(attrNamesMap.keySet());
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

    protected boolean connect(SynchConfig config) throws NamingException {

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

    protected void closeConnection() {
        try {
            if (ctx != null) {
                ctx.close();
            }

        } catch (NamingException ne) {
            log.error(ne.getMessage(), ne);
            ne.printStackTrace();
        }
    }

    protected abstract LastRecordTime getRowTime(LineObject rowObj);

    protected LastRecordTime getTime(org.openiam.idm.srvc.synch.dto.Attribute atr) {
        LastRecordTime lrt = new LastRecordTime();

        String s = atr.getValue();
        int i = s.indexOf("Z");
        if (i == -1) {
            i = s.indexOf("-");
        }
        if (i > 0) {
            lrt.setMostRecentRecord(Double.parseDouble(s.substring(0, i)));
            lrt.setGeneralizedTime(atr.getValue());
            return lrt;

        }
        lrt.setMostRecentRecord(Double.parseDouble(s));
        lrt.setGeneralizedTime(atr.getValue());

        return lrt;
    }

}