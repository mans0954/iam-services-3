package org.openiam.idm.srvc.synch.srcadapter;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.mngsys.service.AttributeNamesLookupService;
import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;
import org.openiam.idm.srvc.synch.dto.LineObject;
import org.openiam.idm.srvc.synch.dto.SyncResponse;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.dto.SynchReview;
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
import org.openiam.idm.srvc.synch.service.TransformScript;
import org.openiam.idm.srvc.synch.service.ValidationScript;
import org.springframework.beans.factory.annotation.Value;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
        if(log.isDebugEnabled()) {
            log.debug("LDAP startSynch CALLED.^^^^^^^^");
        }

        SyncResponse res;
        SynchReview review = null;
        if (sourceReview != null) {
            review = synchReviewDozerConverter.convertToDTO(sourceReview, false);
        }

        LdapContext ctx = null;

        try {

            final ValidationScript validationScript = org.mule.util.StringUtils.isNotEmpty(config.getValidationRule()) ? SynchScriptFactory.createValidationScript(config, review) : null;
            final List<TransformScript> transformScripts = SynchScriptFactory.createTransformationScript(config, review);
            final MatchObjectRule matchRule = matchRuleFactory.create(config.getCustomMatchRule()); // check if matchRule exists

            if (validationScript == null || transformScripts == null || matchRule == null) {
                res = new SyncResponse(ResponseStatus.FAILURE);
                res.setErrorText("The problem in initialization of LDAPAdapter, please check validationScript= " + validationScript + ", transformScripts=" + transformScripts + ", matchRule=" + matchRule + " all must be set!");
                res.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
                return res;
            }

            if (sourceReview != null && !sourceReview.isSourceRejected()) {
                return startSynchReview(config, sourceReview, resultReview, validationScript, transformScripts, matchRule);
            }

            ctx = connect(config);
            if (ctx == null) {
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
            if ("INCREMENTAL".equalsIgnoreCase(config.getSynchType())) {
                lastRecProcessed = config.getLastRecProcessed();
                if (StringUtils.isBlank(lastRecProcessed)) {
                    lastRecProcessed = getNullDate();
                }
                //looking for filter like (&(objectclass=user)(modifyTimeStamp>=?))
                String ldapFilterQuery = config.getQuery();
                config.setQuery(ldapFilterQuery.replace("?", lastRecProcessed));
                if(log.isDebugEnabled()) {
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
                if(log.isDebugEnabled()) {
                    log.debug("========== Processed: " + totalRecords + " records");
                }
                //TimeOut Error String attrIds[] = {"objectClass",""1.1,"+","*"};
                //  TimeOut Error String attrIds[] = {"objectClass", "*", "accountUnlockTime", "aci", "aclRights", "aclRightsInfo", "altServer", "attributeTypes", "changeHasReplFixupOp", "changeIsReplFixupOp", "copiedFrom", "copyingFrom", "createTimestamp", "creatorsName", "deletedEntryAttrs", "dITContentRules", "dITStructureRules", "dncomp", "ds-pluginDigest", "ds-pluginSignature", "ds6ruv", "dsKeyedPassword", "entrydn", "entryid", "hasSubordinates", "idmpasswd", "isMemberOf", "ldapSchemas", "ldapSyntaxes", "matchingRules", "matchingRuleUse", "modDNEnabledSuffixes", "modifiersName", "modifyTimestamp", "nameForms", "namingContexts", "nsAccountLock", "nsBackendSuffix", "nscpEntryDN", "nsds5ReplConflict", "nsIdleTimeout", "nsLookThroughLimit", "nsRole", "nsRoleDN", "nsSchemaCSN", "nsSizeLimit", "nsTimeLimit", "nsUniqueId", "numSubordinates", "objectClasses", "parentid", "passwordAllowChangeTime", "passwordExpirationTime", "passwordExpWarned", "passwordHistory", "passwordPolicySubentry", "passwordRetryCount", "pwdAccountLockedTime", "pwdChangedTime", "pwdFailureTime", "pwdGraceUseTime", "pwdHistory", "pwdLastAuthTime", "pwdPolicySubentry", "pwdReset", "replicaIdentifier", "replicationCSN", "retryCountResetTime", "subschemaSubentry", "supportedControl", "supportedExtension", "supportedLDAPVersion", "supportedSASLMechanisms", "supportedSSLCiphers", "targetUniqueId", "vendorName", "vendorVersion"};

                String[] attrIds = getAttributeIds(config);

                SearchControls searchCtls = new SearchControls();

                ctx.setRequestControls(new Control[]{new PagedResultsControl(PAGE_SIZE, Control.NONCRITICAL)});

                searchCtls.setTimeLimit(TIME_LIMIT);
                searchCtls.setCountLimit(SIZE_LIMIT);
                searchCtls.setSearchScope(config.getSearchScope().ordinal());
                searchCtls.setReturningAttributes(attrIds);
                if(log.isDebugEnabled()) {
                    log.debug("Search: base dn=" + baseou + ", filter= " + config.getQuery() + ", attributes=" + attrIds);
                }
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
                        if(log.isDebugEnabled()) {
                            log.debug("LAST LDAP SYNC COUNTERS: TotalRecords="+totalRecords+"");
                        }
                        SearchResult sr = (SearchResult) results.nextElement();
                        if(log.isDebugEnabled()) {
                            log.debug("SearchResultElement   : " + sr.getName());
                            log.debug("Attributes: " + sr.getAttributes());
                        }
                        LineObject rowObj = new LineObject();
                        if(log.isDebugEnabled()) {
                            log.debug("-New Row to Synchronize --" + ctr++);
                        }
                        Attributes attrs = sr.getAttributes();

                        if (attrs != null) {
                            for (NamingEnumeration ae = attrs.getAll(); ae.hasMore(); ) {
                                javax.naming.directory.Attribute attr = (javax.naming.directory.Attribute) ae.next();
                                List<String> valueList = new ArrayList<String>();
                                String key = attr.getID();
                                if(log.isDebugEnabled()) {
                                    log.debug("attribute id=: " + key);
                                }
                                for (NamingEnumeration e = attr.getAll(); e.hasMore(); ) {
                                    Object o = e.next();
                                    if(o instanceof byte[]){
                                        valueList.add(Hex.encodeHexString((byte[])o));
                                        if(log.isDebugEnabled()) {
                                            log.debug("- value:=" + Hex.encodeHexString((byte[]) o));
                                        }
                                    } else if (o.toString() != null) {
                                        valueList.add(o.toString());
                                        if(log.isDebugEnabled()) {
                                            log.debug("- value:=" + o.toString());
                                        }
                                    }
                                }
                                if (valueList.size() > 0) {
                                    org.openiam.idm.srvc.synch.dto.Attribute rowAttr = new org.openiam.idm.srvc.synch.dto.Attribute();
                                    rowAttr.populateAttribute(key, valueList);
                                    rowObj.put(key, rowAttr);
                                } else {
                                    if(log.isDebugEnabled()) {
                                        log.debug("- value is null");
                                    }
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
                    if(log.isDebugEnabled()) {
                        log.debug("LDAP Search PAGE RESULT: Page=" + pageCounter + ", rows= " + pageRowCount + " have been processed.");
                    }
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

                if(log.isDebugEnabled()) {
                    log.debug("Search ldap result OU=" + baseou + " found = " + recordsInOUCounter + " records.");
                }
            }
            for (LineObject rowObj : processingData) {
                processLineObject(rowObj, config, resultReview, validationScript, transformScripts, matchRule);
                Thread.sleep(100);
            }
            if(log.isDebugEnabled()) {
                log.debug("EXECUTION TIME: "+(System.currentTimeMillis()-startTime));
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
            if(log.isDebugEnabled()) {
                log.debug("LDAP SYNCHRONIZATION COMPLETE WITH ERRORS ^^^^^^^^");
            }
            return resp;

        }  catch (NamingException ne) {
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
            closeConnection(ctx);
        }
        if(log.isDebugEnabled()) {
            log.debug("LDAP SYNCHRONIZATION COMPLETE^^^^^^^^");
        }

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
        LdapContext ctx = null;
        try {
            ctx = connect(config);
            if (ctx != null) {
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
            closeConnection(ctx);
        }
    }

    protected LdapContext connect(SynchConfig config) throws NamingException {

        Hashtable<String, String> envDC = new Hashtable<String, String>(11);
        System.setProperty("javax.net.ssl.trustStore", keystore);

        String hostUrl = config.getSrcHost(); //   managedSys.getHostUrl();
        if(log.isDebugEnabled()) {
            log.debug("Directory host url:" + hostUrl);
        }

        envDC.put(Context.PROVIDER_URL, hostUrl);
        envDC.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        envDC.put(Context.SECURITY_AUTHENTICATION, "simple"); // simple
        envDC.put("java.naming.ldap.attributes.binary", "objectGUID");
        envDC.put(Context.SECURITY_PRINCIPAL, config.getSrcLoginId());  //"administrator@diamelle.local"
        envDC.put(Context.SECURITY_CREDENTIALS, config.getSrcPassword());
        //    envDC.put(Context.BATCHSIZE, "100");
        //   envDC.put("com.sun.jndi.ldap.read.timeout", "60000");

        if (hostUrl.toLowerCase().contains("ldaps")) {
            envDC.put(Context.SECURITY_PROTOCOL, "SSL");
        }

        return new InitialLdapContext(envDC, null);

    }

    protected void closeConnection(LdapContext ctx) {
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

    protected abstract String getNullDate();

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
