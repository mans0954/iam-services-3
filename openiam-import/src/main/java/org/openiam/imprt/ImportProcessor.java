package org.openiam.imprt;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.id.UUIDGen;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.OrganizationUserEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.synch.domain.SynchConfigEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.custom.MailboxHelper;
import org.openiam.imprt.jdbc.DataSource;
import org.openiam.imprt.jdbc.parser.impl.*;
import org.openiam.imprt.key.KeyManagementWSClient;
import org.openiam.imprt.model.Attribute;
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
    private final static boolean debugMode = true;

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

        //**************************************************************************************************************

        if (CollectionUtils.isEmpty(processingData)) {
            if (debugMode) {
                System.out.println("Nothing FOund in AD!");
            }
            return;
        }

        if (debugMode) {
            System.out.print("Prefill linked data ");
        }

        // do transform and insert/update and commit
        //Prepare Data
        Map<String, Object> bindingMap = new HashMap<String, Object>();


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

        // Define SQL query for dependency select
        final String getUserByLoginSQL = "SELECT u.* FROM LOGIN l JOIN USERS u ON l.USER_ID=u.USER_ID WHERE l.MANAGED_SYS_ID='0' AND l.LOWERCASE_LOGIN='%s'";
        final String getAllOrganizationsSQL = "SELECT %s FROM COMPANY c WHERE c.ORG_TYPE_ID='ORGANIZATION'";
        final String getChildOrganizationsSQL = "SELECT %s FROM COMPANY c JOIN COMPANY_TO_COMPANY_MEMBERSHIP ccm ON c.COMPANY_ID=ccm.MEMBER_COMPANY_ID WHERE ccm.COMPANY_ID='%s' AND c.ORG_TYPE_ID='SUBSIDIARY'";
        final String getAttributesOrganizationsSQL = "SELECT %s FROM COMPANY_ATTRIBUTE ca WHERE ca.COMPANY_ID='%s'";

        List<OrganizationEntity> organizationEntityList = companyEntityParser.get(String.format(getAllOrganizationsSQL, Utils.columnsToSelectFields(companyColumnList, "c")), companyColumnList);
