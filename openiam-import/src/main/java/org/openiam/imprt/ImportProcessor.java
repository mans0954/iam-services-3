package org.openiam.imprt;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.id.UUIDGen;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.loc.domain.LocationEntity;
import org.openiam.idm.srvc.loc.dto.Location;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.OrganizationUserEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.synch.domain.SynchConfigEntity;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.custom.MailboxHelper;
import org.openiam.imprt.jdbc.DataSource;
import org.openiam.imprt.jdbc.parser.impl.*;
import org.openiam.imprt.key.KeyManagementWSClient;
import org.openiam.imprt.model.Attribute;
import org.openiam.imprt.model.LastRecordTime;
import org.openiam.imprt.model.LineObject;
import org.openiam.imprt.query.Restriction;
import org.openiam.imprt.query.SelectQueryBuilder;
import org.openiam.imprt.query.expression.Column;
import org.openiam.imprt.util.DataHolder;
import org.openiam.imprt.util.Transformation;
import org.openiam.imprt.util.Utils;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.*;
import java.io.*;
import java.util.*;

/**
 * Created by alexander on 27/04/16.
 */
public class ImportProcessor {
    private final static int PAGE_SIZE = 1000;
    private final static int SIZE_LIMIT = 10000;
    private final static int TIME_LIMIT = 0;
    private final static boolean debugMode = true;
    double mostRecentRecord = 0L;
    String lastRecProcessed = null;

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
        String query = syncConfig.getQuery();
        // get the last execution time
        if ("INCREMENTAL".equalsIgnoreCase(syncConfig.getSynchType())) {
            lastRecProcessed = syncConfig.getLastRecProcessed();
            if (StringUtils.isBlank(lastRecProcessed)) {
                lastRecProcessed = getNullDate();
            }
            //looking for filter like (&(objectclass=user)(modifyTimeStamp>=?))
            query = query.replace("?", lastRecProcessed);
        }
        int filesAmount = 0;
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
            //  TimeOut Error String attrIds[] = {"objectClass", "*", "accountUnlockTime", "aci", "aclRights", "aclRightsInfo", "altServer", "attributeTypes", "changeHasReplFixupOp", "changeIsReplFixupOp", "copiedFrom", "copyingFrom", "createTimestamp", "creatorsName", "deletedEntryAttrs", "dITContentRules", "dITStructureRules", "dncomp", "ds-pluginDigest", "ds-pluginSignature", "ds6ruv", "dsKeyedPassword", "entrydn", "entryid", "hasSubordinates", "idmpasswd", "isMemberOf", "ldapSchemas", "ldapSyntaxes", "matchingRules", "matchingRuleUse", "modDNEnabledSuffixes", "modifiersName", "modifyTimeStamp", "nameForms", "namingContexts", "nsAccountLock", "nsBackendSuffix", "nscpEntryDN", "nsds5ReplConflict", "nsIdleTimeout", "nsLookThroughLimit", "nsRole", "nsRoleDN", "nsSchemaCSN", "nsSizeLimit", "nsTimeLimit", "nsUniqueId", "numSubordinates", "objectClasses", "parentid", "passwordAllowChangeTime", "passwordExpirationTime", "passwordExpWarned", "passwordHistory", "passwordPolicySubentry", "passwordRetryCount", "pwdAccountLockedTime", "pwdChangedTime", "pwdFailureTime", "pwdGraceUseTime", "pwdHistory", "pwdLastAuthTime", "pwdPolicySubentry", "pwdReset", "replicaIdentifier", "replicationCSN", "retryCountResetTime", "subschemaSubentry", "supportedControl", "supportedExtension", "supportedLDAPVersion", "supportedSASLMechanisms", "supportedSSLCiphers", "targetUniqueId", "vendorName", "vendorVersion"};


            SearchControls searchCtls = new SearchControls();

            ctx.setRequestControls(new Control[]{new PagedResultsControl(PAGE_SIZE, Control.NONCRITICAL)});

