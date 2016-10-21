package org.openiam.imprt.constant;

/**
 * Class contains constant for all PropertiesKey<br>
 *
 * @author Alexander Duckardt
 */
public enum ImportPropertiesKey {
    CONF_PATH("openiam.confPath"),
    SYNC_CONFIG("openiam.syncConfig"),
    WEB_SERVER_URL("openiam.web.server.url"),
    KEYSTORE("openia.keystore"),

    SIMPLE_DATE_FORMAT("openiam.date.format"),
    SERVER_MODE("openiam.server.mode"),
    KEY_SERVICE_WSDL("openiam.key.service.wsdl"),
    // jdbc connector properties
    DATABASE_USER("openiam.db.username"),
    DATABASE_PASSWORD("openiam.db.password"),
    DATABASE_URL("openiam.db.connection.string"),
    JDBC_DRIVER("openiam.db.driverClassName"),
    JDBC_MIN_POOL_SIZE("openiam.db.pool.min"),
    JDBC_MAX_POOL_SIZE("openiam.db.pool.max"),


    // database tables mappings
    //Very important!! Each columnName should starts with <TABLE NAME>_.
    // For exampe if Table Name = USERS,
    //each column name for USERS should start with USERS_
    //USERS
    USERS("openiam.table.user"),
    USERS_USER_ID("openiam.table.user.id"),
    USERS_FIRST_NAME("openiam.table.user.firstName"),
    USERS_LAST_NAME("openiam.table.user.lastName"),
    USERS_MIDDLE_INIT("openiam.table.user.middleInit"),
    USERS_TYPE_ID("openiam.table.user.typeId"),
    USERS_CLASSIFICATION("openiam.table.user.classification"),
    USERS_TITLE("openiam.table.user.title"),
    USERS_MAIL_CODE("openiam.table.user.mailCode"),
    USERS_COST_CENTER("openiam.table.user.costCenter"),
    USERS_STATUS("openiam.table.user.status"),
    USERS_SECONDARY_STATUS("openiam.table.user.secondaryStatus"),
    USERS_BIRTHDATE("openiam.table.user.birthdate"),
    USERS_SEX("openiam.table.user.sex"),
    USERS_CREATE_DATE("openiam.table.user.createDate"),
    USERS_CREATED_BY("openiam.table.user.createdBy"),
    USERS_LAST_UPDATE("openiam.table.user.lastUpdate"),
    USERS_LAST_UPDATED_BY("openiam.table.user.lastUpdatedBy"),
    USERS_PREFIX("openiam.table.user.prefix"),
    USERS_SUFFIX("openiam.table.user.suffix"),
    USERS_USER_TYPE_IND("openiam.table.user.userTypeInd"),
    USERS_EMPLOYEE_ID("openiam.table.user.employeeId"),
    USERS_EMPLOYEE_TYPE("openiam.table.user.employeeType"),
    USERS_LOCATION_CD("openiam.table.user.locationCd"),
    USERS_LOCATION_NAME("openiam.table.user.locationName"),
    USERS_COMPANY_OWNER_ID("openiam.table.user.companyOwnerId"),
    USERS_JOB_CODE("openiam.table.user.jobCode"),
    USERS_ALTERNATE_ID("openiam.table.user.alternateId"),
    USERS_START_DATE("openiam.table.user.startDate"),
    USERS_LAST_DATE("openiam.table.user.lastDate"),
    USERS_MAIDEN_NAME("openiam.table.user.maidenName"),
    USERS_NICKNAME("openiam.table.user.nickname"),
    USERS_PASSWORD_THEME("openiam.table.user.passwordTheme"),
    USERS_SHOW_IN_SEARCH("openiam.table.user.showInSearch"),
    USERS_USER_OWNER_ID("openiam.table.user.userOwnerId"),
    USERS_DATE_PASSWORD_CHANGED("openiam.table.user.datePasswordChanged"),
    USERS_DATE_CHALLENGE_RESP_CHANGED("openiam.table.user.dateChallengeRespChanged"),
    USERS_DATE_IT_POLICY_APPROVED("openiam.table.user.dateidPolicyApproved"),
    USERS_CLAIM_DATE("openiam.table.user.claimDate"),
    USERS_LASTNAME_PREFIX("openiam.table.user.lastnamePrefix"),
    USERS_SUB_TYPE_ID("openiam.table.user.subTypeId"),
    USERS_PARTNER_NAME("openiam.table.user.partnerName"),
    USERS_PREFIX_PARTNER_NAME("openiam.table.user.prefixPartnerName"),

