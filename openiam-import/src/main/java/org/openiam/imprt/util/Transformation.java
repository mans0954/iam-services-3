package org.openiam.imprt.util;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.loc.domain.LocationEntity;
import org.openiam.idm.srvc.loc.dto.Location;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.OrganizationUserEntity;
import org.openiam.idm.srvc.org.dto.Organization;
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

    final String META_DATA_TYPE_ID_FOR_SERVICE_TYPE_ROLE = "2c94b2574bc5f9d0014bdf2976ac0a70";
    final String META_DATA_TYPE_ID_FOR_BUSINESS_UNIT_ROLE = "2c94b2574bc5f9d0014bdf24d9dc0a61";
    final String META_DATA_TYPE_ID_FOR_ORGANIZATIONAL_ROLE = "2c94b2574a7c3454014a9608add231df";
    final String DEFAULT_DATE = "01/01/2020 12:00:00";

    final String badRoleId = "2";

    public int execute(LineObject rowObj, UserEntity user, Map<String, Object> bindingMap) {
        try {
            populateObject(rowObj, user, bindingMap);
        } catch (Exception ex) {
            System.out.println(ex);
            return -1;
        }
        user.setStatus(UserStatusEnum.ACTIVE);

        return 0;
    }


    public void populateObject(LineObject lo, UserEntity user, Map<String, Object> bindingMap) throws Exception {
        List<OrganizationEntity> organizationEntityList = (List<OrganizationEntity>) bindingMap.get("ORGANIZATIONS");
        MailboxHelper mailboxHelper = (MailboxHelper) bindingMap.get("MAILBOX_HELPER");
        List<GroupEntity> groups = (List<GroupEntity>) bindingMap.get("GROUPS");
        Map<String, String> groupsMap = (Map<String, String>) bindingMap.get("GROUPS_MAP");
        Map<String, GroupEntity> groupsMapEntities = (Map<String, GroupEntity>) bindingMap.get("GROUPS_MAP_ENTITY");
        List<LocationEntity> locations = (List<LocationEntity>) bindingMap.get("LOCATIONS");

        boolean changeDisplayName = false;
        boolean isNewUser = (user.getId() == null);
        //DistiguishedName
        String distinguishedName = this.getValue(lo.get("distinguishedName"));
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
        if ((str == "9223372036854775807") || (str == "0")) {
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
            String[] surnameSplit = surname.trim().split(" ");
            List<String> snRes = new ArrayList<>();
            for (String str1 : surnameSplit) {
                if (str1.trim().length() > 0) {
                    snRes.add(str1.trim());
                }
            }
            String prefSn = "";
            String resSn = "";
            if (snRes.size() == 1) {
                resSn = snRes.get(0);
            } else if (snRes.size() == 2) {
                prefSn = snRes.get(0);
                resSn = snRes.get(1);
            } else if (snRes.size() == 3) {
                if (snRes.get(0).equals(snRes.get(1))) {
                    prefSn = snRes.get(0);
                    resSn = snRes.get(2);
                } else if ((snRes.get(0).length() + snRes.get(1)).length() > 9) {
                    prefSn = snRes.get(0);
                    resSn = snRes.get(1) + " " + snRes.get(2);
                } else {
                    prefSn = snRes.get(0) + " " + snRes.get(1);
                    resSn = snRes.get(2);
                }
            } else {
                resSn = surname;
            }

            user.setPrefixLastName(prefSn.length() > 10 ? prefSn.substring(0, 9) : prefSn);
            if (StringUtils.isNotBlank(resSn)) {
                user.setLastName(resSn.substring(0, 1).toUpperCase() + resSn.substring(1));
            }
        } else {
            user.setLastName(samAccountName);
        }

        //Initials
        String initials = this.getValue(lo.get("initials"));
        if (StringUtils.isBlank(initials) || "null".equalsIgnoreCase(initials)) {
            initials = "";
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
        String classification = "None";
        boolean isService = false;

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
        //serviceAccountName
        addUserAttribute(user, new UserAttributeEntity("serviceAccountName", isService ? samAccountName : null));

        //Home MDB
        String homeMDB = this.getValue(lo.get("homeMDB"));
        if (!isService) {
            boolean isNoMBX = StringUtils.isBlank(homeMDB) || PDD_EMAIL.equalsIgnoreCase(this.getValue(lo.get("mail")));
            mdTypeId = isNoMBX ? "AKZONOBEL_USER_NO_MBX" : "AKZONOBEL_USER_MBX";
        }

        String attr = this.getValue(lo.get("userPrincipalName"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("userPrincipalName", attr));
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
            addUserAttribute(user, new UserAttributeEntity("division", attr));
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

        attr = this.getValue(lo.get("extensionAttribute14"));
        boolean isMDM = "MDM".equalsIgnoreCase(attr);
        addUserAttribute(user, new UserAttributeEntity("classification", attr));

        attr = this.getValue(lo.get("company"));
        addUserAttribute(user, new UserAttributeEntity("ORG_NAME", attr));

        //Managers
        attr = this.getValue(lo.get("managers"));
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("managers", attr));
        }
        //TODO implement add supervisor to user
        //
        // Set mailbox
        try {
            String mailboxSize = mailboxHelper.getBoxSize(homeMDB);
            if (StringUtils.isNotBlank(mailboxSize) || StringUtils.isBlank(homeMDB)) {
                addUserAttribute(user, new UserAttributeEntity("mailbox", mailboxSize));
            }
        } catch (Exception e) {
            System.out.println("Problem with mailbox Definitions");
        }

        // MemberOf
        Attribute mOfAttr = lo.get("memberOf");
        String[] memberOf = null;
        if (mOfAttr != null) {
            if (mOfAttr.isMultiValued()) {
                memberOf = mOfAttr.getValueList().toArray(new String[mOfAttr.getValueList().size()]);
            } else {
                memberOf = mOfAttr.getValue() == null ? null : mOfAttr.getValue().split("/\\,/");
            }
        }
        boolean isCacheEnabled = containsNameGroup(memberOf, groupsMap, ARCHIVE_CACHE_ENABLED);
        boolean isCacheDisabled = containsNameGroup(memberOf, groupsMap, ARCHIVE_CACHE_DISABLED);
        boolean isInternet = containsMaskGroup(memberOf, groupsMap, INTERNET_GROUP_MASK);

        boolean isPDD = (mdTypeId == "AKZONOBEL_USER_NO_MBX" && PDD_EMAIL.equalsIgnoreCase(emailAddressValue));
        addUserAttribute(user, new UserAttributeEntity("internetAccess", isInternet ? "On" : null));
        addUserAttribute(user, new UserAttributeEntity("mdm", isMDM ? "On" : null));
        addUserAttribute(user, new UserAttributeEntity("activeSync", isMDM ? "Off" : null));
        addUserAttribute(user, new UserAttributeEntity("lyncMobility", isMDM ? "On" : null));
        addUserAttribute(user, new UserAttributeEntity("PDDAccount", isPDD ? "On" : null));

        if (isCacheEnabled) {
            addUserAttribute(user, new UserAttributeEntity("archive", "Cached - Laptop"));
        } else if (isCacheDisabled) {
            addUserAttribute(user, new UserAttributeEntity("archive", "Non-Cached - Desktop"));
        } else {
            addUserAttribute(user, new UserAttributeEntity("archive", null));
        }
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
        mergeGroups(memberOf, groupsMapEntities, user);

        //TODO Reveal what is and how to define option: 'longTermAbsence'
        //TODO get Citrix attributes: terminalServiceHomeD...

        // Classification
        //TODO Reveal what is and how to define Classifications: 'RAA', 'LowFrequencyUser'
        addUserAttribute(user, new UserAttributeEntity("classification", classification));

        //Manager
        addSuper(user, this.getValue(lo.get("manager")));
        //For AD
        addRoleId(user, "8a8da02e51a28fd20151a291ce360002");

        String accessRoleId = (mdTypeId == "AKZONOBEL_USER_MBX" || mdTypeId == "AKZONOBEL_USER_NO_MBX") ? "1" : mdTypeId == "AKZONOBEL_ADM_ACCOUNT" ? "SUPPORT_ADMIN_ROLE_ID" : "SERVICE_ROLE_ID";
        if (StringUtils.isNotBlank(accessRoleId)) {
            addRoleId(user, accessRoleId);
        }

        // Add HP Admin role
        boolean isHpAdmin = StringUtils.isNotBlank(distinguishedName) && distinguishedName.matches("/.*,OU=Administrators,.*OU=HP,.*/");
        if (isHpAdmin) {
            for (RoleEntity re : user.getRoles()) {
                if (re.getId() == "HP_ADMIN_ROLE_ID") {
                    addRoleId(user, "HP_ADMIN_ROLE_ID");
                }
//                if (re.getId() == UNITY_ROLE_ID) {
//                    addRoleId(user, UNITY_ROLE_ID);
//                }
            }
        }

        // Exchange
        String userPrincipalName = this.getValue(lo.get("userPrincipalName"));
        updateLoginAndRole(StringUtils.isNotBlank(homeMDB) ? userPrincipalName : null, EXCH_MNG_SYS_ID, user, "EXCHANGE_ROLE_ID");

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

        getLinkedOrganization(distinguishedName, siteCode, extensionAttribute15, organizationEntityList, locations, user);
    }

    private void updateLoginAndRole(String login, String managedSystemId, UserEntity user, String roleId) {

        LoginEntity lg = null;
        for (LoginEntity le : user.getPrincipalList()) {
            if (le.getManagedSysId() == managedSystemId) {
                lg = le;
                break;
            }
        }

        RoleEntity userRole = null;
        if (StringUtils.isNotBlank(roleId)) {
            for (RoleEntity re : user.getRoles()) {
                if (re.getId() == roleId) {
                    userRole = re;
                }
            }
        }

        if (StringUtils.isNotBlank(login)) {
            if (lg != null) {
                lg = new LoginEntity();
                lg.setManagedSysId(managedSystemId);
                lg.setCreateDate(new Date());
                lg.setStatus(LoginStatusEnum.ACTIVE);
                lg.setUserId(user.getId());
                lg.setLogin(login);
                user.getPrincipalList().add(lg);
            } else if (lg.getLogin() != login) {
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
    }

    public boolean containsNameGroup(String[] userADNames, Map<String, String> groupsMap, String toFind) {
        if (userADNames == null)
            return false;
        String dn = groupsMap.get(toFind);
        boolean retVal = false;
        if (dn != null) {
            return Arrays.asList(userADNames).contains(dn);
        }
        return retVal;
    }

    public boolean containsMaskGroup(String[] userADNames, Map<String, String> groupsMap, String mask) {
        if (userADNames == null)
            return false;
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
        ph.setPhoneNbr(phoneValue);
        addPhone(user, ph);
    }

    private void addPhone(UserEntity user, PhoneEntity phone) {
        for (PhoneEntity e : user.getPhones()) {
            if (e.getMetadataType().equals(phone.getMetadataType())) {
                e.setName(phone.getName());
                e.setIsDefault(phone.getIsDefault());
                e.setPhoneNbr(phone.getPhoneNbr());
                e.setPhoneExt(phone.getPhoneExt());
                return;
            }
        }
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

    private void getLinkedOrganization(String distinguishedName, String site, String bu, List<OrganizationEntity> orgs, List<LocationEntity> locations, UserEntity user) {
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
                    addUserAttribute(user, new UserAttributeEntity("COUNTRY", l.getCountry()));
                }
            }
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