package org.openiam.imprt;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.synch.domain.SynchConfigEntity;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.jdbc.DataSource;
import org.openiam.imprt.jdbc.parser.impl.SyncConfigEntityParser;
import org.openiam.imprt.jdbc.parser.impl.UserEntityParser;
import org.openiam.imprt.key.KeyManagementWSClient;
import org.openiam.imprt.model.Attribute;
import org.openiam.imprt.model.LineObject;
import org.openiam.imprt.query.SelectQueryBuilder;
import org.openiam.imprt.util.DataHolder;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by alexander on 27/04/16.
 */
public class ImportProcessor {
    private final static int PAGE_SIZE = 1000;
    private final static int SIZE_LIMIT = 10000;
    private final static int TIME_LIMIT = 0;

    public ImportProcessor() {
        init();
    }

    private void init() {
        try {
            String confPath = DataHolder.getInstance().getProperty(ImportPropertiesKey.CONF_PATH);
            // load database props
            InputStream in = new FileInputStream(confPath + "/conf/datasource.properties");
            DataHolder.getInstance().loadProperties(in);
            // load default properties
            in = Import.class.getClassLoader().getResourceAsStream("default.properties");
            DataHolder.getInstance().loadProperties(in);

            String esbLocation = DataHolder.getInstance().getProperty(ImportPropertiesKey.WEB_SERVER_URL);
            if (!esbLocation.contains("openiam-esb/idmsrvc")) {
                esbLocation += "/openiam-esb/idmsrvc/";
            }
            DataHolder.getInstance().setProperty(ImportPropertiesKey.KEY_SERVICE_WSDL, esbLocation + "KeyManagementWS?wsdl");
            DataHolder.getInstance().setProperty(ImportPropertiesKey.KEYSTORE, confPath + "/conf/cacerts");
            // init data source.
            DataSource.getInstance().initialize();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void start() throws Exception {
        // get syncConfig

        SynchConfigEntity syncConfig = new SyncConfigEntityParser().getById(DataHolder.getInstance().getProperty(ImportPropertiesKey.SYNC_CONFIG));

        System.out.println("syncConfig loaded : " + syncConfig.getBaseDn());
        // get data from AD

        LineObject lineHeader = null;
        LdapContext ctx = null;
        ctx = connect(syncConfig);
        int totalRecords = 0;
        List<LineObject> processingData = new LinkedList<LineObject>();
        List<String> ouByParent = new LinkedList<String>();
        if (syncConfig.getBaseDn().contains(";")) {
            for (String basedn : syncConfig.getBaseDn().split(";")) {
                ouByParent.add(basedn.trim());
            }
        } else {
            ouByParent.add(syncConfig.getBaseDn().trim());
        }
        for (String baseou : ouByParent) {
            int recordsInOUCounter = 0;
            //TimeOut Error String attrIds[] = {"objectClass",""1.1,"+","*"};
            //  TimeOut Error String attrIds[] = {"objectClass", "*", "accountUnlockTime", "aci", "aclRights", "aclRightsInfo", "altServer", "attributeTypes", "changeHasReplFixupOp", "changeIsReplFixupOp", "copiedFrom", "copyingFrom", "createTimestamp", "creatorsName", "deletedEntryAttrs", "dITContentRules", "dITStructureRules", "dncomp", "ds-pluginDigest", "ds-pluginSignature", "ds6ruv", "dsKeyedPassword", "entrydn", "entryid", "hasSubordinates", "idmpasswd", "isMemberOf", "ldapSchemas", "ldapSyntaxes", "matchingRules", "matchingRuleUse", "modDNEnabledSuffixes", "modifiersName", "modifyTimestamp", "nameForms", "namingContexts", "nsAccountLock", "nsBackendSuffix", "nscpEntryDN", "nsds5ReplConflict", "nsIdleTimeout", "nsLookThroughLimit", "nsRole", "nsRoleDN", "nsSchemaCSN", "nsSizeLimit", "nsTimeLimit", "nsUniqueId", "numSubordinates", "objectClasses", "parentid", "passwordAllowChangeTime", "passwordExpirationTime", "passwordExpWarned", "passwordHistory", "passwordPolicySubentry", "passwordRetryCount", "pwdAccountLockedTime", "pwdChangedTime", "pwdFailureTime", "pwdGraceUseTime", "pwdHistory", "pwdLastAuthTime", "pwdPolicySubentry", "pwdReset", "replicaIdentifier", "replicationCSN", "retryCountResetTime", "subschemaSubentry", "supportedControl", "supportedExtension", "supportedLDAPVersion", "supportedSASLMechanisms", "supportedSSLCiphers", "targetUniqueId", "vendorName", "vendorVersion"};


            SearchControls searchCtls = new SearchControls();

            ctx.setRequestControls(new Control[]{new PagedResultsControl(PAGE_SIZE, Control.NONCRITICAL)});

            searchCtls.setTimeLimit(TIME_LIMIT);
            searchCtls.setCountLimit(SIZE_LIMIT);
            searchCtls.setSearchScope(syncConfig.getSearchScope().ordinal());
            byte[] cookie = null;
            int pageCounter = 0;
            int pageRowCount = 0;
            NamingEnumeration results = null;
            do {
                pageCounter++;
                pageRowCount = 0;
                try {
                    results = ctx.search(baseou, syncConfig.getQuery(), searchCtls);
                } catch (ServiceUnavailableException sux) {
                    break;
                }
                while (results != null && results.hasMoreElements()) {
                    pageRowCount++;
                    totalRecords++;
                    recordsInOUCounter++;
                    SearchResult sr = (SearchResult) results.nextElement();
                    LineObject rowObj = new LineObject();
                    Attributes attrs = sr.getAttributes();

                    if (attrs != null) {
                        for (NamingEnumeration ae = attrs.getAll(); ae.hasMore(); ) {
                            javax.naming.directory.Attribute attr = (javax.naming.directory.Attribute) ae.next();
                            List<String> valueList = new ArrayList<String>();
                            String key = attr.getID();
                            for (NamingEnumeration e = attr.getAll(); e.hasMore(); ) {
                                Object o = e.next();
                                if (o instanceof byte[]) {
                                    valueList.add(Hex.encodeHexString((byte[]) o));
                                } else if (o.toString() != null) {
                                    valueList.add(o.toString());
                                }
                            }
                            if (valueList.size() > 0) {
                                Attribute rowAttr = new Attribute();
                                rowAttr.populateAttribute(key, valueList);
                                rowObj.put(key, rowAttr);
                            }
                        }

                        if (lineHeader == null) {
                            lineHeader = rowObj; // get first row
                        }
                        processingData.add(rowObj);

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
                }
            } while (cookie != null);
        }
        // do transform and insert/update and commit
        // TODO: transform logic here
        // do generate user keys
        KeyManagementWSClient keyManagementWSClient = new KeyManagementWSClient(DataHolder.getInstance().getProperty(ImportPropertiesKey.KEY_SERVICE_WSDL));
        // TODO: need to pass collected user ids on the previos step.
        keyManagementWSClient.generateKeysForUserList(new ArrayList<String>());
        // do reindex
        // for now need to restart jboss ))) not enough time to integrate lucene
    }


    private LdapContext connect(SynchConfigEntity syncConfig) throws NamingException {

        Hashtable<String, String> envDC = new Hashtable<String, String>(11);
        System.setProperty("javax.net.ssl.trustStore", DataHolder.getInstance().getProperty(ImportPropertiesKey.KEYSTORE));

        String hostUrl = syncConfig.getSrcHost(); //   managedSys.getHostUrl();
        envDC.put(Context.PROVIDER_URL, hostUrl);
        envDC.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        envDC.put(Context.SECURITY_AUTHENTICATION, "simple"); // simple
        envDC.put("java.naming.ldap.attributes.binary", "objectGUID");
        envDC.put(Context.SECURITY_PRINCIPAL, syncConfig.getSrcLoginId());  //"administrator@diamelle.local"
        envDC.put(Context.SECURITY_CREDENTIALS, syncConfig.getSrcPassword());
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
            ne.printStackTrace();
        }
    }
}