    //LOGIN
    LOGIN("openiam.table.login"),
    LOGIN_LOGIN("openiam.table.login.login"),
    LOGIN_MANAGED_SYS_ID("openiam.table.login.managedSysId"),
    //    LOGIN_IDENTITY_TYPE("openiam.table.login.identityType"),
    LOGIN_CANONICAL_NAME("openiam.table.login.canonicalName"),
    LOGIN_USER_ID("openiam.table.login.userId"),
    LOGIN_PASSWORD("openiam.table.login.password"),
    LOGIN_PWD_EQUIVALENT_TOKEN("openiam.table.login.pwdEquivalentToken"),
    LOGIN_PWD_CHANGED("openiam.table.login.pwdChanfed"),
    LOGIN_PWD_EXP("openiam.table.login.pwdExp"),
    LOGIN_RESET_PWD("openiam.table.login.resetPwd"),
    LOGIN_FIRST_TIME_LOGIN("openiam.table.login.firstTimeLogin"),
    LOGIN_IS_LOCKED("openiam.table.login.isLocked"),
    LOGIN_STATUS("openiam.table.login.status"),
    LOGIN_GRACE_PERIOD("openiam.table.login.gracePeriod"),
    LOGIN_CREATE_DATE("openiam.table.login.createDate"),
    LOGIN_CREATED_BY("openiam.table.login.createdBy"),
    LOGIN_CURRENT_LOGIN_HOST("openiam.table.login.currentLoginHost"),
    LOGIN_AUTH_FAIL_COUNT("openiam.table.login.authFailCount"),
    LOGIN_LAST_AUTH_ATTEMPT("openiam.table.login.lastAuthAttempt"),
    LOGIN_LAST_LOGIN("openiam.table.login.lastLogin"),
    LOGIN_LAST_LOGIN_IP("openiam.table.login.lastLoginIp"),
    LOGIN_PREV_LOGIN("openiam.table.login.prevLogin"),
    LOGIN_PREV_LOGIN_IP("openiam.table.login.prevLoginIp"),
    LOGIN_IS_DEFAULT("openiam.table.login.isDefault"),
    LOGIN_PWD_CHANGE_COUNT("openiam.table.login.pwdChangeCount"),
    LOGIN_PSWD_RESET_TOKEN("openiam.table.login.pswdResetToken"),
    LOGIN_PSWD_RESET_TOKEN_EXP("openiam.table.login.pswdResetTokenExp"),
    LOGIN_LOGIN_ID("openiam.table.login.loginId"),
    LOGIN_LAST_UPDATE("openiam.table.login.lastUpdate"),
    LOGIN_LOWERCASE_LOGIN("openiam.table.login.lowercaseLogin"),
    LOGIN_PROV_STATUS("openiam.table.login.provStatus"),
    LOGIN_CHALLENGE_RESPONSE_FAIL_COUNT("openiam.table.login.challengeResponseFailCount"),
    //Not now
//    LOGIN_SMS_RESET_TOKEN("openiam.table.login.smsResetToken"),
//    LOGIN_SMS_RESET_TOKEN_EXP("openiam.table.login.smsResetTokenExp"),

