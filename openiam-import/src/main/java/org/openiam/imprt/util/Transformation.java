package org.openiam.imprt.util;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.imprt.ImportProcessor;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.jdbc.parser.impl.MetadataTypeEntityParser;
import org.openiam.imprt.model.Attribute;
import org.openiam.imprt.model.LineObject;
import org.openiam.util.StringUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Transformation {

    private final String IDM_MNG_SYS_ID = "0";
    private final String AD_MNG_SYS_ID = "DD6CA4CC8BBC4D78A5879D93CEBC8A29";
    private final String PDD_EMAIL = "PDDUser@cog.akzonobel.com";

    final String[] ARCHIVE_CACHE_ENABLED = {"g_gss_eus_maas_vaultcache_enabled - vv"};
    final String[] ARCHIVE_CACHE_DISABLED = {"g_gss_eus_maas_vaultcache_disabled - vv"};
    final String INTERNET_GROUP_MASK = "a_.*_internetaccess";

    final String UNITY_ROLE_ID = "2c94b2574be50e06014be569449302ed";
    final String LYNC_MNG_SYS_ID = "2c94b25748eaf9ef01492d5507100273";
    final String EXCH_MNG_SYS_ID = "2c94b25748eaf9ef01492d5312d3026d";

    final String META_DATA_TYPE_ID_FOR_SERVICE_TYPE_ROLE = "2c94b2574bc5f9d0014bdf2976ac0a70";
    final String META_DATA_TYPE_ID_FOR_BUSINESS_UNIT_ROLE = "2c94b2574bc5f9d0014bdf24d9dc0a61";
    final String META_DATA_TYPE_ID_FOR_ORGANIZATIONAL_ROLE = "2c94b2574a7c3454014a9608add231df";

    final String csvMailboxes = "/home/OpenIAM/data/openiam/upload/sync/AN_Exchange_DBs.csv"

    final String badRoleId = "2";

    public int execute(LineObject rowObj, UserEntity user) {
        try {
            populateObject(rowObj, user);
        } catch (Exception ex) {
            return -1;
        }
        user.setStatus(UserStatusEnum.ACTIVE);

        return 0;
    }

    public void populateObject(LineObject lo, UserEntity user) throws Exception {
        boolean changeDisplayName = false;
        boolean isNewUser = true;

        final GroupDataWebService groupService = context.getBean(GroupDataWebService.class);
        final MailboxHelper mailboxHelper = new MailboxHelper(StringUtils.isNotBlank(csvMailboxes) ? SkipUTF8BOM(csvMailboxes) : null);

        String distinguishedName = lo.get("distinguishedName").getValue();
        String samAccountName = lo.get("sAMAccountName").getValue();

        String emplId = lo.get("employeeNumber").getValue();
        if (!emplId.contains("@")) {
            while (emplId.length() < 8) emplId = "0" + emplId;
        }
        user.setEmployeeId(emplId);

        if (changeDisplayName || isNewUser) {
            String fn = lo.get("givenName").getValue();
            if (fn != null && fn.length() > 0) {
                user.setFirstName(fn.substring(0,1).toUpperCase() + fn.substring(1));
            } else {
                user.setFirstName("Admin");
            }
            String surname = lo.get("sn").getValue();
            if (StringUtils.isNotBlank(surname)) {
                String[] surnameSplit = surname.trim().split(" ");
                List<String> snRes = new ArrayList<String>();
                for (String str : surnameSplit) {
                    if (str.trim().length() > 0) {
                        snRes.add(str.trim());
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
                    user.setLastName(resSn.substring(0,1).toUpperCase() + resSn.substring(1));
                }
            } else {
                user.setLastName(samAccountName);
            }

            String initials = lo.get("initials").getValue();
            if (StringUtils.isBlank(initials) || "null".equalsIgnoreCase(initials)) {
                initials = "";
            }
            user.setMiddleInit(initials);
            addUserAttribute(user, new UserAttributeEntity("initials", initials));

            String displayName = lo.get("displayName").getValue();
            if (StringUtils.isBlank(displayName) || "null".equalsIgnoreCase(displayName)) {

                displayName = genName(user.getLastName(), user.getFirstName(), user.getMiddleInit(), user.getPrefixLastName());
            }
            user.setNickname(displayName);

            if (StringUtils.isBlank(displayName)) {
                addUserAttribute(user, new UserAttributeEntity("displayName", displayName));
            }
        }

        if (isNewUser) {
            user.setId(null);
        }

        user.setTitle(lo.get("title").getValue());
        addUserAttribute(user, new UserAttributeEntity("extensionAttribute15", lo.get("extensionAttribute15").getValue()));
        String serviceTypeAttr = lo.get("extensionAttribute2").getValue();
        if (StringUtils.isBlank(serviceTypeAttr)) {
            addUserAttribute(user, new UserAttributeEntity("serviceType", serviceTypeAttr));
        }
        String samAccountNameLowerCase = samAccountName;

        DateFormat formatter;
        Date verDateMax;
        Date expDate;
        formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        verDateMax = formatter.parse("01/01/2020 12:00:00");
        Date dateBase = formatter.parse("01/01/1601 00:00:00");

        String str = lo.get("accountExpires").getValue();

        if ((str == "9223372036854775807") || (str == "0")) {
            expDate = null;
        } else {
            try {
                Long tmpD = Long.valueOf(str) / 864000000000;
                int days = tmpD.intValue();
                expDate = dateBase + days;
            } catch (Exception exp1)
            {
            }

        }

        // ******************* ADD / MODIFY LOGIN **********************
        Integer flOp = 0;
        Integer flAd = 0;
        try {

            List<LoginEntity> logList = user.getPrincipalList();
            for (LoginEntity lg : logList) {
                if (lg.getManagedSysId() == IDM_MNG_SYS_ID) {
                    flOp = 1;
                    lg.setLogin(samAccountNameLowerCase);
                    lg.setPwdExp(expDate);
                }
                if (lg.getManagedSysId() == AD_MNG_SYS_ID) {
                    flAd = 1;
                    lg.setLogin(samAccountNameLowerCase);
                    lg.setPwdExp(expDate);
                }
            }
            if (flOp > 0) {
                LoginEntity lg = new LoginEntity();
                lg.setManagedSysId(IDM_MNG_SYS_ID);
                lg.setLogin(samAccountNameLowerCase);

                String pswd = "Password$51";
                lg.setPassword(pswd);
                lg.setPwdExp(expDate);
                logList.add(lg);
                user.setDefaultLogin(samAccountNameLowerCase);
                user.setStatus(UserStatusEnum.ACTIVE);
            }
            if (flAd > 0) {
                LoginEntity lg = new LoginEntity();
                lg.setManagedSysId(AD_MNG_SYS_ID);
                lg.setLogin(samAccountNameLowerCase);

                String pswd = "Password$51";
                lg.setPassword(pswd);
                lg.setPwdExp(expDate);
                logList.add(lg);
            }
            user.setPrincipalList(logList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String str2 = lo.get("whenCreated").getValue();
            DateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmmss");
            String[] str3 = str2.split(".0Z");
            Date startDate = formatter2.parse(str3[0]);
            user.setStartDate(startDate);
        } catch (Exception e) {

        }

        String mdTypeId = null;
        String classification = "None";

        boolean isService = false;

        if (samAccountNameLowerCase.length() > 4) {

            switch (samAccountNameLowerCase.substring(0, 4)) {
                case "adm_":
                    mdTypeId = "AKZONOBEL_ADM_ACCOUNT";
                    classification = "ADM";
                    break
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

        addUserAttribute(user, new UserAttributeEntity("serviceAccountName", isService ? samAccountName : null));

        String homeMDB = lo.get("homeMDB").getValue();
        if (!isService) {
            boolean isNoMBX = StringUtils.isBlank(homeMDB) || PDD_EMAIL.equalsIgnoreCase(lo.get("mail").getValue());
            mdTypeId = isNoMBX ? "AKZONOBEL_USER_NO_MBX" : "AKZONOBEL_USER_MBX";
        }

        if (StringUtils.isNotBlank(distinguishedName)) {
            addUserAttribute(user, new UserAttributeEntity("distinguishedName", distinguishedName));
        }

        String attr = lo.get("userPrincipalName").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("userPrincipalName", attr));
        }

        if (StringUtils.isNotBlank(samAccountName)) {
            addUserAttribute(user, new UserAttributeEntity("sAMAccountName", samAccountName));
        }

        attr = lo.get("employeeID").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("localEmployeeNumber", attr));
        }

        attr = lo.get("employeeType").getValue();
        if (StringUtils.isNotBlank(attr)) {
            if (attr.contains("Long Term Absence")) {
                addUserAttribute(user, new UserAttributeEntity("employeeType", "Employee"));
                addUserAttribute(user, new UserAttributeEntity("longTermAbsence", "On"));
            } else {
                addUserAttribute(user, new UserAttributeEntity("employeeType", attr));
                addUserAttribute(user, new UserAttributeEntity("longTermAbsence", "Off"));
            }
        }

        attr = lo.get("otherName").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("otherName", attr));
        }

        attr = lo.get("division").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("division", attr));
        }

        attr = lo.get("department").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("department", attr));
        }

        attr = lo.get("description").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("description", attr));
        }

        attr = lo.get("info").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("notes", attr));
        }

        attr = lo.get("givenName").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("givenName", attr));
        }

        attr = lo.get("sn").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("sn", attr));
        }

        if (expDate != null) {
            addUserAttribute(user, new UserAttributeEntity("accountExpires", formatter.format(expDate)));
        } else {
            addUserAttribute(user, new UserAttributeEntity("accountExpires", null));
        }

        attr = lo.get("l").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("l", attr));
        }
        attr = lo.get("co").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("Country", attr));
        }
        attr = lo.get("postalCode").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("PostalCode", attr));
        }
        attr = lo.get("st").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("st", attr));
        }
        attr = lo.get("streetAddress").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("StreetAddress", attr));
        }
        attr = lo.get("office").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("Office", attr));
        }
        attr = lo.get("mail").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("EmailAddress", attr));
        }
        attr = lo.get("telephoneNumber").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("OfficePhone", attr));
        }
        attr = lo.get("mobile").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("MobilePhone", attr));
        }
        attr = lo.get("fax").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("Fax", attr));
        }
        attr = lo.get("homeDirectory").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("homeDirectory", attr));
        }
        attr = lo.get("homeDrive").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("homeDrive", attr));
        }

        String guid = lo.get("objectGUID").getValue();

        if (StringUtils.isNotBlank(guid)) {
            addUserAttribute(user, new UserAttributeEntity("objectGUID", guid));
        }

        attr = lo.get("extensionAttribute1").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("extensionAttribute1", attr));
        }

        attr = lo.get("extensionAttribute3").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("discipline", attr));
        }
        attr = lo.get("extensionAttribute3").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("extensionAttribute3", attr));
        }
        attr = lo.get("extensionAttribute8").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("extensionAttribute8", attr));
        }
        attr = lo.get("extensionAttribute9").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("extensionAttribute9", attr));
        }
        attr = lo.get("extensionAttribute14").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("classification", attr));
        }
        attr = lo.get("company").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("Company", attr));
        }

        attr = lo.get("managers").getValue();
        if (StringUtils.isNotBlank(attr)) {
            addUserAttribute(user, new UserAttributeEntity("managers", attr));
        }

        // Set mailbox
        String mailboxSize = mailboxHelper.getBoxSize(homeMDB);
        if (StringUtils.isNotBlank(mailboxSize) || StringUtils.isBlank(homeMDB) {
            addUserAttribute(user, new UserAttributeEntity("mailbox", mailboxSize));
        }

        // Add Address
        try {
            AddressEntity adr = new AddressEntity();
            adr.setName("CurrentAddress");
            adr.setCountry(lo.get("co").getValue());
            adr.setCity(lo.get("l").getValue());
            adr.setAddress1(lo.get("streetAddress").getValue());
            adr.setAddress2(lo.get("office").getValue());
            adr.setState(lo.get("st").getValue());
            adr.setPostalCd(lo.get("postalCode").getValue();

            adr.setMetadataType(new MetadataTypeEntityParser().getById("OFFICE_ADDRESS"));
            addUserAddress(user, adr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String emailAddressValue = lo.get("mail").getValue();

        // Email
        try {
            EmailAddressEntity email = new EmailAddressEntity();
            if (StringUtils.isNotBlank(emailAddressValue)) {
                email.setName("PRIMARY_EMAIL");
                email.setMetadataType(new MetadataTypeEntityParser().getById("PRIMARY_EMAIL"));
                email.setIsDefault(true);
                email.setIsActive(true);
                email.setEmailAddress(emailAddressValue);
                addUserEmail(user, email);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (!isNewUser) {
            for (EmailAddressEntity e : user.getEmailAddresses()) {
                if (e.getMetadataType().getId().equalsIgnoreCase("SECONDARY_EMAIL") && (e.getEmailAddress().equalsIgnoreCase(emailAddressValue))) {
                    user.getEmailAddresses().remove(e);
                    return;
                }
            }
        }

        String ph = lo.get("telephoneNumber").getValue();
        if (StringUtils.isNotBlank(ph)) {
            try {
                addPhone(ph, new MetadataTypeEntityParser().getById("OFFICE_PHONE"), user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        ph = lo.get("mobile").getValue();
        if (StringUtils.isNotBlank(ph)) {
            try {
                addPhone(ph, new MetadataTypeEntityParser().getById("CELL_PHONE"), user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ph = lo.get("fax").getValue();
        if (StringUtils.isNotBlank(ph)) {
            try {
                addPhone(ph, new MetadataTypeEntityParser().getById("FAX"), user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Add Company
        String extensionAttr15 = lo.get("extensionAttribute15").getValue();
        if (StringUtils.isNotBlank(extensionAttr15)) {
            addUserOrganization(user, extensionAttr15, null, "ORGANIZATION", null);
        }

        // MemberOf
        String[] memberOf = lo.get("memberOf").getValue().split("/\\,/");

        GroupHelper groupHelper = null;
        if (memberOf != null) {
            groupHelper = new GroupHelper(groupService, memberOf);
            //  groupHelper.mergeGroups(user.groups)
        } else {
            groupHelper = new GroupHelper(groupService, "");
        }

//        println("groupNames=" + groupHelper.getGroupNames())

        boolean isMDM =false;// groupHelper.containsName(MDM_GROUP_NAMES)
        boolean isCacheEnabled = groupHelper.containsName(ARCHIVE_CACHE_ENABLED);
        boolean isCacheDisabled = groupHelper.containsName(ARCHIVE_CACHE_DISABLED);
        boolean isInternet = groupHelper.containsMask(INTERNET_GROUP_MASK);
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

        //TODO Reveal what is and how to define option: 'longTermAbsence'
        //TODO get Citrix attributes: terminalServiceHomeD...

        // Classification
        //TODO Reveal what is and how to define Classifications: 'RAA', 'LowFrequencyUser'
        addUserAttribute(user, new UserAttributeEntity("classification", classification));

        // Managers
//        println("managers:" + lo.get("manager")));

        addSuperByLogin(user, lo.get("manager").getValue());

//        println("nickname : " + user.nickname);

        // Add org role
        String siteCode = lo.get("extensionAttribute9").getValue();
        String bu = lo.get("extensionAttribute15").getValue();
        String serviceType = lo.get("extensionAttribute2").getValue();
        RoleEntity roleTech = extractOrgRoleName(distinguishedName, siteCode, bu, serviceType, user);
        RoleDataWebService roleManager = context.getBean("roleWS");

        List<RoleEntity> oldRoles = null;
        if (user.getId() != null){
            oldRoles = roleManager.getRolesForUser(user.getId(), null, false, -1, -1);
        }

        if (roleTech != null) {
            List<String> listMeta = new ArrayList<String>();
            listMeta.add(META_DATA_TYPE_ID_FOR_ORGANIZATIONAL_ROLE);
            listMeta.add(META_DATA_TYPE_ID_FOR_SERVICE_TYPE_ROLE);
            listMeta.add(META_DATA_TYPE_ID_FOR_BUSINESS_UNIT_ROLE);
            fillRoles(oldRoles, roleTech, user, listMeta);

        } else {
            addRoleId(user, badRoleId);
        }

        String accessRoleId = (mdTypeId == "AKZONOBEL_USER_MBX" || mdTypeId == "AKZONOBEL_USER_NO_MBX")? "1" : mdTypeId == "AKZONOBEL_ADM_ACCOUNT" ? "SUPPORT_ADMIN_ROLE_ID" : "SERVICE_ROLE_ID";
        if (StringUtils.isNotBlank(accessRoleId)) {
            addRoleId(user, accessRoleId);
        }

        // Add HP Admin role
        boolean isHpAdmin = StringUtils.isNotBlank(distinguishedName) && distinguishedName.matches("/.*,OU=Administrators,.*OU=HP,.*/");
        if (isHpAdmin) {
            for (RoleEntity re : user.getRoles()){
                if (re.getId() == "HP_ADMIN_ROLE_ID") {
                    addRoleId(user, "HP_ADMIN_ROLE_ID");
                }
                if (re.getId() == UNITY_ROLE_ID) {
                    addRoleId(user, UNITY_ROLE_ID);
                }
            }
        }

        // Exchange
        String userPrincipalName = lo.get("userPrincipalName").getValue();
        updateLoginAndRole(StringUtils.isNotBlank(homeMDB) ? userPrincipalName : null, EXCH_MNG_SYS_ID, user, "EXCHANGE_ROLE_ID");

        // lync
        String sipAddress = lo.get("msRTCSIP-PrimaryUserAddress"));
        sipAddress = StringUtils.isNotBlank(sipAddress) && sipAddress.startsWith("sip:") ? sipAddress.substring(4) : null;
        updateLoginAndRole(sipAddress, LYNC_MNG_SYS_ID, user, "LYNC_ROLE_ID")
        addUserAttribute(user, new UserAttributeEntity("lync", StringUtils.isNotBlank(sipAddress) ? "On" : null));

        //Update mdType for user
        if (StringUtils.isNotBlank(mdTypeId)) {
            user.setType(new MetadataTypeEntityParser().getById(mdTypeId));
        }

    }



    private void addUserAttribute (UserEntity user, UserAttributeEntity attr) {
        if (user.getUserAttributes().get(attr.getName()) != null) {
            user.updateUserAttribute(attr);
        } else {
            user.addUserAttribute(attr);
        }
    }

    private void addUserAddress (UserEntity user, AddressEntity addr) {
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

    private void addUserEmail (UserEntity user, EmailAddressEntity email) {
        for (EmailAddressEntity ee : user.getEmailAddresses()) {
            if (ee.getMetadataType().equals(email.getMetadataType())) {
                ee.setName(email.getName());
                ee.setEmailAddress(email.getEmailAddress());
                ee.setIsDefault(email.getIsDefault());
                ee.setIsActive(email.getIsActive());
            }
        }
        user.getEmailAddresses().add(email);
    }

    private void addPhone(String phoneValue, MetadataTypeEntity metatype, UserEntity user) {
            PhoneEntity ph = new PhoneEntity();
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

    private void updateLoginAndRole(String login, String managedSystemId, UserEntity user, String roleId) {

        LoginEntity lg = null;
        for (LoginEntity le : user.getPrincipalList()) {
            if (le.getManagedSysId() == managedSystemId) {
                lg = le;
                break;
            }
        }

        RoleEntity userRole = null;
        if (StringUtils.isNotBlank(roleId)){
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
                user.getPrincipalList().remove(lg);
            }
            if (userRole != null) {
                user.getRoles().remove(userRole);
            }
        }
    }

    // Skip UTF8_BOM
    private InputStream SkipUTF8BOM(String inputFile) {
        InputStream input = null;
        try {
            input = new FileInputStream(inputFile);
            final byte[] UTF8_BOM = {(byte)0xEF, (byte)0xBB, (byte)0xBF};
            byte [] head = new byte[3];
            int read = input.read(head, 0, 3);
            if (read != 3 || !Arrays.equals(head, UTF8_BOM)) {
                input.close();
                input = new FileInputStream(inputFile);
            }
        } catch (IOException e) {

        }
        return input;

    }

    private void addUserOrganization(UserEntity user, String BU, String parentOrgName, String orgType, String parentOrgType) {
        //try to find in OpenIAM and check on exists
        if (StringUtils.isBlank(BU)) {
            return;
        }
        try {
            OrganizationSearchBean searchBean = new OrganizationSearchBean();
            searchBean.setDeepCopy(false);
            searchBean.setInternalOrgId(BU);
            searchBean.setOrganizationTypeId(orgType);
            searchBean.setFindInCache(true);
            List<OrganizationEntity> orgList = orgService.findBeans(searchBean, null, -1, -1);
            boolean exists = orgList != null && orgList.size() > 0

            String newOrgId = null;
            if (exists) {
                def foundOrgUserDTO = user.organizationUserDTOs.find { o -> o.organization.internalOrgId == BU && o.organization.organizationTypeId == orgType }
                def foundOrg = foundOrgUserDTO.organization
                if (!foundOrg) {
                    def org = null;

                    if (orgList != null && orgList.size() > 0) {
                        org = orgList.get(0);
                    }
                    if (org) {
                        org.operation = AttributeOperationEnum.ADD
                        user.getOrganizationUserDTOs().add(new OrganizationUserDTO(pUser.getId(), org.getId(), "DEFAULT_AFFILIATION", AttributeOperationEnum.ADD));
                    }
                }

                if (newOrgId == null) {
                    newOrgId = orgList.get(0).getId();
                }

                if (parentOrgType != null && parentOrgName != null) {
                    // Add parent by parent org internal Id
                    OrganizationSearchBean searchParentBeanP = new OrganizationSearchBean();
                    searchParentBeanP.setDeepCopy(false);
                    searchParentBeanP.setName(parentOrgName);
                    searchParentBeanP.setOrganizationTypeId(parentOrgType);
                    List<Organization> orgParents = orgService.findBeans(searchParentBeanP, null, -1, -1);
                    Organization foundParentOrg;
                    if (orgParents != null && orgParents.size() > 0 && newOrgId != null) {
                        foundParentOrg = orgParents.get(0)
                        orgService.addChildOrganization(foundParentOrg.id, newOrgId)

                    } else {

                    }
                }
            }
        } catch (Exception e) {
            println(e);
        }
    }

    private void addRole(UserEntity user, RoleEntity r) {
        def foundRole = pUser.roles.find { it.id == r.id }
        if (pUser.id) {
            def roleDataService = context?.getBean("roleDataService") as RoleDataService
            List<Role> roles = roleDataService.getRolesDtoForUser(pUser.id, null, -1, -1);
            if (roles) {
                for (Role role : roles) {
                    if (!role.id.equals(r.id) && (ORG_ROLE_TYPE_ID.equals(role.mdTypeId) || BU_ROLE_TYPE_ID.equals(role.mdTypeId) || ST_ROLE_TYPE_ID.equals(role.mdTypeId))) {
                        role.operation = AttributeOperationEnum.DELETE;
                        pUser.roles.add(role);
                    }
                }
            }
        }
        if (!foundRole) {
            r.operation = AttributeOperationEnum.ADD;
            pUser.roles.add(r);
        }
    }

    private void addRole(UserEntity user, String roleName) {
        def roleDataService = context?.getBean("roleDataService") as RoleDataService
        if (roleName.equals("FAILED_SYNC")) {
            def failRole = roleDataService?.getRoleDtoByName("FAILED_SYNC", null,false)
            pUser.roles.add(failRole)
//            addAttribute(pUser, new Attribute("ENTITLE_ROLE_FAILED_REASON", "NO SUCH ROLE WITH NAME=" + roleName));
        } else {
            def foundRole = pUser.roles.find { r -> r.name == roleName }
//        println("foundRole" + foundRole);
            if (!foundRole) {
                def role = roleDataService?.getRoleDtoByName(roleName, null,false)
                println("role" + role);
                if (role) {
                    role.operation = AttributeOperationEnum.ADD
                    pUser.roles.add(role)
                } else {
                    def failRole = roleDataService?.getRoleDtoByName("FAILED_SYNC", null,false)
                    pUser.roles.add(failRole)
                    addAttribute(pUser, new Attribute("ENTITLE_ROLE_FAILED_REASON", "NO SUCH ROLE WITH NAME=" + roleName));
                }
            }
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
            RoleDataService roleDataService = context?.getBean("roleDataService");
            RoleEntity role = roleDataService.getRole(roleId);
            if (role != null) {
                user.getRoles().add(role);
            }
        }
    }

    private void removeRoleId(UserEntity user, String roleId) {
        for (RoleEntity re : user.getRoles()) {
            if (re.getId().equalsIgnoreCase(roleId)) {
                user.getRoles().remove(re);
                break;
            }
        }
    }

    private void addSuperByLogin(UserEntity user, String login) {
        if (StringUtils.isNotBlank(login)) {
            return;
        }
        List<SearchAttribute> sal = new ArrayList<SearchAttribute>();
        sal.add(new SearchAttribute("distinguishedName", login));
        UserSearchBean usb = new UserSearchBean();
        usb.setDeepCopy(false);
        usb.setAttributeList(sal);

        List<User> ul = userWS.findBeans(usb, -1, -1);


        if (ul && (ul.size() > 0)) {
            User u = ul?.get(0);
            if (u) {
                u.operation = AttributeOperationEnum.ADD;
                pUser.superiors.add(u);
            }
        } else {
            println(">>>>>> DID NOT FIND USER :" + login);
        }
    }


    private RoleEntity extractOrgRoleName(String distinguishedName, String site, String bu, String serviceType, UserEntity user) {
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

        if (StringUtils.isNotBlank(adPath)) {
            //// NO AD PATH!

            addUserAttribute(user, new UserAttributeEntity("FAIL_ROLE_EXTRACT_PATH_FROM_DN", distinguishedName));
            return null;
        }
        RoleDataService roleDataService = context.getBean("roleDataService");
        List<RoleEntity> roles = roleDataService.findRolesDtoByAttributeValue("AD_PATH", adPath, false);
        if (roles == null || roles.size() == 0) {
            addUserAttribute(user, new UserAttributeEntity("FAIL_ROLE_NO_ROLE_FOR_PATH", adPath));
            return null;
        }
        if (roles != null) {
            if (roles.size() == 1) {
                return roles.get(0);
            } else {
                String orgName = "${site}_${bu}";
                List<RoleEntity> rolesFind = new ArrayList<RoleEntity>();
                for (RoleEntity re : roles) {
                    if (re.getName().toLowerCase().contains(orgName.toLowerCase())) {
                        rolesFind.add(re);
                    }
                }
                roles = new ArrayList<>(rolesFind);

                if (roles == null || roles.size() == 0) {
                    addUserAttribute(user, new UserAttributeEntity("FAIL_ROLE_NO_ROLE_EXTRA", "AD Path was ${adPath} and were returned a lot of roles, but based on ${orgName} we didn't find a match"));
                    return null;
                } else if (roles.size() == 1) {
                    return roles.get(0);
                } else {
                    for (RoleEntity re : roles) {
                        if (re.getName().equalsIgnoreCase("${orgName}_${serviceType}")) {
                            return re;
                        }
                    }
                    addUserAttribute(user, new UserAttributeEntity("FAIL_ROLE_NO_ROLE_EXTRA", "AD Path was ${adPath} and were returned a lot of roles by ${orgName}, but based on ${orgName}_${serviceType} we didn't find a match"));
                }
            }
        }
        if (roles != null && roles.size() > 0) {
            return roles.get(0);
        }
        return null;
    }


    public List<RoleEntity> getRoleFromADPath(String adPath, Map<String, RoleEntity> rolesMap) {
        List<RoleEntity> retVal = new ArrayList<RoleEntity>();
        for (String a : rolesMap.keySet()) {
            RoleEntity r = rolesMap.get(a);
            if (r != null) {
                for (RoleAttributeEntity ra : r.getRoleAttributes()) {
                    if ("AD_PATH".equalsIgnoreCase(ra.getName()) && adPath.equalsIgnoreCase(ra.getValue())) {
                        retVal.add(r);
                        break;
                    }
                }
            }
        }
        return retVal;
    }

    private void fillRoles(List<RoleEntity> roleList, RoleEntity curRole, UserEntity user, List<String> types) {
        if (roleList != null) {
            user.setRoles(new HashSet<RoleEntity>(roleList));
        }
        boolean alreadyThere = false;
        for (RoleEntity r : roleList) {
            if (r.getId().equals(curRole.getId())) {
                alreadyThere = true;
                continue;
            }
            if (types.contains(r.getType().getId())) {
                r..operation = AttributeOperationEnum.DELETE;

            }
        }
        if (!alreadyThere) {
            user.getRoles().add(curRole);
        }
    }
}




class GroupHelper {

    private List<GroupEntity> groupList = null;
    private Set<String> groupCNs = null;
    private GroupDataWebService groupService;
    private String[] groupDNs;

    public GroupHelper(GroupDataWebService groupService, String[] groupsAttribute) {
        this.groupService = groupService
        this.groupDNs = groupsAttribute;

    }

    public List<GroupEntity> getGroupEntities() {

        if (groupList == null) {
            groupList = loadEntitiesByDNs(groupDNs);
        }
        return groupList
    }

    public void mergeGroups(Set<Group> userGroups) {

        List<Group> groupsToRemove = []
        Set<String> DNToAdd = new ArrayList<>(groupDNs.toList())

        userGroups.each { gr ->
                String distinguishedName = gr.attributes.find({ at -> at.name == "DistinguishedName" })?.value
            if (distinguishedName) {
                if (!DNToAdd.remove(distinguishedName)) {
                    groupsToRemove += gr // If DNToAdd does not contain distinguishedName
                }
            }
        }

        userGroups.removeAll(groupsToRemove)

        List<Group> addList = loadEntitiesByDNs(DNToAdd.toArray() as String[])
        addList.each { gr ->
                gr.operation = AttributeOperationEnum.ADD;

        }
        userGroups.addAll(addList)
    }

    private List<Group> loadEntitiesByDNs(String[] DNs) {
        def list = [] as List<Group>
                DNs?.each { dn ->
                List<Group> groups = groupService.findGroupsByAttributeValue("DistinguishedName", dn);
            if (groups) {
                list += groups.get(0)
            }
        }
        return list
    }

    private Set<String> getGroupNames() {
        if (groupCNs == null) {
            groupCNs = new HashSet<>(groupDNs?.length ?: 0)
            groupDNs?.each { dn ->
                if (dn.startsWith("CN=")) {
                    String[] parts = dn.split(/(?<!\\),/, 2)
                    groupCNs += parts[0].substring(3).toLowerCase()
                }
            }
        }
        return groupCNs
    }

    public boolean containsName(String[] names) {
        return names.find { name -> groupNames.contains(name) }
    }

    public boolean containsMask(String mask) {
        return groupNames.find { cn ->
                cn.matches(mask)
        }
    }
}

class MailboxHelper {

    private HashMap<String, HashSet<String>> sortedSets = {
            "Small"  : [:] as HashSet<String>,
            "Medium" : [:] as HashSet<String>,
            "Regular": [:] as HashSet<String>,
            "Large"  : [:] as HashSet<String>}

    public MailboxHelper(InputStream is) {

        String activeSet = null
        String tagContent = null

        if (is) {
            final CSVHelper parser = new CSVHelper(is, "UTF-8", CSVStrategy.EXCEL_STRATEGY)

            final String[][] rows = parser.getAllValues()

            for (int i = 1; i < rows?.length; ++i) {
                final String[] row = rows[i]
                if (row?.length == 2) {
                    String size = "Regular"
                    if (row[1].startsWith("250 MB")) size = "Small"
                    else if (row[1].startsWith("1 GB")) size = "Medium"
                    else if (row[1].startsWith("10.01 GB")) size = "Large"
                    sortedSets.get(size).add(row[0])
                }
            }
        }
    }

    public String getBoxSize(String mailServerDN) {
        if (mailServerDN) {
            def matcher = (mailServerDN =~ /CN=(\w+),.*/)
            String serverName = matcher ? matcher[0][1] : null
            if (serverName) {
                return sortedSets.keySet().find { key ->
                        sortedSets.get(key).contains(serverName)
                }
            }
        }
        return null
    }
}