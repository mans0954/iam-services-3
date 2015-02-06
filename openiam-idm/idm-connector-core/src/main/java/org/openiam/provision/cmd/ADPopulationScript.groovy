package org.openiam.provision.cmd

import org.openiam.base.AttributeOperationEnum
import org.openiam.idm.srvc.auth.dto.Login
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService
import org.openiam.idm.srvc.res.dto.Resource
import org.openiam.idm.srvc.res.service.ResourceDataService
import org.openiam.provision.dto.ProvisionUser
import org.openiam.idm.srvc.user.dto.UserStatusEnum
import org.openiam.idm.srvc.user.dto.UserAttribute

public class ADPopulationScript extends org.openiam.idm.srvc.recon.service.AbstractPopulationScript<ProvisionUser> {
    public int execute(Map<String, String> line, ProvisionUser pUser) {
        int retval = 1;
        for (String key : line.keySet()) {
            switch (key) {
                case "sAMAccountName":
                    addAttribute(pUser, "sAMAccountName", line.get("sAMAccountName"));
                    break
                case "cn":
                    addAttribute(pUser, "cn", line.get("cn"));
                    break
                case "mail":
                    if (pUser.email != line.get("mail")) {
                        pUser.email = line.get("mail")
                        retval = 0
                    }
                    addAttribute(pUser, "mail", line.get("mail"));
                    break
                case "o":
                    // fixed value for this LDAP managed sys
                    break
                case "ou":
                    // fixed value for this LDAP managed sys
                    addAttribute(pUser, "ou", line.get("ou"));
                    break
                case "employeeID":
                    if (pUser.employeeId != line.get("employeeID")) {
                        pUser.employeeId = line.get("employeeID")
                        retval = 0
                    }
                    addAttribute(pUser, "employeeID", line.get("employeeID"));
                    break
                case "employeeType":
                    if (pUser.employeeType != line.get("employeeType")) {
                        pUser.employeeType = line.get("employeeType")
                        retval = 0
                    }
                    addAttribute(pUser, "employeeType", line.get("employeeType"));
                    break
                case "postalCode":
                    addAttribute(pUser, "postalCode", line.get("postalCode"));
                    break
                case "sn":
                    if (pUser.lastName != line.get("sn")) {
                        pUser.lastName = line.get("sn")
                        retval = 0
                    }
                    addAttribute(pUser, "sn", line.get("sn"));
                    break
                case "st":
                    addAttribute(pUser, "st", line.get("st"));
                    break
                case "userPassword":
                    // not supported yet
                    break
                case "streetAddress":
                    addAttribute(pUser, "streetAddress", line.get("streetAddress"));
                    break
                case "displayName":
                    String[] parts = line.get("displayName").split(",")
                    if (parts.length == 2) {
                        if (pUser.firstName != parts[1]) {
                            pUser.firstName = parts[1]
                            retval = 0
                        }
                        if (pUser.lastName != parts[0]) {
                            pUser.lastName = parts[0]
                            retval = 0
                        }
                    }
                    addAttribute(pUser, "displayName", line.get("displayName"));
                    break

                case "title":
                    if (pUser.title != line.get("title")) {
                        pUser.title = line.get("title")
                        retval = 0
                    }
                    addAttribute(pUser, "title", line.get("title"));
                    break
                case "givenName":
                    if (pUser.firstName != line.get("givenName")) {
                        pUser.firstName = line.get("givenName")
                        retval = 0
                    }
                    addAttribute(pUser, "givenName", line.get("givenName"));
                    break
                case "userPrincipalName":
                    if (pUser.getPrimaryPrincipal("0") != null) {
                        pUser.getPrimaryPrincipal("0").setLogin(line.get("userPrincipalName"))
                        pUser.getPrimaryPrincipal("0").setOperation(AttributeOperationEnum.ADD);
                    } else {
                        Login l = new Login();
                        l.setOperation(AttributeOperationEnum.ADD);
                        l.setManagedSysId("0");
                        l.setLogin(line.get("userPrincipalName"));
                        pUser.getPrincipalList().add(l);
                    }
                    addAttribute(pUser, "userPrincipalName", line.get("userPrincipalName"));
                    break
                case "distinguishedName":
                    println("HOW COW HOW COW HOW COW HOW COW HOW COW HOW COW HOW COW HOW COW HOW COW HOW COW")
                    if (pUser.getPrimaryPrincipal("110") != null) {
                        pUser.getPrimaryPrincipal("110").setLogin(line.get("distinguishedName"))
                        pUser.getPrimaryPrincipal("110").setOperation(AttributeOperationEnum.ADD);
                    } else {
                        Login l = new Login();
                        l.setOperation(AttributeOperationEnum.ADD);
                        l.setManagedSysId("0");
                        l.setLogin(line.get("distinguishedName"));
                        pUser.getPrincipalList().add(l);
                    }
                    addAttribute(pUser, "distinguishedName", line.get("distinguishedName"));
                    break
            }
        }

        ManagedSystemWebService systemWebService = context.getBean("managedSysService");
        ResourceDataService resourceDataService = context.getBean("resourceDataService");
        ManagedSysDto currentManagedSys = systemWebService.getManagedSys(managedSysId);
        Resource currentResource = resourceDataService.getResource(currentManagedSys.getResourceId(), null);
        currentResource.setOperation(AttributeOperationEnum.ADD);
        pUser.getResources().add(currentResource);
        //set status to active: IMPORTANT!!!!
        pUser.setStatus(UserStatusEnum.PENDING_INITIAL_LOGIN);
        pUser.setMdTypeId("Contractor");
        return retval;
    }

    def addAttribute(ProvisionUser pUser, String attributeName, String attributeValue) {
        def userAttr = new UserAttribute(attributeName, attributeValue)
        if (!pUser.userAttributes.containsKey(attributeName)) {
            userAttr.operation = AttributeOperationEnum.ADD
        } else {
            if (userAttr.value != attributeValue) {
                userAttr.operation = AttributeOperationEnum.REPLACE
            }
        }
        pUser.userAttributes.put(attributeName, userAttr)
    }
}