    //USER_ATTRIBUTES
    USER_ATTRIBUTES("openiam.table.userAttributes"),
    USER_ATTRIBUTES_ID("openiam.table.userAttributes.id"),
    USER_ATTRIBUTES_USER_ID("openiam.table.userAttributes.userId"),
    USER_ATTRIBUTES_METADATA_ID("openiam.table.userAttributes.metaDataId"),
    USER_ATTRIBUTES_NAME("openiam.table.userAttributes.name"),
    USER_ATTRIBUTES_VALUE("openiam.table.userAttributes.value"),
    //    USER_ATTRIBUTES_VALUE_AS_BYTE_ARRAY("openiam.table.userAttributes.valueAsByteArray"),
    USER_ATTRIBUTES_IS_MULTIVALUED("openiam.table.userAttributes.isMultivalued"),

    //ROLE
    ROLE("openiam.table.role"),
    ROLE_ROLE_NAME("openiam.table.role.roleName"),
    ROLE_CREATE_DATE("openiam.table.role.createDate"),
    ROLE_CREATED_BY("openiam.table.role.createdBy"),
    ROLE_DESCRIPTION("openiam.table.role.description"),
    ROLE_STATUS("openiam.table.role.status"),
    ROLE_ROLE_ID("openiam.table.role.roleId"),
    ROLE_MANAGED_SYS_ID("openiam.table.role.managedSysId"),
    ROLE_ADMIN_RESOURCE_ID("openiam.table.role.adminResourceId"),
    ROLE_TYPE_ID("openiam.table.role.typeId"),

    //GRP
    GRP("openiam.table.grp"),
    GRP_GRP_ID("openiam.table.grp.grpId"),
    GRP_GRP_NAME("openiam.table.grp.grpName"),
    GRP_CREATE_DATE("openiam.table.grp.createDate"),
    GRP_CREATED_BY("openiam.table.grp.createdBy"),
    GRP_GROUP_DESC("openiam.table.grp.groupDesc"),
    GRP_STATUS("openiam.table.grp.status"),
    GRP_LAST_UPDATE("openiam.table.grp.lastUpdate"),
    GRP_LAST_UPDATED_BY("openiam.table.grp.lastUpdatedBy"),
    GRP_MANAGED_SYS_ID("openiam.table.grp.managedSysId"),
    GRP_ADMIN_RESOURCE_ID("openiam.table.grp.adminResourceId"),
    GRP_TYPE_ID("openiam.table.grp.typeId"),
    GRP_GRP_CLASSIFICATION("openiam.table.grp.grpClassification"),
    GRP_AD_GRP_TYPE("openiam.table.grp.adGrpType"),
    GRP_AD_GRP_SCOPE("openiam.table.grp.adGrpScope"),
    GRP_GRP_RISK("openiam.table.grp.grpRisk"),
    GRP_MAX_USER_NUMBER("openiam.table.grp.maxUserNumber"),
    GRP_MEMBERSHIP_DURATION_SECONDS("openiam.table.grp.membershipDurationSeconds"),

    //GRP ATTRIBUTES
    T_GRP_ATTRIBUTES("openiam.table.grp.attributes"),
    T_GRP_ATTRIBUTES_ID("openiam.table.grp.attributes.id"),
    T_GRP_ATTRIBUTES_GRP_ID("openiam.table.grp.attributes.grpId"),
    T_GRP_ATTRIBUTES_NAME("openiam.table.grp.attributes.name"),
    T_GRP_ATTRIBUTES_ATTR_VALUE("openiam.table.grp.attributes.attrVal"),
    T_GRP_ATTRIBUTES_METADATA_ID("openiam.table.grp.attributes.mdTypeId"),

    //RES
    RES("openiam.table.res"),
    RES_RESOURCE_ID("openiam.table.res.resourceId"),
    RES_RESOURCE_TYPE_ID("openiam.table.res.resourceTypeId"),
    RES_DESCRIPTION("openiam.table.res.description"),
    RES_NAME("openiam.table.res.name"),
    RES_DISPLAY_ORDER("openiam.table.res.displayOrder"),
    RES_URL("openiam.table.res.url"),
    RES_MIN_AUTH_LEVEL("openiam.table.res.minAuthLevel"),
    RES_IS_PUBLIC("openiam.table.res.isPublic"),
    RES_ADMIN_RESOURCE_ID("openiam.table.res.adminResourceId"),
    RES_RISK("openiam.table.res.risk"),
    RES_TYPE_ID("openiam.table.res.typeId"),
    RES_COORELATED_NAME("openiam.table.res.coorelatedName"),

