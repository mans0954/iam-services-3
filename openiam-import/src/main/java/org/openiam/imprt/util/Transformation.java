package org.openiam.imprt.util;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.openiam.base.ws.MatchType;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.loc.domain.LocationEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.OrganizationUserEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.custom.MailboxHelper;
import org.openiam.imprt.jdbc.parser.impl.UserAttributeEntityParser;
import org.openiam.imprt.model.Attribute;
import org.openiam.imprt.model.LineObject;
import org.openiam.imprt.query.expression.Column;
import org.openiam.util.encrypt.RijndaelCryptor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Transformation {

    private final String IDM_MNG_SYS_ID = "0";
    private final String AD_MNG_SYS_ID = "DD6CA4CC8BBC4D78A5879D93CEBC8A29";
    private final String PDD_EMAIL = "PDDUser@cog.akzonobel.com";

    final String ARCHIVE_CACHE_ENABLED = "g_gss_eus_maas_vaultcache_enabled - vv";
    final String ARCHIVE_CACHE_DISABLED = "g_gss_eus_maas_vaultcache_disabled - vv";
    final String INTERNET_GROUP_MASK = "a_.*_internetaccess";

    final String UNITY_ROLE_ID = "2c94b2574be50e06014be569449302ed";
    final String LYNC_MNG_SYS_ID = "2c94b25748eaf9ef01492d5507100273";
    final String EXCH_MNG_SYS_ID = "2c94b25748eaf9ef01492d5312d3026d";
    final List<String> activeStatuses = Arrays.asList("512", "544", "66048", "66080", "262656", "262688", "328192", "328224");
    final String DEFAULT_DATE = "01/01/2020 12:00:00";
    final private String baseDN = "DC=d30,DC=intra";
    //        final private String baseDN = "OU=AKZO,DC=dev,DC=local";
    final byte[] pwd = "90eb79e8-5954-4af1-b0e3-f712e25e1fca".getBytes();
    final byte[] iv = "tu89geji340t89u2".getBytes();

    public int execute(LineObject rowObj, UserEntity user, Map<String, Object> bindingMap) {
        try {
            populateObject(rowObj, user, bindingMap);
        } catch (Exception ex) {
            System.out.println(ex.getCause());
            System.out.println(ex.getStackTrace());
            System.out.println(ex);
            return -1;
        }
        return 0;
    }


    public void populateObject(LineObject lo, UserEntity user, Map<String, Object> bindingMap) throws Exception {
        List<OrganizationEntity> organizationEntityList = (List<OrganizationEntity>) bindingMap.get("ORGANIZATIONS");
        MailboxHelper mailboxHelper = (MailboxHelper) bindingMap.get("MAILBOX_HELPER");
        Map<String, String> groupsMap = (Map<String, String>) bindingMap.get("GROUPS_MAP");
        Map<String, GroupEntity> groupsMapEntities = (Map<String, GroupEntity>) bindingMap.get("GROUPS_MAP_ENTITY");
        List<LocationEntity> locations = (List<LocationEntity>) bindingMap.get("LOCATIONS");

        boolean changeDisplayName = false;
        boolean isNewUser = (user.getId() == null);
        //DistiguishedName
        String distinguishedName = this.getValue(lo.get("distinguishedName"));

        if (distinguishedName.toLowerCase().contains("OU=IAMHolding,OU=Unity,DC=d30,DC=intra".toLowerCase())) {
            throw new Exception("Default holder user. Skip for sync!!!");
        }

        addUserAttribute(user, new UserAttributeEntity("distinguishedName", distinguishedName));
        //sAMAccoutName
        String samAccountName = this.getValue(lo.get("sAMAccountName"));
        //Expiration Date
        DateFormat formatter;
        Date verDateMax;
        Date expDate = null;
        formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        verDateMax = formatter.parse(DEFAULT_DATE);
        Date dateBase = formatter.parse("01/01/1601 00:00:00");
        Date maxDate = formatter.parse("01/01/8000 00:00:00");
        String str = this.getValue(lo.get("accountExpires"));
        if (expDate != null) {
            addUserAttribute(user, new UserAttributeEntity("accountExpires", formatter.format(expDate)));
        } else {
            addUserAttribute(user, new UserAttributeEntity("accountExpires", null));
        }
        if (("9223372036854775807".equals(str)) || ("0".equals(str))) {
            expDate = null;
        } else {
            try {
                Long tmpD = Long.valueOf(str) / 864000000000L;
                int days = tmpD.intValue();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateBase);
                calendar.add(Calendar.DATE, days);
                expDate = calendar.getTime();
                if (expDate.after(maxDate)) {
                    expDate = maxDate;
                }
            } catch (Exception exp1) {
                System.out.println("WARN! cant parse exp Date:" + str);
            }
        }
        // Logins
        this.updateLogins(user, samAccountName, expDate);
        //employeeId
        String emplId = this.getValue(lo.get("employeeNumber"));

        if (emplId != null && !emplId.contains("@")) {
            while (emplId.length() < 8) emplId = "0" + emplId;
        }
        user.setEmployeeId(emplId);

        //First Name
        String fn = this.getValue(lo.get("givenName"));
        if (StringUtils.isNotBlank(fn)) {
            user.setFirstName(fn.substring(0, 1).toUpperCase() + fn.substring(1));
        } else {
            user.setFirstName("Admin");
        }

        //Last Name
        String surname = this.getValue(lo.get("sn"));
        if (StringUtils.isNotBlank(surname)) {
            String prefixLastName = user.getPrefixLastName();
            if (StringUtils.isNotBlank(prefixLastName)) {
                surname = surname.replace(prefixLastName.trim(), "").trim();
            } else {
                user.setPrefixLastName(null);
            }
            user.setLastName(surname);
        } else {
            user.setLastName(samAccountName);
        }

        //Initials
        String initials = this.getValue(lo.get("initials"));
        if (StringUtils.isBlank(initials) || "null".equalsIgnoreCase(initials)) {
            initials = user.getFirstName().substring(0, 1);
        }
        user.setMiddleInit(initials);

        //displayName
        String displayName = this.getValue(lo.get("displayName"));
        if (StringUtils.isBlank(displayName) || "null".equalsIgnoreCase(displayName)) {
            displayName = genName(user.getLastName(), user.getFirstName(), user.getMiddleInit(), user.getPrefixLastName());
        }

        //NickName
        user.setNickname(displayName);
        addUserAttribute(user, new UserAttributeEntity("displayName", displayName));
        //Title
        user.setTitle(this.getValue(lo.get("title")));
        // Bu Code (extenstionAttribute15)
        String extensionAttribute15 = this.getValue(lo.get("extensionAttribute15"));
        addUserAttribute(user, new UserAttributeEntity("BU_CODE", extensionAttribute15));
        // Service Type (extensionAttribute2)
        String serviceTypeAttr = this.getValue(lo.get("extensionAttribute2"));
        addUserAttribute(user, new UserAttributeEntity("serviceType", serviceTypeAttr));

        String sbu = this.getValue(lo.get("extensionAttribute5"));
        addUserAttribute(user, new UserAttributeEntity("ORG_SBU_SHORT_NAME", serviceTypeAttr));
        String msExchExtensionAttribute16 = this.getValue(lo.get("msExchExtensionAttribute16"));
        if ("1".equalsIgnoreCase(msExchExtensionAttribute16)) {
            addUserAttribute(user, new UserAttributeEntity("syncToCloud", "On"));
        } else {
            addUserAttribute(user, new UserAttributeEntity("syncToCloud", "Off"));
        }
        // Start Date
        try {
            String str2 = this.getValue(lo.get("whenCreated"));
            DateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmmss");
            String[] str3 = str2.split(".0Z");
            Date startDate = formatter2.parse(str3[0]);
            user.setStartDate(startDate);
        } catch (Exception e) {
            System.out.println("WARN! cant parse Start Date:" + str);
        }

        //MD_TYPE, Classification, Service
        String mdTypeId = null;

        String attr = this.getValue(lo.get("userPrincipalName"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("UserPrincipalName", attr));
        }

        if (StringUtils.isNotBlank(samAccountName)) {
            addUserAttribute(user, new UserAttributeEntity("sAMAccountName", samAccountName));
        }

        attr = this.getValue(lo.get("employeeID"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("localEmployeeNumber", attr));
        }

        attr = this.getValue(lo.get("employeeType"));
        if (StringUtils.isNotBlank(attr)) {
            if (attr.contains("Long Term Absence")) {
                addUserAttribute(user, new UserAttributeEntity("employeeType", "Employee"));
                addUserAttribute(user, new UserAttributeEntity("longTermAbsence", "On"));
            } else {
                addUserAttribute(user, new UserAttributeEntity("employeeType", attr));
                addUserAttribute(user, new UserAttributeEntity("longTermAbsence", "Off"));
            }
        }

        attr = this.getValue(lo.get("otherName"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("otherName", attr));
        }

        attr = this.getValue(lo.get("division"));
        if (StringUtils.isNotBlank(attr)) {
//            addUserAttribute(user, new UserAttributeEntity("division", attr));
            addUserAttribute(user, new UserAttributeEntity("REGION", attr));
        }

        attr = this.getValue(lo.get("department"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("department", attr));
        }

        attr = this.getValue(lo.get("description"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("description", attr));
        }

        attr = this.getValue(lo.get("info"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("notes", attr));
        }

        //Location (Addess)
        attr = this.getValue(lo.get("l"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("CITY", attr));
        }
        //Location (Addess)
        attr = this.getValue(lo.get("c"));
        String country = null;
        if (StringUtils.isNotBlank(attr)) {
            country = attr;
            addUserAttribute(user, new UserAttributeEntity("COUNTRY", attr));
        }
        attr = this.getValue(lo.get("co"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("COUNTRY_CO", attr));
        }
        attr = this.getValue(lo.get("postalCode"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("POSTAL_CODE", attr));
        }
        attr = this.getValue(lo.get("st"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("st", attr));
        }
        attr = this.getValue(lo.get("streetAddress"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("STREET_ADDRESS", attr));
        }
        attr = this.getValue(lo.get("office"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("Office", attr));
        }

        // Add Address
        try {
            AddressEntity adr = new AddressEntity();
            adr.setName("CurrentAddress");
            adr.setCountry(this.getValue(lo.get("co")));
            adr.setCity(this.getValue(lo.get("l")));
            adr.setAddress1(this.getValue(lo.get("streetAddress")));
            adr.setAddress2(this.getValue(lo.get("office")));
            adr.setState(this.getValue(lo.get("st")));
            adr.setPostalCd(this.getValue(lo.get("postalCode")));
            MetadataTypeEntity metadataTypeEntity = new MetadataTypeEntity();
            metadataTypeEntity.setId("OFFICE_ADDRESS");
            adr.setMetadataType(metadataTypeEntity);
            addUserAddress(user, adr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Primary Email

        attr = this.getValue(lo.get("mail"));
        String emailAddressValue = "";
        if (StringUtils.isNotBlank(attr)) {
            emailAddressValue = attr;
            this.addEmail(user, attr);
        }

        //phones
        for (PhoneEntity e : user.getPhones()) {
            e.setDescription("DELETE_FROM_DB");
        }

        attr = this.getValue(lo.get("telephoneNumber"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("OfficePhone", attr));
            addPhone(attr, "OFFICE_PHONE", user);
        }
        attr = this.getValue(lo.get("mobile"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("MobilePhone", attr));
            addPhone(attr, "CELL_PHONE", user);
        }
        attr = this.getValue(lo.get("fax"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("Fax", attr));
            addPhone(attr, "FAX", user);
        }

        String attr13 = this.getValue(lo.get("extensionAttribute13"));
        if (StringUtils.isNotBlank(attr13)) {
            String attr13decr = null;
            try {
                attr13decr = new RijndaelCryptor().decrypt(pwd, iv, attr13);
            } catch (Exception ex) {
                System.out.println("Error on extensionAttribute13 decript.  extensionAttribute13=" + attr13);
            }
            if (StringUtils.isNotBlank(attr13decr)) {
                for (String curStr : attr13decr.split(";")) {
                    String[] curPh = curStr.split(":");
                    if (curPh.length == 2) {
                        if (StringUtils.isNotBlank(curPh[1]) && StringUtils.isNotBlank(curPh[0])) {
                            if ("tablet".equalsIgnoreCase(curPh[0])) {
                                addPhone(curPh[1].trim(), "tablet", user);
                            } else if ("voice".equalsIgnoreCase(curPh[0])) {
                                addPhone(curPh[1].trim(), "voice", user);
                            } else if ("data".equalsIgnoreCase(curPh[0])) {
                                addPhone(curPh[1].trim(), "data", user);
                            } else if ("dongle".equalsIgnoreCase(curPh[0])) {
                                addPhone(curPh[1].trim(), "dongle", user);
                            }
                        }
                    }
                }
            }
        }

        //Drive & Directory
        attr = this.getValue(lo.get("homeDirectory"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("homeDirectory", attr));
        }
        attr = this.getValue(lo.get("homeDrive"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("homeDrive", attr));
        }

        attr = this.getValue(lo.get("extensionAttribute1"));
        addUserAttribute(user, new UserAttributeEntity("legalEntity", attr));

        attr = this.getValue(lo.get("extensionAttribute3"));
        addUserAttribute(user, new UserAttributeEntity("discipline", attr));

        attr = this.getValue(lo.get("extensionAttribute8"));
        addUserAttribute(user, new UserAttributeEntity("CLUSTER", attr));

        attr = this.getValue(lo.get("extensionAttribute9"));
        String siteCode = attr;
        addUserAttribute(user, new UserAttributeEntity("siteCode", attr));

        boolean isService = false;

        attr = this.getValue(lo.get("extensionAttribute14"));
        addUserAttribute(user, new UserAttributeEntity("extensionAttribute14", attr));
        boolean isMDM = "MDM".equalsIgnoreCase(attr);
        String classification = attr == null ? "None" : attr;
        addUserAttribute(user, new UserAttributeEntity("classification", attr));
        if (samAccountName.length() > 4) {
            switch (samAccountName.substring(0, 4).toLowerCase()) {
                case "adm_":
                    mdTypeId = "AKZONOBEL_ADM_ACCOUNT";
                    classification = "ADM";
                    break;
                case "srv_":
                    mdTypeId = "AKZONOBEL_SRV_ACCOUNT";
                    classification = "SVC";
                    isService = true;
                    break;
                case "tst_":
                    mdTypeId = "AKZONOBEL_TST_ACCOUNT";
                    classification = "TST";
                    isService = true;
                    break;
                case "rsc_":
                    mdTypeId = "AKZONOBEL_RSC_ACCOUNT";
                    classification = "RSC";
                    isService = true;
                    break;
                case "pos_":
                    mdTypeId = "AKZONOBEL_POS_ACCOUNT";
                    classification = "POS";
                    isService = true;
                    break;
            }
        }
        //Home MDB
        String homeMDB = this.getValue(lo.get("homeMDB"));
        if (!isService) {
            boolean isNoMBX = StringUtils.isBlank(homeMDB) || PDD_EMAIL.equalsIgnoreCase(this.getValue(lo.get("mail")));
            mdTypeId = isNoMBX ? "AKZONOBEL_USER_NO_MBX" : "AKZONOBEL_USER_MBX";
        }
        //serviceAccountName
        addUserAttribute(user, new UserAttributeEntity("serviceAccountName", isService ? samAccountName : null));
        attr = this.getValue(lo.get("company"));
        addUserAttribute(user, new UserAttributeEntity("ORG_NAME", attr));

        //Managers
        attr = this.getValue(lo.get("managers"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("managers", attr));
        }
        //TODO implement add supervisor to user
        //
        String mbType = this.getValue(lo.get("msExchRecipientTypeDetails"));
        System.out.println("mailbox type=" + mbType);
        // Set mailbox
        try {
            if ("1".equals(mbType)) {
                System.out.println("I'm standard mailbox");
                String mailboxSize = mailboxHelper.getBoxSize(homeMDB);
                System.out.println("MailboxSize=" + mailboxSize);
                if (StringUtils.isNotBlank(mailboxSize) || StringUtils.isBlank(homeMDB)) {
                    addUserAttribute(user, new UserAttributeEntity("mailbox", mailboxSize));
                } else if ("2147483648".equals(mbType)) {
                    System.out.println("I'm remote mailbox");
                    addUserAttribute(user, new UserAttributeEntity("mailbox", "O365"));
                }
            }
        } catch (Exception e) {
            System.out.println("Problem with mailbox Definitions");
        }

//        // MemberOf
//        Attribute mOfAttr = lo.get("memberOf");
//        String[] memberOf = null;
//        if (mOfAttr != null) {
//            if (mOfAttr.isMultiValued()) {
//                memberOf = mOfAttr.getValueList().toArray(new String[mOfAttr.getValueList().size()]);
//            } else {
//                memberOf = mOfAttr.getValue() == null ? null : mOfAttr.getValue().split("/\\,/");
//            }
//        }
//        boolean isCacheEnabled = containsNameGroup(memberOf, groupsMap, ARCHIVE_CACHE_ENABLED);
//        boolean isCacheDisabled = containsNameGroup(memberOf, groupsMap, ARCHIVE_CACHE_DISABLED);
//        boolean isInternet = containsMaskGroup(memberOf, groupsMap, INTERNET_GROUP_MASK);
//
        boolean isPDD = ("AKZONOBEL_USER_NO_MBX".equals(mdTypeId) && PDD_EMAIL.equalsIgnoreCase(emailAddressValue));
//        addUserAttribute(user, new UserAttributeEntity("internetAccess", isInternet ? "On" : null));
        addUserAttribute(user, new UserAttributeEntity("mdm", isMDM ? "On" : null));
        addUserAttribute(user, new UserAttributeEntity("activeSync", isMDM ? "Off" : null));
        addUserAttribute(user, new UserAttributeEntity("lyncMobility", isMDM ? "On" : null));
        addUserAttribute(user, new UserAttributeEntity("PDDAccount", isPDD ? "On" : null));
//
//        if (isCacheEnabled) {
//            addUserAttribute(user, new UserAttributeEntity("archieve", "Cached - Laptop"));
//        } else if (isCacheDisabled) {
//            addUserAttribute(user, new UserAttributeEntity("archieve", "Non-Cached - Desktop"));
//        } else {
//            addUserAttribute(user, new UserAttributeEntity("archieve", null));
//        }
        if (isMDM) {
            classification = "MDM";
        } else if (isPDD) {
            classification = "PDD";
        }
        if (isMDM) {
            addRoleId(user, "MDM_ROLE_ID");
        } else {
            removeRoleId(user, "MDM_ROLE_ID");
        }
//        try {
//            mergeGroups(memberOf, groupsMapEntities, user);
//        } catch (Exception e) {
//            System.out.println("Problems with merge groups");
//        }
        //TODO Reveal what is and how to define option: 'longTermAbsence'
        //TODO get Citrix attributes: terminalServiceHomeD...

        // Classification
        //TODO Reveal what is and how to define Classifications: 'RAA', 'LowFrequencyUser'
        addUserAttribute(user, new UserAttributeEntity("classification", classification));

        //Manager
        addSuper(user, this.getValue(lo.get("manager")));
        //For AD
        addRoleId(user, "8a8da02e51a28fd20151a291ce360002");

        String accessRoleId = ("AKZONOBEL_USER_MBX".equals(mdTypeId) ||
                "AKZONOBEL_USER_NO_MBX".equals(mdTypeId)) ? "1" : "AKZONOBEL_ADM_ACCOUNT".equals(mdTypeId) ? "SUPPORT_ADMIN_ROLE_ID" : "SERVICE_ROLE_ID";
        if (StringUtils.isNotBlank(accessRoleId)) {
            addRoleId(user, accessRoleId);
        }

        // Add HP Admin role
        boolean isHpAdmin = StringUtils.isNotBlank(distinguishedName) && distinguishedName.matches("/.*,OU=Administrators,.*OU=HP,.*/");
        if (isHpAdmin) {
            for (RoleEntity re : user.getRoles()) {
                if ("HP_ADMIN_ROLE_ID".equals(re.getId())) {
                    addRoleId(user, "HP_ADMIN_ROLE_ID");
                }
//                if (re.getId() == UNITY_ROLE_ID) {
//                    addRoleId(user, UNITY_ROLE_ID);
//                }
            }
        }

        // Exchange
        String userPrincipalName = this.getValue(lo.get("userPrincipalName"));

        // PROD
        updateLoginAndRole(StringUtils.isNotBlank(homeMDB) ? userPrincipalName : null, EXCH_MNG_SYS_ID, user, "EXCHANGE_ROLE_ID");

        // STAGING
//        updateLoginAndRole(StringUtils.isNotBlank(homeMDB) ? userPrincipalName : null, EXCH_MNG_SYS_ID, user, "8a8da02e5497f2b90154a6c24d142340");

        // lync
        String sipAddress = this.getValue(lo.get("msRTCSIP-PrimaryUserAddress"));
        sipAddress = StringUtils.isNotBlank(sipAddress) && sipAddress.startsWith("sip:") ? sipAddress.substring(4) : null;
        updateLoginAndRole(sipAddress, LYNC_MNG_SYS_ID, user, "LYNC_ROLE_ID");
        addUserAttribute(user, new UserAttributeEntity("lync", StringUtils.isNotBlank(sipAddress) ? "On" : null));

        //Update mdType for user
        if (StringUtils.isNotBlank(mdTypeId)) {
            MetadataTypeEntity metadataTypeEntity = new MetadataTypeEntity();
            metadataTypeEntity.setId(mdTypeId);
            user.setType(metadataTypeEntity);
        }

        getLinkedOrganization(distinguishedName, siteCode, extensionAttribute15, country, organizationEntityList, locations, user);

        //status
        String status = this.getValue(lo.get("userAccountControl"));
        if (activeStatuses.contains(status)) {
            user.setStatus(UserStatusEnum.ACTIVE);
            user.setSecondaryStatus(null);
        } else {
            user.setSecondaryStatus(UserStatusEnum.DISABLED);
            if (StringUtils.isNotBlank(emailAddressValue) && emailAddressValue.contains(".iamterm")) {
                user.setStatus(UserStatusEnum.LEAVE);
            }
        }
        //here is some extra fields that we should provision

        Attribute proxyAttr = lo.get("proxyAddresses");
        System.out.println("Proxy address bean=" + proxyAttr);
        if (proxyAttr != null) {
            UserAttributeEntity userAttributeEntity = new UserAttributeEntity();
            userAttributeEntity.setName("proxyAddress");
            if (proxyAttr.getValueList() != null) {
                userAttributeEntity.setValue(StringUtils.join(proxyAttr.getValueList(), "\n"));
            } else {
                userAttributeEntity.setValue(proxyAttr.getValue());
            }
            user.addUserAttribute(userAttributeEntity);
        }
    }

    private void updateLoginAndRole(String login, String managedSystemId, UserEntity user, String roleId) {
        try {
            LoginEntity lg = null;
            for (LoginEntity le : user.getPrincipalList()) {
                if (le.getManagedSysId().equals(managedSystemId)) {
                    lg = le;
                    break;
                }
            }
            RoleEntity userRole = null;
            if (StringUtils.isNotBlank(roleId)) {
                for (RoleEntity re : user.getRoles()) {
                    if (re.getId().equals(roleId)) {
                        userRole = re;
                    }
                }
            }
            if (StringUtils.isNotBlank(login)) {
                if (lg == null) {
                    lg = new LoginEntity();
                    lg.setManagedSysId(managedSystemId);
                    lg.setCreateDate(new Date());
                    lg.setStatus(LoginStatusEnum.ACTIVE);
                    lg.setUserId(user.getId());
                    lg.setLogin(login);
                    user.getPrincipalList().add(lg);
                } else if (!lg.getLogin().equals(login)) {
                    lg.setLogin(login);
                }
                addRoleId(user, roleId);
            } else {
                if (lg != null) {
                    lg.setLogin("DELETE_FROM_DB");
                }
                if (userRole != null)
                    removeRoleId(user, userRole.getId());
            }
        } catch (Exception e) {
            System.out.println("Problems inside of updateLoginAndRole ");
        }
    }

    public boolean containsNameGroup(String[] userADNames, Map<String, String> groupsMap, String toFind) {
        boolean retVal = false;
        if (userADNames == null)
            return false;
        try {
            String dn = groupsMap.get(toFind);

            if (dn != null) {
                return Arrays.asList(userADNames).contains(dn);
            }
        } catch (Exception e) {
            System.out.println("Error with Groups Contains. ToFind=" + toFind);
        }
        return retVal;
    }

    public boolean containsMaskGroup(String[] userADNames, Map<String, String> groupsMap, String mask) {
        if (userADNames == null)
            return false;
        try {
            List<String> userADNamesList = Arrays.asList(userADNames);
            Pattern pattern = Pattern.compile(mask);
            Matcher matcher = null;
            for (String key : groupsMap.keySet()) {
                matcher = pattern.matcher(key);
                if (matcher.matches()) {
                    String dn = groupsMap.get(key);
                    if (userADNamesList.contains(dn)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Problems with groups again! MASK");
        }

        return false;
    }

    private void mergeGroups(String[] membersOf, Map<String, GroupEntity> groupsEntities, UserEntity user) {
        if (membersOf == null)
            return;
        for (String member : membersOf) {
            GroupEntity groupEntity = groupsEntities.get(member.toLowerCase());
            if (groupEntity != null) {
                if (user.getGroups() == null) {
                    user.setGroups(new HashSet<GroupEntity>());
                }
                boolean isFind = false;
                for (GroupEntity gr : user.getGroups()) {
                    if (gr.getId().equals(groupEntity.getId())) {
                        isFind = true;
                        break;
                    }
                }
                if (!isFind) {
                    //ADD MARKER THAT SHOULD BE ADDED
                    groupEntity.setName("ADD_TO_DB");
                    user.getGroups().add(groupEntity);
                }
            }
        }
    }

    private void updateLogins(UserEntity user, String samAccountName, Date expDate) {
        int flOp = 0;
        int flAd = 0;
        try {
            List<LoginEntity> logList = user.getPrincipalList();
            for (LoginEntity lg : logList) {
                if (IDM_MNG_SYS_ID.equals(lg.getManagedSysId())) {
                    flOp = 1;
                    lg.setLogin(samAccountName);
                    lg.setPwdExp(expDate);
                }
                if (AD_MNG_SYS_ID.equals(lg.getManagedSysId())) {
                    flAd = 1;
                    lg.setLogin(samAccountName);
                    lg.setPwdExp(expDate);
                }
            }
            if (flOp == 0) {
                LoginEntity lg = new LoginEntity();
                lg.setManagedSysId(IDM_MNG_SYS_ID);
                lg.setLogin(samAccountName);

                String pswd = "Password$51";
                lg.setPassword(pswd);
                lg.setPwdExp(expDate);
                logList.add(lg);
                user.setDefaultLogin(samAccountName);
                user.setStatus(UserStatusEnum.ACTIVE);
            }
            if (flAd == 0) {
                LoginEntity lg = new LoginEntity();
                lg.setManagedSysId(AD_MNG_SYS_ID);
                lg.setLogin(samAccountName);

                String pswd = "Password$51";
                lg.setPassword(pswd);
                lg.setPwdExp(expDate);
                logList.add(lg);
            }
            user.setPrincipalList(logList);
        } catch (Exception e) {
            System.out.println("WARN! cant parse exp Date:" + e);
        }
    }

    private void addRoleId(UserEntity user, String roleId) {
        boolean foundRole = false;
        for (RoleEntity re : user.getRoles()) {
            if (re.getId().equalsIgnoreCase(roleId)) {
                foundRole = true;
                break;
            }
        }
        if (!foundRole) {
            RoleEntity role = new RoleEntity();
            role.setId(roleId);
            role.setName("ADD_TO_DB");
            user.getRoles().add(role);
        }
    }

    private void removeRoleId(UserEntity user, String roleId) {
        for (RoleEntity re : user.getRoles()) {
            if (re.getId().equalsIgnoreCase(roleId)) {
                re.setName("DELETE_FROM_DB");
                break;
            }
        }
    }

    private String getValue(Attribute attribute) {
        String retVal = null;
        if (attribute != null) {
            retVal = attribute.getValue();
            if ("NULL".equalsIgnoreCase(retVal)) {
                retVal = null;
            }
        }
        return retVal;
    }

    private void addUserAttribute(UserEntity user, UserAttributeEntity attr) {
        if (user.getUserAttributes().get(attr.getName()) != null) {
            user.updateUserAttribute(attr);
        } else {
            user.addUserAttribute(attr);
        }
    }

    public String genName(String lastName, String givenName, String initials, String infix) {
        if ((initials == null) || ("null".equalsIgnoreCase(initials))) {
            initials = "";
        }
        initials = initials.replaceAll("\\s+|\\.", "").toUpperCase();
        StringBuilder inits = new StringBuilder();
        for (char ch : initials.toCharArray()) {
            inits.append(ch).append(".");
        }
        if ((infix == null) || ("null".equalsIgnoreCase(infix))) {
            infix = "";
        }
        if ((givenName == null) || ("null".equalsIgnoreCase(givenName))) {
            givenName = "";
        }
        if ((lastName == null) || ("null".equalsIgnoreCase(lastName))) {
            lastName = "";
        }
        return (lastName + ", " + inits.toString() + (StringUtils.isNotBlank(infix) ? " " + infix : "") + " (" + givenName + ")");
    }

    public void addEmail(UserEntity user, String emailAddressValue) {
        try {
            EmailAddressEntity email = new EmailAddressEntity();
            if (StringUtils.isNotBlank(emailAddressValue)) {
                email.setName("PRIMARY_EMAIL");
                MetadataTypeEntity metadataTypeEntity = new MetadataTypeEntity();
                metadataTypeEntity.setId("PRIMARY_EMAIL");
                email.setMetadataType(metadataTypeEntity);
                email.setIsDefault(true);
                email.setIsActive(true);
                email.setEmailAddress(emailAddressValue);
                addUserEmail(user, email);
            }
        } catch (Exception e) {
            System.out.println("ERROR add Email=" + e);
        }
    }

    private void addUserEmail(UserEntity user, EmailAddressEntity email) {
        for (EmailAddressEntity ee : user.getEmailAddresses()) {
            if (ee.getMetadataType().getId().equals(email.getMetadataType().getId())) {
                ee.setName(email.getName());
                ee.setEmailAddress(email.getEmailAddress());
                ee.setIsDefault(email.getIsDefault());
                ee.setIsActive(email.getIsActive());
                return;
            }
        }
        user.getEmailAddresses().add(email);
    }

    private void addPhone(String phoneValue, String metatypeId, UserEntity user) {
        PhoneEntity ph = new PhoneEntity();
        MetadataTypeEntity metatype = new MetadataTypeEntity();
        metatype.setId(metatypeId);
        ph.setName(metatype.getId().replaceAll("_", " "));
        ph.setMetadataType(metatype);
        ph.setIsDefault(false);
        String[] parts = phoneValue.split(" ");
        if (parts != null && parts.length > 1) {
            String newP = "";
            for (int i = 1; i < parts.length; i++) {
                newP += parts[i];
            }
            ph.setPhoneNbr(newP);
            ph.setCountryCd(parts[0]);
        } else {
            ph.setPhoneNbr(phoneValue);
        }
        addPhone(user, ph);
    }

    private void addPhone(UserEntity user, PhoneEntity phone) {
        /*
        for (PhoneEntity e : user.getPhones()) {
            if (e.getMetadataType().equals(phone.getMetadataType())) {
                e.setName(phone.getName());
                e.setIsDefault(phone.getIsDefault());
                e.setPhoneNbr(phone.getPhoneNbr());
                e.setPhoneExt(phone.getPhoneExt());
                e.setCountryCd(phone.getCountryCd());
                return;
            }
        }
        */
        user.getPhones().add(phone);
    }

    private void addUserAddress(UserEntity user, AddressEntity addr) {
        for (AddressEntity ue : user.getAddresses()) {
            if (ue.getMetadataType().equals(addr.getMetadataType())) {
                ue.setName(addr.getName());
                ue.setCountry(addr.getCountry());
                ue.setCity(addr.getCity());
                ue.setPostalCd(addr.getPostalCd());
                ue.setAddress1(addr.getAddress1());
                ue.setAddress2(addr.getAddress2());
                ue.setState(addr.getState());
                return;
            }
        }
        user.getAddresses().add(addr);
    }

    private void addSuper(UserEntity user, String dn) {
        if (StringUtils.isBlank(dn)) {
            return;
        }

        final List<Column> userAttributeColumnList = Utils.getColumns(new ImportPropertiesKey[]{ImportPropertiesKey.USER_ATTRIBUTES_ID, ImportPropertiesKey.USER_ATTRIBUTES_USER_ID,
                ImportPropertiesKey.USER_ATTRIBUTES_NAME,
                ImportPropertiesKey.USER_ATTRIBUTES_VALUE});

        final String getUserAttributeByUserDN = "SELECT %s FROM USER_ATTRIBUTES ua WHERE ua.NAME='distinguishedName' AND ua.VALUE='%s'";
        UserAttributeEntityParser attributeEntityParser = new UserAttributeEntityParser();
        String userId = null;
        try {
            List<UserAttributeEntity> userAttributeEntityList = attributeEntityParser.get(
                    String.format(getUserAttributeByUserDN, Utils.columnsToSelectFields(userAttributeColumnList, "ua"),
                            dn), userAttributeColumnList);
            if (CollectionUtils.isNotEmpty(userAttributeColumnList)) {
                userId = userAttributeEntityList.get(0).getUserId();
            }
        } catch (Exception e) {
        }

        if (userId != null) {
            if (user.getSupervisors() == null) {
                user.setSupervisors(new HashSet<SupervisorEntity>());
            }
            boolean isFind = false;
            for (SupervisorEntity se : user.getSupervisors()) {
                if (se.getSupervisor() != null) {
                    if (userId.equals(se.getSupervisor().getId())) {
                        isFind = true;
                    } else {
                        se.getSupervisor().setFirstName("DELETE_FROM_DB");
                    }
                }
            }
            if (!isFind) {
                SupervisorEntity supervisorEntity = new SupervisorEntity();
                supervisorEntity.setSupervisor(new UserEntity());
                supervisorEntity.getSupervisor().setId(userId);
                supervisorEntity.setIsPrimarySuper(true);
                supervisorEntity.getSupervisor().setFirstName("ADD_TO_DB");
                user.getSupervisors().add(supervisorEntity);
            }
        }
    }

    final private String globalAdminId = "ADMIN_GL_ROLE_ID";
    final private String serviceTypeAdminId = "ADMIN_ST_ROLE_ID";
    final private String buAdminId = "ADMIN_BU_ROLE_ID";
    final private String siteCodeAdminId = "ADMIN_SITE_ROLE_ID";
    final private List<String> serviceTypes = Arrays.asList(new String[]{"unity", "unitygsd", "unitymwls"});

    private void addCorrectAdministratorRole(UserEntity user, List<OrganizationEntity> organizationEntities, String adPath) {
        if (StringUtils.isBlank(adPath)) {
            return;
        }
        String attributeValue = "";
        if (baseDN.equalsIgnoreCase(adPath)) {
            attributeValue = adPath;
            //global admin
            this.addRoleId(user, globalAdminId);
        }
        if (adPath.toLowerCase().contains("ou=hp,ou=unity,dc=d30,dc=intra".toLowerCase())) {
            attributeValue = "ou=unity,dc=d30,dc=intra";
            this.removeRoleId(user, buAdminId);
            this.removeRoleId(user, globalAdminId);
            this.removeRoleId(user, siteCodeAdminId);
            this.addRoleId(user, serviceTypeAdminId);
            this.addRoleId(user, "HP_ADMIN_ROLE_ID");
        } else {
            adPath = adPath.replace("," + baseDN.toLowerCase(), "").replace("OU=", "");
            String[] adPathParts = adPath.split(",");
            if (adPathParts != null) {
                for (String pa : adPathParts) {
                    System.out.println(pa);
                }
            }
            if (adPathParts != null) {
                if (adPathParts.length == 3) {
                    //all service type, BU, siteCode is presented
                    this.addRoleId(user, siteCodeAdminId);
                } else if (adPathParts.length == 2) {
                    //possible combiniations site + BU or BU + Service Type
                    if (serviceTypes.contains(adPathParts[1].toLowerCase())) {
                        //2nd is serviceType, than 1st is BU -> buAdmin
                        this.addRoleId(user, buAdminId);
                    } else {
                        //so this is site admin
                        this.addRoleId(user, siteCodeAdminId);
                    }
                } else if (adPathParts.length == 1) {
                    //possible BU or service Type
                    if (serviceTypes.contains(adPathParts[0].toLowerCase())) {
                        //2nd is serviceType, than 1st is BU -> buAdmin
                        this.addRoleId(user, serviceTypeAdminId);
                    } else {
                        //so this is site admin
                        this.addRoleId(user, buAdminId);
                    }
                }
            }
            attributeValue = adPath + ("," + baseDN.toLowerCase());
        }

        this.addUserAttribute(user, new UserAttributeEntity("DLG_FLT_PARAM",
                String.format("\"%s\";\"%s\";\"%s\"", "AD_PATH", attributeValue, MatchType.END_WITH)));
    }

    private void getLinkedOrganization(String distinguishedName, String site, String bu, String country, List<OrganizationEntity> orgs, List<LocationEntity> locations, UserEntity user) {
        try {
            String adPath = null;
            if (distinguishedName.contains("OU=UserTransfer,DC=d30,DC=intra")) {
                adPath = "OU=UserTransfer,DC=d30,DC=intra";
            } else {
                Pattern pattern = Pattern.compile(".*OU=(Users|Administrators|ResourceMailbox|Service Accounts|Services Accounts|External Accounts|Resources|Remote Access Users|Expired),(.*)".toLowerCase());
                Matcher matcher = pattern.matcher(distinguishedName.toLowerCase());
                if (matcher.matches()) {
                    adPath = matcher.group(2);
                }
            }
            if (StringUtils.isBlank(adPath)) {
                addUserAttribute(user, new UserAttributeEntity("FAIL_ROLE_EXTRACT_PATH_FROM_DN", distinguishedName));
                return;
            } else {
                addUserAttribute(user, new UserAttributeEntity("AD_PATH", adPath));
                //try to understand type of administrator
                if (distinguishedName.contains("OU=Administrators")) {
                    this.addCorrectAdministratorRole(user, orgs, adPath);
                }

            }
            if (bu == null) {
                return;
            }

            OrganizationEntity organizationEntity = null;
            for (OrganizationEntity o : orgs) {
                if (bu.equalsIgnoreCase(o.getInternalOrgId())) {
                    organizationEntity = o;
                    break;
                }
            }
            addOrganization(user, organizationEntity);

            if (site == null) {
                return;
            }


            OrganizationEntity siteEntity = null;
            for (OrganizationEntity o : organizationEntity.getChildOrganizations()) {
                if (site.equalsIgnoreCase(o.getInternalOrgId())) {
                    siteEntity = o;
                    break;
                }
            }

            if (siteEntity != null) {
                for (LocationEntity l : locations) {
                    if (siteEntity.getId().equals(l.getOrganizationId())) {
                        addUserAttribute(user, new UserAttributeEntity("LOCATION_ID", l.getLocationId()));
                        if (country == null)
                            addUserAttribute(user, new UserAttributeEntity("COUNTRY", l.getCountry()));
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Problem in Orgs definition");
        }
    }

    private void addOrganization(UserEntity user, OrganizationEntity org) {
        if (org == null) {
            return;
        }
        if (CollectionUtils.isEmpty(user.getOrganizationUser())) {
            user.setOrganizationUser(new HashSet<OrganizationUserEntity>());
            OrganizationUserEntity organizationUserEntity = new OrganizationUserEntity(user.getId(), org.getId(), "DEFAULT_AFFILIATION");
            organizationUserEntity.getMetadataTypeEntity().setDescription("ADD_TO_DB");
            user.getOrganizationUser().add(organizationUserEntity);
            return;
        } else {
            boolean isFind = false;
            for (OrganizationUserEntity oue : user.getOrganizationUser()) {
                if (oue.getOrganization() != null) {
                    if (oue.getOrganization().getId().equals(org.getId())) {
                        isFind = true;
                    } else {
                        if (oue.getMetadataTypeEntity() == null) {
                            oue.setMetadataTypeEntity(new MetadataTypeEntity());
                        }
                        oue.getMetadataTypeEntity().setDescription("DELETE_FROM_DB");
                    }
                }
            }
            if (!isFind) {
                user.setOrganizationUser(new HashSet<OrganizationUserEntity>());
                OrganizationUserEntity organizationUserEntity = new OrganizationUserEntity(user.getId(), org.getId(), "DEFAULT_AFFILIATION");
                organizationUserEntity.getMetadataTypeEntity().setDescription("ADD_TO_DB");
                user.getOrganizationUser().add(organizationUserEntity);
            }
        }

    }

}