            searchCtls.setTimeLimit(TIME_LIMIT);
            searchCtls.setCountLimit(SIZE_LIMIT);
            searchCtls.setSearchScope(syncConfig.getSearchScope().ordinal());
            searchCtls.setReturningAttributes(this.getDirAttrIds());
            byte[] cookie = null;
            int pageCounter = 0;
            int pageRowCount = 0;
            NamingEnumeration results = null;
            do {
                pageCounter++;
                pageRowCount = 0;
                try {
                    results = ctx.search(baseou, query, searchCtls);
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
                            } else {
                            }
                        }
                    }
                    if (lineHeader == null) {
                        lineHeader = rowObj; // get first row
                    }
                    processingData.add(rowObj);
                    if (processingData.size() > 1000) {
                        this.saveToCacheObject(processingData, "DATA" + filesAmount);
                        System.out.println("Cached users to" + ("DATA" + filesAmount));
                        processingData = new ArrayList<LineObject>();
                        filesAmount++;
                    }
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

        }
        //**************************************************************************************************************
        this.saveToCacheObject(processingData, "DATA" + filesAmount);

        if (CollectionUtils.isEmpty(processingData) && filesAmount == 0) {
            if (debugMode) {
                System.out.println("Nothing FOund in AD!");
            }
            return;
        }
        System.out.println("All users=" + processingData.size());
        if (debugMode) {
            System.out.print("Prefill linked data ");
        }

        // do transform and insert/update and commit
        //Prepare Data
        Map<String, Object> bindingMap = this.readFromCache();
        if (!"On".equals(syncConfig.getCustomAdatperScript()) || bindingMap == null) {
            bindingMap = new HashMap<String, Object>();
        }

        long time1 = System.currentTimeMillis();