    //COMPANY
    COMPANY("openiam.table.company"),
    COMPANY_COMPANY_ID("openiam.table.company.companyId"),
    COMPANY_COMPANY_NAME("openiam.table.company.companyName"),
    COMPANY_LST_UPDATE("openiam.table.company.lstUpdate"),
    COMPANY_LST_UPDATED_BY("openiam.table.company.lstUpdatedBy"),
    COMPANY_PARENT_ID("openiam.table.company.parentId"),
    COMPANY_STATUS("openiam.table.company.status"),
    COMPANY_CREATE_DATE("openiam.table.company.createDate"),
    COMPANY_CREATED_BY("openiam.table.company.createdBy"),
    COMPANY_ALIAS("openiam.table.company.alias"),
    COMPANY_DESCRIPTION("openiam.table.company.description"),
    COMPANY_DOMAIN_NAME("openiam.table.company.domainName"),
    COMPANY_LDAP_STR("openiam.table.company.ldapStr"),
    COMPANY_CLASSIFICATION("openiam.table.company.classification"),
    COMPANY_INTERNAL_COMPANY_ID("openiam.table.company.internalCompanyId"),
    COMPANY_ABBREVIATION("openiam.table.company.abbreviation"),
    COMPANY_SYMBOL("openiam.table.company.symbol"),
    COMPANY_ORG_TYPE_ID("openiam.table.company.orgTypeId"),
    COMPANY_ADMIN_RESOURCE_ID("openiam.table.company.adminResourceId"),
    COMPANY_IS_SELECTABLE("openiam.table.company.isSelectable"),
    COMPANY_TYPE_ID("openiam.table.company.typeId"),

    //T_COMPANY_ATTRIBUTE
    T_COMPANY_ATTRIBUTE("openiam.table.company.attribute"),
    T_COMPANY_ATTRIBUTE_ID("openiam.table.company.attribute.id"),
    T_COMPANY_ATTRIBUTE_COMPANY_ID("openiam.table.company.attribute.companyId"),
    T_COMPANY_ATTRIBUTE_NAME("openiam.table.company.attribute.name"),
    T_COMPANY_ATTRIBUTE_VALUE("openiam.table.company.attribute.value"),


    //METADATA_TYPE
    METADATA_TYPE("openiam.table.metadataType"),
    METADATA_TYPE_TYPE_ID("openiam.table.metadataType.typeId"),
    METADATA_TYPE_DESCRIPTION("openiam.table.metadataType.description"),
    METADATA_TYPE_ACTIVE("openiam.table.metadataType.active"),
    METADATA_TYPE_SYNC_MANAGED_SYS("openiam.table.metadataType.syncManagedSys"),
    METADATA_TYPE_GROUPING("openiam.table.metadataType.grouping"),
    METADATA_TYPE_IS_BINARY("openiam.table.metadataType.isBinary"),
    METADATA_TYPE_IS_SENSITIVE("openiam.table.metadataType.isSensitive"),