//        if (CollectionUtils.isNotEmpty(organizationEntityList)) {
//            for (OrganizationEntity organizationEntity : organizationEntityList) {
//                List<OrganizationEntity> childs = companyEntityParser.get(String.format(getChildOrganizationsSQL, Utils.columnsToSelectFields(companyColumnList, "c"), organizationEntity.getId()), companyColumnList);
//                if (CollectionUtils.isNotEmpty(childs)) {
//                    for (OrganizationEntity child : childs) {
//                        List<OrganizationAttributeEntity> organizationAttributeEntityList = companyAttributeEntityParser.get(String.format(getAttributesOrganizationsSQL, Utils.columnsToSelectFields(companyAttributeColumnList, "ca"), child.getId()), companyAttributeColumnList);
//                        if (CollectionUtils.isNotEmpty(organizationAttributeEntityList)) {
//                            child.setAttributes(new HashSet<OrganizationAttributeEntity>(organizationAttributeEntityList));
//                        }
//                    }
//                    organizationEntity.setChildOrganizations(new HashSet<OrganizationEntity>(childs));
//                }
//            }
//        }
        bindingMap.put("ORGANIZATIONS", organizationEntityList);
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
        for (LineObject lo : processingData) {
            sAMAccountNameAttribute = lo.get("sAMAccountName");
            if (sAMAccountNameAttribute != null && sAMAccountNameAttribute.getValue() != null) {
                if (debugMode) {
                    System.out.print("get User by Login. ");
                }
                time1 = System.currentTimeMillis();
                //FIXME should be common search in OpenIAM
                users = userEntityParser.get(String.format(getUserByLoginSQL, sAMAccountNameAttribute.getValue()));
                if (debugMode) {
                    System.out.println("Time=" + (System.currentTimeMillis() - time1) + "ms");
                }
                if (CollectionUtils.isEmpty(users)) {
                    user = new UserEntity();
                    user.setOrganizationUser(new HashSet<OrganizationUserEntity>());
                    if (debugMode) {
                        System.out.println("New User=" + sAMAccountNameAttribute.getValue());
                    }
                } else {
                    user = users.get(0);
                    time1 = System.currentTimeMillis();
                    if (debugMode) {
                        System.out.print("fillUserWithDependenies ");
                    }
                    //fill user with data
                    fillUserWithDependenies(user,
                            loginEntityParser,
                            organizationUserEntityParser,
                            roleEntityParser,
                            groupEntityParser,
                            userAttributeEntityParser,
                            emailAddressEntityParser);

                    if (debugMode) {
                        System.out.println("Time=" + (System.currentTimeMillis() - time1) + "ms");
                    }
                }
                time1 = System.currentTimeMillis();
                if (debugMode) {
                    System.out.print("Transformation time=");
                }
                tr.execute(lo, user, bindingMap);
                if (debugMode) {
                    System.out.println((System.currentTimeMillis() - time1) + "ms");
                }
                time1 = System.currentTimeMillis();
                if (debugMode) {
                    System.out.print("Save User time=");
                }
                saveChanges(user,
                        userEntityParser,
                        loginEntityParser,
                        organizationUserEntityParser,
                        roleEntityParser,
                        groupEntityParser,
                        userAttributeEntityParser,
                        emailAddressEntityParser);
                if (debugMode) {
                    System.out.println((System.currentTimeMillis() - time1) + "ms");
                }
            }
        }
        //**************************************************************************************************************

        // do generate user keys
        KeyManagementWSClient keyManagementWSClient = new KeyManagementWSClient(DataHolder.getInstance().getProperty(ImportPropertiesKey.KEY_SERVICE_WSDL));
        // TODO: need to pass collected user ids on the previos step.
        keyManagementWSClient.generateKeysForUserList(new ArrayList<String>());
        // do reindex
        // for now need to restart jboss ))) not enough time to integrate lucene
    }

    private void fillUserWithDependenies(UserEntity user,
                                         LoginEntityParser loginEntityParser,
                                         OrganizationUserEntityParser organizationUserEntityParser,
                                         RoleEntityParser roleEntityParser,
                                         GroupEntityParser groupEntityParser,
                                         UserAttributeEntityParser userAttributeEntityParser,
                                         EmailAddressEntityParser emailAddressEntityParser) throws Exception {
        final String getUserOrganizationsSQL = "SELECT %s FROM USER_AFFILIATION c WHERE c.USER_ID='%s'";
        final String getUserAttributesSQL = "SELECT %s FROM USER_ATTRIBUTES c WHERE c.USER_ID='%s'";
        final String getUserRoleSQL = "SELECT %s FROM ROLE c JOIN USER_ROLE j on j.ROLE_ID=c.ROLE_ID WHERE j.USER_ID='%s'";
        final String getUserGroupSQL = "SELECT %s FROM GRP c JOIN USER_GRP j on j.GRP_ID=c.GRP_ID WHERE j.USER_ID='%s'";
//        final String getLoginsSQL = "SELECT %s FROM LOGIN c WHERE c.USER_ID='%s'";
        final String getEmailsSQL = "SELECT %s FROM EMAIL_ADDRESS c WHERE  c.PARENT_ID='%s'";


        final List<Column> organizationUserColumnList = Utils.getColumns(new ImportPropertiesKey[]{ImportPropertiesKey.USER_AFFILIATION_COMPANY_ID,
                ImportPropertiesKey.USER_AFFILIATION_METADATA_TYPE_ID});

        final List<Column> userAttributeColumnList = Utils.getColumns(new ImportPropertiesKey[]{ImportPropertiesKey.USER_ATTRIBUTES_ID,ImportPropertiesKey.USER_ATTRIBUTES_USER_ID,
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

    private void saveChanges(UserEntity user, UserEntityParser userEntityParser,
                             LoginEntityParser loginEntityParser,
                             OrganizationUserEntityParser organizationUserEntityParser,
                             RoleEntityParser roleEntityParser,
                             GroupEntityParser groupEntityParser,
                             UserAttributeEntityParser userAttributeEntityParser,
                             EmailAddressEntityParser emailAddressEntityParser) {
        String userId = null;
        if (user.getId() == null) {
            userId = UUIDGen.getUUID();
            user.setId(userId);
            userEntityParser.add(user);
        } else {
            userId = user.getId();
            userEntityParser.update(user, userId);
        }
        try {
            saveLogins(user, loginEntityParser);
        } catch (Exception e) {
            System.out.println("Can't save logins!");
        }

        try {
            saveUserAttributes(user, userAttributeEntityParser);
        } catch (Exception e) {
            System.out.println("Can't save logins!");
        }

        try {
            saveEmails(user, emailAddressEntityParser);
        } catch (Exception e) {
            System.out.println("Can't save logins!");
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
//                    loginEntityParser.add(loginEntity);
                } else {
//                    loginEntityParser.update(loginEntity, loginEntity.getLoginId());
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

}