// Init Parser for Entitires
        CompanyEntityParser companyEntityParser = new CompanyEntityParser();
        CompanyAttributeEntityParser companyAttributeEntityParser = new CompanyAttributeEntityParser();
        RoleEntityParser roleEntityParser = new RoleEntityParser();
        OrganizationUserEntityParser organizationUserEntityParser = new OrganizationUserEntityParser();
        UserAttributeEntityParser userAttributeEntityParser = new UserAttributeEntityParser();
        GroupEntityParser groupEntityParser = new GroupEntityParser();
        LoginEntityParser loginEntityParser = new LoginEntityParser();
        EmailAddressEntityParser emailAddressEntityParser = new EmailAddressEntityParser();
        GroupAttributeEntityParser groupAttributeEntityParser = new GroupAttributeEntityParser();
        PhoneEntityParser phoneEntityParser = new PhoneEntityParser();
        AddressEntityParser addressEntityParser = new AddressEntityParser();
        //FIXME - PRE IMPORT BLOCK. NEED TO MOVE IT TO SINGLE PLACE
        //FIXME SINGLE BLOKC VVVVVVVVV
        //Init required fields for dependencies
        List<Column> companyColumnList = Utils.getColumns(new ImportPropertiesKey[]{ImportPropertiesKey.COMPANY_COMPANY_ID,
                ImportPropertiesKey.COMPANY_COMPANY_NAME,
                ImportPropertiesKey.COMPANY_INTERNAL_COMPANY_ID});

        List<Column> companyAttributeColumnList = Utils.getColumns(new ImportPropertiesKey[]{ImportPropertiesKey.T_COMPANY_ATTRIBUTE_ID,
                ImportPropertiesKey.T_COMPANY_ATTRIBUTE_COMPANY_ID,
                ImportPropertiesKey.T_COMPANY_ATTRIBUTE_NAME,
                ImportPropertiesKey.T_COMPANY_ATTRIBUTE_VALUE});

        List<Column> groupAttributeColumnList = Utils.getColumns(new ImportPropertiesKey[]{ImportPropertiesKey.T_GRP_ATTRIBUTES_GRP_ID,
                ImportPropertiesKey.T_GRP_ATTRIBUTES_ID,
                ImportPropertiesKey.T_GRP_ATTRIBUTES_NAME,
                ImportPropertiesKey.T_GRP_ATTRIBUTES_ATTR_VALUE});

        // Define SQL query for dependency select
        final String getUserByLoginSQL = "SELECT %s FROM LOGIN l JOIN USERS u ON l.USER_ID=u.USER_ID WHERE l.MANAGED_SYS_ID='0' AND l.LOWERCASE_LOGIN='%s'";
        final String getAllOrganizationsSQL = "SELECT %s FROM COMPANY c WHERE c.ORG_TYPE_ID='ORGANIZATION'";
        final String getChildOrganizationsSQL = "SELECT %s FROM COMPANY c JOIN COMPANY_TO_COMPANY_MEMBERSHIP ccm ON c.COMPANY_ID=ccm.MEMBER_COMPANY_ID WHERE ccm.COMPANY_ID='%s' AND c.ORG_TYPE_ID='SUBSIDIARY'";
        final String getAttributesOrganizationsSQL = "SELECT %s FROM COMPANY_ATTRIBUTE ca WHERE ca.COMPANY_ID='%s'";
        final String getAttributesGroupsSQL = "SELECT %s FROM GRP_ATTRIBUTES ca WHERE ca.GRP_ID='%s'";

        if (bindingMap.get("ORGANIZATIONS") == null) {
            List<OrganizationEntity> organizationEntityList = companyEntityParser.get(String.format(getAllOrganizationsSQL, Utils.columnsToSelectFields(companyColumnList, "c")), companyColumnList);
            if (CollectionUtils.isNotEmpty(organizationEntityList)) {
                for (OrganizationEntity organizationEntity : organizationEntityList) {
                    List<OrganizationEntity> childs = companyEntityParser.get(String.format(getChildOrganizationsSQL, Utils.columnsToSelectFields(companyColumnList, "c"), organizationEntity.getId()), companyColumnList);
                    if (CollectionUtils.isNotEmpty(childs)) {
                        for (OrganizationEntity child : childs) {
                            List<OrganizationAttributeEntity> organizationAttributeEntityList = companyAttributeEntityParser.get(String.format(getAttributesOrganizationsSQL, Utils.columnsToSelectFields(companyAttributeColumnList, "ca"), child.getId()), companyAttributeColumnList);
                            if (CollectionUtils.isNotEmpty(organizationAttributeEntityList)) {
                                child.setAttributes(new HashSet<OrganizationAttributeEntity>(organizationAttributeEntityList));
                            }
                        }
                        organizationEntity.setChildOrganizations(new HashSet<OrganizationEntity>(childs));
                    }
                    System.out.println("Organization processed=" + organizationEntity.getName());
                }
            }
            bindingMap.put("ORGANIZATIONS", organizationEntityList);
            System.out.println("All Organizations processed");
        }

        if (bindingMap.get("GROUPS_MAP") == null || bindingMap.get("GROUPS_MAP_ENTITY") == null) {

            List<GroupEntity> groups = groupEntityParser.getGroupsWithDN();

            System.out.println("All Groups processed");
            System.out.println("Total Groups number=" + groups.size());
            Map<String, GroupEntity> groupsEntitiesMap = new HashMap<String, GroupEntity>();
//            //Build map <groupName,DistrguishedName>
            Map<String, String> groupsMap = new HashMap<String, String>();
            if (CollectionUtils.isNotEmpty(groups)) {
                System.out.println("Total Groups number=" + groups.size());
                for (GroupEntity g : groups) {
                    if (CollectionUtils.isNotEmpty(g.getAttributes())) {
                        for (GroupAttributeEntity gae : g.getAttributes()) {
                            if ("DistinguishedName".equals(gae.getName())) {
                                groupsMap.put(g.getName().toLowerCase(), gae.getValue());
                                groupsEntitiesMap.put(gae.getValue().toLowerCase(), g);
                                break;
                            }
                        }
                    }
                }
            }

            bindingMap.put("GROUPS_MAP", groupsMap);
            bindingMap.put("GROUPS_MAP_ENTITY", groupsEntitiesMap);
            System.out.println("All Groups map processed");
            //Build map <groupName,Group>
        }

        if (bindingMap.get("LOCATIONS") == null) {
            List<LocationEntity> locations = new LocationEntityParser().getAll();
            bindingMap.put("LOCATIONS", locations);
            System.out.println("All Locations processed");
        }
        this.saveToCache(bindingMap);
        MailboxHelper mailboxHelper = new MailboxHelper(skipUTF8BOM("/home/OpenIAM/data/openiam/upload/sync/AN_Exchange_DBs.csv"));
        bindingMap.put("MAILBOX_HELPER", mailboxHelper);


        //FIXME SINGLE BLOCK ^^^^^^^^^^^
        if (debugMode) {
            System.out.println("Time=" + (System.currentTimeMillis() - time1) + "ms");
        }

        // TODO: transform logic here
        UserEntity user = null;
        List<UserEntity> users = null;
        Transformation tr = new Transformation();
        UserEntityParser userEntityParser = new UserEntityParser();
        Attribute sAMAccountNameAttribute = null;
        List<String> newUserIds = new ArrayList<String>();
        int res = 0;
        List<Column> userColumns = Utils.getColumnsForTable(ImportPropertiesKey.USERS);
        for (int i = 0; i <= filesAmount; i++) {
            if (filesAmount != 0) {
                processingData = this.readFromCacheObjects("DATA" + i);
            }
            for (LineObject lo : processingData) {
                sAMAccountNameAttribute = lo.get("sAMAccountName");
                if (sAMAccountNameAttribute != null && sAMAccountNameAttribute.getValue() != null) {
                    time1 = System.currentTimeMillis();
                    //FIXME should be common search in OpenIAM
                    users = userEntityParser.get(
                            String.format(getUserByLoginSQL,
                                    Utils.columnsToSelectFields(ImportPropertiesKey.USERS, "u"),
                                    sAMAccountNameAttribute.getValue()), userColumns);
                    if (CollectionUtils.isEmpty(users)) {
                        user = new UserEntity();
                        user.setOrganizationUser(new HashSet<OrganizationUserEntity>());
                    } else {
                        user = users.get(0);
                        //fill user with data
                        fillUserWithDependenies(user,
                                userEntityParser,
                                loginEntityParser,
                                organizationUserEntityParser,
                                roleEntityParser,
                                groupEntityParser,
                                userAttributeEntityParser,
                                emailAddressEntityParser,
                                phoneEntityParser,
                                addressEntityParser);
                    }
                    LastRecordTime lrt = getRowTime(lo);
                    if (mostRecentRecord < lrt.getMostRecentRecord()) {
                        mostRecentRecord = lrt.getMostRecentRecord();
                        lastRecProcessed = lrt.getGeneralizedTime();
                    }
                    res = tr.execute(lo, user, bindingMap);
                    if (res == -1) {
                        System.out.println("Fail Transform for " + sAMAccountNameAttribute.getValue());
                    }

                    saveChanges(newUserIds, user,
                            userEntityParser,
                            loginEntityParser,
                            userAttributeEntityParser,
                            emailAddressEntityParser,
                            phoneEntityParser,
                            addressEntityParser);
                    if (debugMode) {
                        System.out.println("User " + sAMAccountNameAttribute.getValue() + " processing time=" + (System.currentTimeMillis() - time1) + "ms");
                    }
                }
            }
        }
        //**************************************************************************************************************
        syncConfig.setLastRecProcessed(lastRecProcessed);
        new

                SyncConfigEntityParser()

                .

                        update(syncConfig, syncConfig.getSynchConfigId()

                        );
        // do generate user keys
        KeyManagementWSClient keyManagementWSClient = new KeyManagementWSClient(DataHolder.getInstance().getProperty(ImportPropertiesKey.KEY_SERVICE_WSDL));
        keyManagementWSClient.generateKeysForUserList(newUserIds);
        // do reindex
        // for now need to restart jboss ))) not enough time to integrate lucene
    }

    private void fillUserWithDependenies(UserEntity user,
                                         UserEntityParser userEntityParser,
                                         LoginEntityParser loginEntityParser,
                                         OrganizationUserEntityParser organizationUserEntityParser,
                                         RoleEntityParser roleEntityParser,
                                         GroupEntityParser groupEntityParser,
                                         UserAttributeEntityParser userAttributeEntityParser,
                                         EmailAddressEntityParser emailAddressEntityParser,
                                         PhoneEntityParser phoneEntityParser,
                                         AddressEntityParser addressEntityParser) throws Exception {
        final String getUserOrganizationsSQL = "SELECT %s FROM USER_AFFILIATION c WHERE c.USER_ID='%s'";
        final String getUserAttributesSQL = "SELECT %s FROM USER_ATTRIBUTES c WHERE c.USER_ID='%s'";
        final String getUserRoleSQL = "SELECT %s FROM ROLE c JOIN USER_ROLE j on j.ROLE_ID=c.ROLE_ID WHERE j.USER_ID='%s'";
        final String getUserGroupSQL = "SELECT %s FROM GRP c JOIN USER_GRP j on j.GRP_ID=c.GRP_ID WHERE j.USER_ID='%s'";
        final String getEmailsSQL = "SELECT %s FROM EMAIL_ADDRESS c WHERE  c.PARENT_ID='%s'";
        final String getPhonesSQL = "SELECT %s FROM PHONE c WHERE  c.PARENT_ID='%s'";
        final String getAddressesSQL = "SELECT %s FROM ADDRESS c WHERE  c.PARENT_ID='%s'";
        final String getSupervisorsSQL = "SELECT %s FROM USERS u JOIN ORG_STRUCTURE os ON os.SUPERVISOR_ID = u.USER_ID  WHERE os.STAFF_ID='%s'";

        final List<Column> organizationUserColumnList = Utils.getColumns(new ImportPropertiesKey[]{ImportPropertiesKey.USER_AFFILIATION_COMPANY_ID,
                ImportPropertiesKey.USER_AFFILIATION_METADATA_TYPE_ID});

        final List<Column> userAttributeColumnList = Utils.getColumns(new ImportPropertiesKey[]{ImportPropertiesKey.USER_ATTRIBUTES_ID, ImportPropertiesKey.USER_ATTRIBUTES_USER_ID,
                ImportPropertiesKey.USER_ATTRIBUTES_NAME,
                ImportPropertiesKey.USER_ATTRIBUTES_VALUE});

        final List<Column> roleColumnList = Utils.getColumns(new ImportPropertiesKey[]{ImportPropertiesKey.ROLE_ROLE_ID,
                ImportPropertiesKey.ROLE_ROLE_NAME,
                ImportPropertiesKey.ROLE_TYPE_ID,
                ImportPropertiesKey.ROLE_DESCRIPTION,
        });

        final List<Column> groupColumnList = Utils.getColumns(new ImportPropertiesKey[]{ImportPropertiesKey.GRP_GRP_ID,
                ImportPropertiesKey.GRP_GRP_NAME,
                ImportPropertiesKey.GRP_GROUP_DESC
        });

        final List<Column> supervisorsColumns = Utils.getColumns(new ImportPropertiesKey[]{ImportPropertiesKey.USERS_USER_ID,
                ImportPropertiesKey.USERS_EMPLOYEE_ID
        });

        //add  Organizations
        List<OrganizationUserEntity> organizationUserEntityList = organizationUserEntityParser.get(
                String.format(getUserOrganizationsSQL, Utils.columnsToSelectFields(organizationUserColumnList, "c"),
                        user.getId()),
                organizationUserColumnList
        );
        if (organizationUserEntityList != null) {
            user.setOrganizationUser(new HashSet<OrganizationUserEntity>(organizationUserEntityList));
        }

        //add User Attributes
        List<UserAttributeEntity> userAttributeEntityList = userAttributeEntityParser.get(
                String.format(getUserAttributesSQL, Utils.columnsToSelectFields(userAttributeColumnList, "c"),
                        user.getId()), userAttributeColumnList);
        if (userAttributeEntityList != null) {
            for (UserAttributeEntity ua : userAttributeEntityList) {
                user.getUserAttributes().put(ua.getName(), ua);
            }
        }
        //add Roles
        List<RoleEntity> roleEntityList = roleEntityParser.get(String.format(getUserRoleSQL, Utils.columnsToSelectFields(roleColumnList, "c"),
                user.getId()), roleColumnList);

        if (roleEntityList != null) {
            user.setRoles(new HashSet<RoleEntity>(roleEntityList));
        }

        //add Groups
        List<GroupEntity> groupEntityList = groupEntityParser.get(String.format(getUserGroupSQL, Utils.columnsToSelectFields(groupColumnList, "c"),
                user.getId()), groupColumnList);

        if (groupEntityList != null) {
            user.setGroups(new HashSet<GroupEntity>(groupEntityList));
        }

        //addLogins
        List<LoginEntity> loginEntityList = loginEntityParser.get(Restriction.eq(ImportPropertiesKey.LOGIN_USER_ID, user.getId()));
        user.setPrincipalList(loginEntityList);

        List<EmailAddressEntity> emailAddressEntities = emailAddressEntityParser.get(String.format(getEmailsSQL, Utils.columnsToSelectFields(ImportPropertiesKey.EMAIL_ADDRESS, "c"),
                user.getId()), Utils.getColumnsForTable(ImportPropertiesKey.EMAIL_ADDRESS));
        if (emailAddressEntities != null) {
            user.setEmailAddresses(new HashSet<EmailAddressEntity>(emailAddressEntities));
        }

        List<PhoneEntity> phoneEntities = phoneEntityParser.get(String.format(getPhonesSQL, Utils.columnsToSelectFields(ImportPropertiesKey.PHONE, "c"),
                user.getId()), Utils.getColumnsForTable(ImportPropertiesKey.PHONE));
        if (phoneEntities != null) {
            user.setPhones(new HashSet<PhoneEntity>(phoneEntities));
        }

        List<AddressEntity> address = addressEntityParser.get(String.format(getAddressesSQL, Utils.columnsToSelectFields(ImportPropertiesKey.ADDRESS, "c"),
                user.getId()), Utils.getColumnsForTable(ImportPropertiesKey.ADDRESS));
        if (address != null) {
            user.setAddresses(new HashSet<AddressEntity>(address));
        }

        List<UserEntity> supervisors = userEntityParser.get(String.format(getSupervisorsSQL, Utils.columnsToSelectFields(supervisorsColumns, "u"),
                user.getId()), supervisorsColumns);
        if (supervisors != null) {
            Set<SupervisorEntity> supervisorEntities = new HashSet<SupervisorEntity>();
            for (UserEntity ue : supervisors) {
                SupervisorEntity se = new SupervisorEntity();
                se.setSupervisor(ue);
                supervisorEntities.add(se);
            }
            user.setSupervisors(supervisorEntities);
        }

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

    // Skip UTF8_BOM
    private InputStream skipUTF8BOM(String inputFile) throws Exception {
        InputStream input = new FileInputStream(inputFile);
        final byte[] UTF8_BOM = new byte[3];
        UTF8_BOM[0] = (byte) 0xEF;
        UTF8_BOM[1] = (byte) 0xBB;
        UTF8_BOM[2] = (byte) 0xBF;
        byte[] head = new byte[3];
        int read = input.read(head, 0, 3);
        if (read != 3 || !Arrays.equals(head, UTF8_BOM)) {
            input.close();
            input = new FileInputStream(inputFile);
        }
        return input;
    }

    private void saveChanges(List<String> newUserIds, UserEntity user, UserEntityParser userEntityParser,
                             LoginEntityParser loginEntityParser,
                             UserAttributeEntityParser userAttributeEntityParser,
                             EmailAddressEntityParser emailAddressEntityParser,
                             PhoneEntityParser phoneEntityParser,
                             AddressEntityParser addressEntityParser) {


        String userId = null;
        int retVal = 0;
        if (user.getId() == null) {
            userId = UUIDGen.getUUID();
            newUserIds.add(userId);
            user.setId(userId);
            retVal = userEntityParser.add(user);
        } else {
            userId = user.getId();
            retVal = userEntityParser.update(user, userId);
        }
        if (retVal == 0) {
            try {
                saveLogins(user, loginEntityParser);
            } catch (Exception e) {
                System.out.println("Can't save logins!");
            }

            try {
                saveUserAttributes(user, userAttributeEntityParser);
            } catch (Exception e) {
                System.out.println("Can't save UserAttributes!");
            }

            try {
                saveEmails(user, emailAddressEntityParser);
            } catch (Exception e) {
                System.out.println("Can't save emailAddress!");
            }
            try {
                savePhones(user, phoneEntityParser);
            } catch (Exception e) {
                System.out.println("Can't save phoneEntity!");
            }

            try {
                saveAddressEntity(user, addressEntityParser);
            } catch (Exception e) {
                System.out.println("Can't save AddressEntity!");
            }
            saveUserGroups(user, userEntityParser);
            saveUserRoles(user, userEntityParser);
            saveUserOrganizations(user, userEntityParser);
            saveUserSupervisors(user, userEntityParser);
        } else {
            System.out.println("Break save. See problem above ^");
        }
    }

    private void saveLogins(UserEntity user, LoginEntityParser loginEntityParser) throws Exception {
        List<LoginEntity> forADD = new ArrayList<LoginEntity>();
        Map<String, LoginEntity> forUpdate = new HashMap<String, LoginEntity>();
        if (user.getPrincipalList() != null) {
            for (LoginEntity loginEntity : user.getPrincipalList()) {
                loginEntity.setLowerCaseLogin(loginEntity.getLogin().toLowerCase());
                loginEntity.setUserId(user.getId());
                if (loginEntity.getLoginId() == null) {
                    loginEntity.setLoginId(UUIDGen.getUUID());
                    forADD.add(loginEntity);
                } else if ("DELETE_FROM_DB".equals(loginEntity.getLogin())) {
                    loginEntityParser.delete(loginEntity.getLoginId());
                } else {
                    forUpdate.put(loginEntity.getLoginId(), loginEntity);
                }
            }
        }
        loginEntityParser.addAll(forADD);
        loginEntityParser.update(forUpdate);

    }

    private void saveUserAttributes(UserEntity user, UserAttributeEntityParser userAttributeEntityParser) throws Exception {
        List<UserAttributeEntity> forADD = new ArrayList<UserAttributeEntity>();
        Map<String, UserAttributeEntity> forUpdate = new HashMap<String, UserAttributeEntity>();

        if (user.getUserAttributes() != null) {
            for (String attrName : user.getUserAttributes().keySet()) {
                UserAttributeEntity userAttributeEntity = user.getUserAttributes().get(attrName);
                if (userAttributeEntity == null) {
                    continue;
                }
                if (userAttributeEntity.getId() == null) {
                    userAttributeEntity.setId(UUIDGen.getUUID());
                    userAttributeEntity.setUserId(user.getId());
                    forADD.add(userAttributeEntity);
                } else {
                    forUpdate.put(userAttributeEntity.getId(), userAttributeEntity);
                }
            }
        }
        userAttributeEntityParser.addAll(forADD);
        userAttributeEntityParser.update(forUpdate);

    }

    private void saveUserGroups(UserEntity user, UserEntityParser parser) {
        if (CollectionUtils.isNotEmpty(user.getGroups())) {
            String sqlADD = "INSERT INTO USER_GRP (USER_ID,GRP_ID) VALUES (?,?) ";
            String sqlDELETE = "DELETE FROM USER_GRP WHERE USER_ID=? AND GRP_ID=?";
            List<List<Object>> forAdd = new ArrayList<List<Object>>();
            List<List<Object>> forDelete = new ArrayList<List<Object>>();
            for (GroupEntity groupEntity : user.getGroups()) {
                if ("ADD_TO_DB".equals(groupEntity.getName())) {
                    List<Object> vals = new ArrayList<>();
                    vals.add(user.getId());
                    vals.add(groupEntity.getId());
                    forAdd.add(vals);
                }
                if ("DELETE_FROM_DB".equals(groupEntity.getName())) {
                    List<Object> vals = new ArrayList<>();
                    vals.add(user.getId());
                    vals.add(groupEntity.getId());
                    forDelete.add(vals);
                }
            }
            parser.executeNativeCRUD(sqlADD, forAdd);
            parser.executeNativeCRUD(sqlDELETE, forDelete);
        }

    }

    private void saveUserRoles(UserEntity user, UserEntityParser parser) {
        if (CollectionUtils.isNotEmpty(user.getRoles())) {
            String sqlADD = "INSERT INTO USER_ROLE (USER_ID,ROLE_ID) VALUES (?,?) ";
            String sqlDELETE = "DELETE FROM USER_ROLE WHERE USER_ID=? AND ROLE_ID=?";
            List<List<Object>> forAdd = new ArrayList<List<Object>>();
            List<List<Object>> forDelete = new ArrayList<List<Object>>();
            for (RoleEntity roleEntity : user.getRoles()) {
                if ("ADD_TO_DB".equals(roleEntity.getName())) {
                    List<Object> vals = new ArrayList<>();
                    vals.add(user.getId());
                    vals.add(roleEntity.getId());
                    forAdd.add(vals);
                }
                if ("DELETE_FROM_DB".equals(roleEntity.getName())) {
                    List<Object> vals = new ArrayList<>();
                    vals.add(user.getId());
                    vals.add(roleEntity.getId());
                    forDelete.add(vals);
                }
            }
            parser.executeNativeCRUD(sqlADD, forAdd);
            parser.executeNativeCRUD(sqlDELETE, forDelete);
        }

    }

    private void saveUserOrganizations(UserEntity user, UserEntityParser parser) {
        if (CollectionUtils.isNotEmpty(user.getOrganizationUser())) {
            String sqlADD = "INSERT INTO USER_AFFILIATION (USER_ID,COMPANY_ID,METADATA_TYPE_ID) VALUES (?,?,?) ";
            String sqlDELETE = "DELETE FROM USER_AFFILIATION WHERE USER_ID=? AND COMPANY_ID=?";
            List<List<Object>> forAdd = new ArrayList<List<Object>>();
            List<List<Object>> forDelete = new ArrayList<List<Object>>();
            for (OrganizationUserEntity roleEntity : user.getOrganizationUser()) {
                if (roleEntity.getMetadataTypeEntity() != null) {
                    String action = roleEntity.getMetadataTypeEntity().getDescription();
                    if (action != null) {
                        if ("ADD_TO_DB".equals(action)) {
                            List<Object> vals = new ArrayList<>();
                            vals.add(user.getId());
                            vals.add(roleEntity.getOrganization().getId());
                            vals.add(roleEntity.getMetadataTypeEntity().getId());
                            forAdd.add(vals);
                        }
                        if ("DELETE_FROM_DB".equals(action)) {
                            List<Object> vals = new ArrayList<>();
                            vals.add(user.getId());
                            vals.add(roleEntity.getOrganization().getId());
                            forDelete.add(vals);
                        }
                    }
                }
            }
            parser.executeNativeCRUD(sqlADD, forAdd);
            parser.executeNativeCRUD(sqlDELETE, forDelete);
        }

    }

    private void saveUserSupervisors(UserEntity user, UserEntityParser parser) {
        if (CollectionUtils.isNotEmpty(user.getSupervisors())) {
            String sqlADD = "INSERT INTO ORG_STRUCTURE (SUPERVISOR_ID,STAFF_ID,IS_PRIMARY_SUPER) VALUES (?,?,?) ";
            String sqlDELETE = "DELETE FROM ORG_STRUCTURE WHERE SUPERVISOR_ID=? AND STAFF_ID=?";
            List<List<Object>> forAdd = new ArrayList<List<Object>>();
            List<List<Object>> forDelete = new ArrayList<List<Object>>();
            for (SupervisorEntity supervisorEntity : user.getSupervisors()) {
                if (supervisorEntity.getSupervisor() != null && supervisorEntity.getSupervisor().getFirstName() != null) {
                    String action = supervisorEntity.getSupervisor().getFirstName();
                    if (action != null) {
                        if ("ADD_TO_DB".equals(action)) {
                            List<Object> vals = new ArrayList<>();
                            vals.add(supervisorEntity.getSupervisor().getId());
                            vals.add(user.getId());
                            vals.add(supervisorEntity.getIsPrimarySuper() ? "Y" : "N");
                            forAdd.add(vals);
                        }
                        if ("DELETE_FROM_DB".equals(action)) {
                            List<Object> vals = new ArrayList<>();
                            vals.add(supervisorEntity.getSupervisor().getId());
                            vals.add(user.getId());
                            forDelete.add(vals);
                        }
                    }
                }
            }
            parser.executeNativeCRUD(sqlADD, forAdd);
            parser.executeNativeCRUD(sqlDELETE, forDelete);
        }

    }

    private void saveEmails(UserEntity user, EmailAddressEntityParser parser) throws Exception {
        List<EmailAddressEntity> forADD = new ArrayList<EmailAddressEntity>();
        Map<String, EmailAddressEntity> forUpdate = new HashMap<String, EmailAddressEntity>();

        if (user.getEmailAddresses() != null) {
            for (EmailAddressEntity entry : user.getEmailAddresses()) {
                if (entry == null) continue;
                if (entry.getEmailId() == null) {
                    entry.setParent(user);
                    entry.setEmailId(UUIDGen.getUUID());
                    forADD.add(entry);
                } else {
                    forUpdate.put(entry.getEmailId(), entry);
                }
            }
        }
        parser.addAll(forADD);
        parser.update(forUpdate);
    }

    private void savePhones(UserEntity user, PhoneEntityParser parser) throws Exception {
        List<PhoneEntity> forADD = new ArrayList<PhoneEntity>();
        Map<String, PhoneEntity> forUpdate = new HashMap<String, PhoneEntity>();

        if (user.getPhones() != null) {
            for (PhoneEntity entry : user.getPhones()) {
                if (entry == null) continue;
                if ((entry.getPhoneId() != null) && (entry.getDescription() != null) && ("DELETE_FROM_DB".equalsIgnoreCase(entry.getDescription()))) {
                    parser.delete(entry.getPhoneId());
                    continue;
                }
                if (entry.getPhoneId() == null) {
                    entry.setParent(user);
                    entry.setPhoneId(UUIDGen.getUUID());
                    forADD.add(entry);
                } else {
                    forUpdate.put(entry.getPhoneId(), entry);
                }
            }
        }
        parser.addAll(forADD);
        parser.update(forUpdate);
    }

    private void saveAddressEntity(UserEntity user, AddressEntityParser parser) throws Exception {
        List<AddressEntity> forADD = new ArrayList<AddressEntity>();
        Map<String, AddressEntity> forUpdate = new HashMap<String, AddressEntity>();

        if (user.getAddresses() != null) {
            for (AddressEntity entry : user.getAddresses()) {
                if (entry == null) continue;
                if (entry.getAddressId() == null) {
                    entry.setParent(user);
                    entry.setAddressId(UUIDGen.getUUID());
                    forADD.add(entry);
                } else {
                    forUpdate.put(entry.getAddressId(), entry);
                }
            }
        }
        parser.addAll(forADD);
        parser.update(forUpdate);
    }

    private LastRecordTime getRowTime(LineObject rowObj) {
        Attribute atr = rowObj.get("modifyTimeStamp");
        if (atr != null && StringUtils.isNotBlank(atr.getValue())) {
            return getTime(atr);
        }
        atr = rowObj.get("createTimestamp");
        if (atr != null && StringUtils.isNotBlank(atr.getValue())) {
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
            lrt.setMostRecentRecord(Double.parseDouble(s.substring(0, i)));
            lrt.setGeneralizedTime(atr.getValue());
            return lrt;

        }
        lrt.setMostRecentRecord(Double.parseDouble(s));
        lrt.setGeneralizedTime(atr.getValue());

        return lrt;
    }

    protected String[] getDirAttrIds() {
        return new String[]{"*", "modifyTimeStamp", "createTimestamp"};
    }

    protected String getNullDate() {
        return "19700101000000Z"; //Jan, 1, 1970
    }

    private void saveToCache(Object bindingMap) {
        try {
            FileOutputStream fileOut = new FileOutputStream("cache.openiam");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(bindingMap);
        } catch (Exception e) {
            System.out.println("Can't save in cache");
        }
    }

    private void saveToCacheObject(Object bindingMap, String fileName) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(bindingMap);
        } catch (Exception e) {
            System.out.println("Can't save in cache");
        }
    }

    private Map<String, Object> readFromCache() {
        Map<String, Object> bindingMap = null;
        try {
            FileInputStream fileOut = new FileInputStream("cache.openiam");
            ObjectInputStream in = new ObjectInputStream(fileOut);
            bindingMap = (Map<String, Object>) in.readObject();
        } catch (Exception e) {
            System.out.println("Can't read from cache");
        }
        return bindingMap;
    }

    private List<LineObject> readFromCacheObjects(String fileName) {
        List<LineObject> bindingMap = null;
        try {
            FileInputStream fileOut = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileOut);
            bindingMap = (List<LineObject>) in.readObject();
        } catch (Exception e) {
            System.out.println("Can't read from cache");
        }
        return bindingMap;
    }

}