    //ADDRESS
    ADDRESS("openiam.table.address"),
    ADDRESS_ADDRESS_ID("openiam.table.address.addressId"),
    ADDRESS_NAME("openiam.table.address.name"),
    ADDRESS_COUNTRY("openiam.table.address.country"),
    ADDRESS_BLDG_NUM("openiam.table.address.bldgNum"),
    ADDRESS_STREET_DIRECTION("openiam.table.address.streetDirection"),
    ADDRESS_SUITE("openiam.table.address.suite"),
    ADDRESS_ADDRESS1("openiam.table.address.address1"),
    ADDRESS_ADDRESS2("openiam.table.address.address2"),
    ADDRESS_ADDRESS3("openiam.table.address.address3"),
    ADDRESS_ADDRESS4("openiam.table.address.address4"),
    ADDRESS_ADDRESS5("openiam.table.address.address5"),
    ADDRESS_ADDRESS6("openiam.table.address.address6"),
    ADDRESS_ADDRESS7("openiam.table.address.address7"),
    ADDRESS_CITY("openiam.table.address.city"),
    ADDRESS_STATE("openiam.table.address.state"),
    ADDRESS_POSTAL_CD("openiam.table.address.postalCd"),
    ADDRESS_IS_DEFAULT("openiam.table.address.isDefault"),
    ADDRESS_DESCRIPTION("openiam.table.address.description"),
    ADDRESS_ACTIVE("openiam.table.address.active"),
    ADDRESS_PARENT_ID("openiam.table.address.parentId"),
    ADDRESS_LAST_UPDATE("openiam.table.address.lastUpdate"),
    ADDRESS_CREATE_DATE("openiam.table.address.createDate"),
    ADDRESS_TYPE_ID("openiam.table.address.typeId"),
    ADDRESS_COPY_FROM_LOCATION_ID("openiam.table.address.copyFromLocationId"),

    //EMAIL_ADDRESS
    EMAIL_ADDRESS("openiam.table.emailAddress"),
    EMAIL_ADDRESS_EMAIL_ID("openiam.table.emailAddress.emailId"),
    EMAIL_ADDRESS_NAME("openiam.table.emailAddress.name"),
    EMAIL_ADDRESS_DESCRIPTION("openiam.table.emailAddress.description"),
    EMAIL_ADDRESS_EMAIL_ADDRESS("openiam.table.emailAddress.emailAddress"),
    EMAIL_ADDRESS_IS_DEFAULT("openiam.table.emailAddress.isDefault"),
    EMAIL_ADDRESS_ACTIVE("openiam.table.emailAddress.active"),
    EMAIL_ADDRESS_PARENT_ID("openiam.table.emailAddress.parentId"),
    EMAIL_ADDRESS_LAST_UPDATE("openiam.table.emailAddress.lastUpdate"),
    EMAIL_ADDRESS_CREATE_DATE("openiam.table.emailAddress.createDate"),
    EMAIL_ADDRESS_TYPE_ID("openiam.table.emailAddress.typeId"),

    //PHONE
    PHONE("openiam.table.phone"),
    PHONE_PHONE_ID("openiam.table.phone.phoneId"),
    PHONE_NAME("openiam.table.phone.name"),
    PHONE_AREA_CD("openiam.table.phone.areaCd"),
    PHONE_COUNTRY_CD("openiam.table.phone.countryCd"),
    PHONE_DESCRIPTION("openiam.table.phone.description"),
    PHONE_PHONE_NBR("openiam.table.phone.phoneNbr"),
    PHONE_PHONE_EXT("openiam.table.phone.phoneExt"),
    PHONE_IS_DEFAULT("openiam.table.phone.isDefault"),
    PHONE_ACTIVE("openiam.table.phone.active"),
    PHONE_PARENT_ID("openiam.table.phone.parentId"),
    PHONE_LAST_UPDATE("openiam.table.phone.lastUpdate"),
    PHONE_CREATE_DATE("openiam.table.phone.createDate"),
    PHONE_TYPE_ID("openiam.table.phone.typeId"),

    //LOCATION
    LOCATION("openiam.table.location"),
    LOCATION_LOCATION_ID("openiam.table.location.locationId"),
    LOCATION_NAME("openiam.table.location.name"),
    LOCATION_DESCRIPTION("openiam.table.location.description"),
    LOCATION_COUNTRY("openiam.table.location.country"),
    LOCATION_BLDG_NUM("openiam.table.location.bldgNum"),
    LOCATION_STREET_DIRECTION("openiam.table.location.streetDirection"),
    LOCATION_ADDRESS1("openiam.table.location.address1"),
    LOCATION_ADDRESS2("openiam.table.location.address2"),
    LOCATION_ADDRESS3("openiam.table.location.address3"),
    LOCATION_CITY("openiam.table.location.city"),
    LOCATION_STATE("openiam.table.location.state"),
    LOCATION_POSTAL_CD("openiam.table.location.postalCd"),
    LOCATION_ORGANIZATION_ID("openiam.table.location.organizationId"),
    LOCATION_INTERNAL_LOCATION_ID("openiam.table.location.internalLocationId"),
    LOCATION_ACTIVE("openiam.table.location.active"),
    LOCATION_SENSITIVE_LOCATION("openiam.table.location.sensitiveLocation"),

    //USER_GRP
    USER_GRP("openiam.table.userGrp"),
    USER_GRP_GRP_ID("openiam.table.userGrp.grpId"),
    USER_GRP_USER_ID("openiam.table.userGrp.userId"),

    //USER_ROLE
    USER_ROLE("openiam.table.userRole"),
    USER_ROLE_USER_ID("openiam.table.userRole.userId"),
    USER_ROLE_ROLE_ID("openiam.table.userRole.roleId"),

    //RESOURCE_USER
    RESOURCE_USER("openiam.table.resourceUser"),
    RESOURCE_USER_RESOURCE_ID("openiam.table.resourceUser.resourceId"),
    RESOURCE_USER_USER_ID("openiam.table.resourceUser.userId"),
    RESOURCE_USER_START_DATE("openiam.table.resourceUser.startDate"),

    //USER_AFFILIATION
    USER_AFFILIATION("openiam.table.userAffiliation"),
    USER_AFFILIATION_COMPANY_ID("openiam.table.userAffiliation.companyId"),
    USER_AFFILIATION_USER_ID("openiam.table.userAffiliation.userId"),
    USER_AFFILIATION_CREATE_DATE("openiam.table.userAffiliation.createDate"),
    USER_AFFILIATION_METADATA_TYPE_ID("openiam.table.userAffiliation.metadateTypeId"),

    //ORGANIZATION_TYPE
    ORGANIZATION_TYPE("openiam.table.organizationType"),
    ORGANIZATION_TYPE_ORG_TYPE_ID("openiam.table.organizationType.id"),
    ORGANIZATION_TYPE_NAME("openiam.table.organizationType.name"),
    ORGANIZATION_TYPE_DESCRIPTION("openiam.table.organizationType.description"),

    //SYNC_CONFIG
    SYNCH_CONFIG("openiam.table.syncConfig"),
    SYNCH_CONFIG_ID("openiam.table.syncConfig.id"),
    SYNCH_CONFIG_NAME("openiam.table.syncConfig.name"),
    SYNCH_CONFIG_STATUS("openiam.table.syncConfig.status"),
    SYNCH_CONFIG_SYNCH_SRC("openiam.table.syncConfig.sync.src"),
    SYNCH_CONFIG_FILE_NAME("openiam.table.syncConfig.file.name"),
    SYNCH_CONFIG_SRC_LOGIN_ID("openiam.table.syncConfig.login.id"),
    SYNCH_CONFIG_SRC_PASSWORD("openiam.table.syncConfig.password"),
    SYNCH_CONFIG_SRC_HOST("openiam.table.syncConfig.host"),
    SYNCH_CONFIG_DRIVER("openiam.table.syncConfig.driver"),
    SYNCH_CONFIG_CONNECTION_URL("openiam.table.syncConfig.connection.url"),
    SYNCH_CONFIG_QUERY("openiam.table.syncConfig.query"),
    SYNCH_CONFIG_QUERY_TIME_FIELD("openiam.table.syncConfig.query.time"),
    SYNCH_CONFIG_BASE_DN("openiam.table.syncConfig.base.dn"),
    SYNCH_CONFIG_LAST_EXEC_TIME("openiam.table.syncConfig.last.exec.time"),
    SYNCH_CONFIG_LAST_REC_PROCESSED("openiam.table.syncConfig.last.rec.processed"),
    SYNCH_CONFIG_MANAGED_SYS_ID("openiam.table.syncConfig.managed.sys.id"),
    SYNCH_CONFIG_LOAD_MATCH_ONLY("openiam.table.syncConfig.load.march.only"),
    SYNCH_CONFIG_UPDATE_ATTRIBUTE("openiam.table.syncConfig.update.attribute"),
    SYNCH_CONFIG_SYNCH_FREQUENCY("openiam.table.syncConfig.sync.frequency"),
    SYNCH_CONFIG_SYNCH_TYPE("openiam.table.syncConfig.sync.type"),
    SYNCH_CONFIG_PROCESS_RULE("openiam.table.syncConfig.process.rule"),
    SYNCH_CONFIG_VALIDATION_RULE("openiam.table.syncConfig.validation.rule"),
    SYNCH_CONFIG_TRANSFORMATION_RULE("openiam.table.syncConfig.transformation.rule"),
    SYNCH_CONFIG_MATCH_FIELD_NAME("openiam.table.syncConfig.match.field.name"),
    SYNCH_CONFIG_MATCH_MANAGED_SYS_ID("openiam.table.syncConfig.match.managed.sys"),
    SYNCH_CONFIG_MATCH_SRC_FIELD_NAME("openiam.table.syncConfig.match.src.field"),
    SYNCH_CONFIG_CUSTOM_MATCH_RULE("openiam.table.syncConfig.custom.match.rule"),
    SYNCH_CONFIG_CUSTOM_ADAPTER_SCRIPT("openiam.table.syncConfig.custome.adapter.script"),
    SYNCH_CONFIG_CUSTOM_MATCH_ATTR("openiam.table.syncConfig.custom.match.attr"),
    SYNCH_CONFIG_WS_URL("openiam.table.syncConfig.ws.url"),
    SYNCH_CONFIG_USE_POLICY_MAP("openiam.table.syncConfig.use.policy.map"),
    SYNCH_CONFIG_USE_TRANSFORM_SCRIPT("openiam.table.syncConfig.use.transform.script"),
    SYNCH_CONFIG_POLICY_MAP_BEFORE_TRANSFORM("openiam.table.syncConfig.policy.map.before"),
    SYNCH_CONFIG_USE_SYSTEM_PATH("openiam.table.syncConfig.use.system.path"),
    SYNCH_CONFIG_PRE_SYNC_SCRIPT("openiam.table.syncConfig.pre.sync.script"),
    SYNCH_CONFIG_POST_SYNC_SCRIPT("openiam.table.syncConfig.post.sync.script"),
    SYNCH_CONFIG_COMPANY_ID("openiam.table.syncConfig.company.id"),
    SYNCH_CONFIG_ATTRIBUTE_NAMES_LOOKUP("openiam.table.syncConfig.attribute.names.lookup"),
    SYNCH_CONFIG_SEARCH_SCOPE("openiam.table.syncConfig.search.scope"),
    SYNCH_CONFIG_WS_URI("openiam.table.syncConfig.ws.uri"),
    SYNCH_CONFIG_WS_NAME_SPACE("openiam.table.syncConfig.ws.namespace"),
    SYNCH_CONFIG_WS_OPERATION("openiam.table.syncConfig.ws.operation"),
    SYNCH_CONFIG_WS_TARGET_ENTITY_PATH("openiam.table.syncConfig.ws.target.entity.path"),
    SYNCH_CONFIG_WS_ATTRIBUTES_STRING("openiam.table.syncConfig.ws.attribute.string");

    private String propertyKey;

    private ImportPropertiesKey(String key) {
        this.propertyKey = key;
    }

    public String getPropertyKey() {
        return this.propertyKey;
    }

    public static ImportPropertiesKey getByPropertyKey(String key) {
        for (ImportPropertiesKey gpk : ImportPropertiesKey.values()) {
            if (key.equals(gpk.getPropertyKey())) {
                return gpk;
            }
        }
        return null;
    }

}
