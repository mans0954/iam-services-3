import org.openiam.base.AttributeOperationEnum
import org.openiam.idm.srvc.continfo.dto.Address
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService
import org.openiam.idm.srvc.res.service.ResourceDataService
import org.openiam.provision.dto.ProvisionUser
import org.openiam.idm.srvc.user.dto.UserStatusEnum
import org.openiam.idm.srvc.user.dto.UserAttribute

import java.text.SimpleDateFormat

public class ADPopulationScript extends org.openiam.idm.srvc.recon.service.AbstractPopulationScript {

    def dateFormat = new SimpleDateFormat("yyyy-MM-dd")

    public int execute(Map<String, String> line, ProvisionUser pUser) {
        int retval = 1
        for(String key: line.keySet()) {
            switch(key) {
                case "SamAccountName":
                    addAttribute(pUser, "sAMAccountName", line.get("SamAccountName"))
                    break
                case "EmployeeID":
                    if(pUser.employeeId != line.get("EmployeeID")){
                        pUser.employeeId = line.get("EmployeeID")
                        retval = 0
                    }
                    addAttribute(pUser, "employeeID", line.get("EmployeeID"))
                    break
                case "employeeType":
                    if(pUser.employeeType != line.get("employeeType")){
                        pUser.employeeType = line.get("employeeType")
                        retval = 0
                    }
                    addAttribute(pUser, "employeeType", line.get("employeeType"))
                    break
                case "GivenName":
                    if(pUser.firstName != line.get("GivenName")){
                        pUser.firstName = line.get("GivenName")
                        retval = 0
                    }
                    addAttribute(pUser, "givenName", line.get("GivenName"))
                    break
                case "Surname":
                    if(pUser.lastName != line.get("Surname")){
                        pUser.lastName = line.get("Surname")
                        retval = 0
                    }
                    addAttribute(pUser, "sn", line.get("Surname"))
                    break
                case "Initials":
                    if(pUser.middleInit != line.get("Initials")){
                        pUser.middleInit = line.get("Initials")
                        retval = 0
                    }
                    addAttribute(pUser, "initials", line.get("Initials"))
                    break
                case "OtherName":
                    if(pUser.nickname != line.get("OtherName")){
                        pUser.nickname = line.get("OtherName")
                        retval = 0
                    }
                    addAttribute(pUser, "otherName", line.get("OtherName"))
                    break
                case "AccountExpires":
                    //TODO: What's the date format?
                    /*
                    if(line.get("AccountExpires") && pUser.lastDate != line.get("AccountExpires")) {
                        pUser.lastDate = new Date(line.get("AccountExpires") as Long)
                        retval = 0
                    }
                    */
                    addAttribute(pUser, "accountExpires", line.get("AccountExpires"))
                    break
                case "City":
                    if (insertPrimaryAddressItem(pUser, 'city', line.get("City"))) {
                        retval = 0
                    }
                    addAttribute(pUser, "l", line.get("City"))
                    break
                case "State":
                    if (insertPrimaryAddressItem(pUser, 'state', line.get("State"))) {
                        retval = 0
                    }
                    addAttribute(pUser, "st", line.get("State"))
                    break
                case "HomeDirectory":
                    addAttribute(pUser, "homeDirectory", line.get("HomeDirectory"))
                    break
                case "homeDrive":
                    addAttribute(pUser, "homeDrive", line.get("homeDrive"))
                    break
                case "UserPrincipalName":
                    addAttribute(pUser, "userPrincipalName", line.get("UserPrincipalName"))
                    break
                case "DistinguishedName":
                    addAttribute(pUser, "distinguishedName", line.get("DistinguishedName"))
                    break
                case "ObjectGUID":
                    addAttribute(pUser, "objectGUID", line.get("ObjectGUID"))
                    break
                case "extensionAttribute1":
                    addAttribute(pUser, "extensionAttribute1", line.get("extensionAttribute1"))
                    break
                case "extensionAttribute2":
                    addAttribute(pUser, "extensionAttribute2", line.get("extensionAttribute2"))
                    break
                case "Manager":
                    //TODO: Add supervisor
                    addAttribute(pUser, "manager", line.get("Manager"))
                    break
            }
        }

        ManagedSystemWebService systemWebService = context.getBean("managedSysService")
        ResourceDataService  resourceDataService = context.getBean("resourceDataService")
        def currentManagedSys = systemWebService.getManagedSys(managedSysId)
        def currentResource = resourceDataService.getResource(currentManagedSys.resourceId)
        if (!pUser?.resources?.find {it == currentResource}) {
            currentResource.operation = AttributeOperationEnum.ADD
            pUser.resources.add(currentResource)
        }
        //set status to active: IMPORTANT!!!!
        if (!pUser.status) {
            pUser.status = UserStatusEnum.PENDING_INITIAL_LOGIN
        }
        if (!pUser.metadataTypeId) {
            pUser.metadataTypeId = "Contractor"
        }
        return retval
    }

    boolean insertPrimaryAddressItem(ProvisionUser pUser, String item, String value) {
        def address = pUser.addresses?.find { Address a-> a.metadataTypeId == 'PRIMARY_LOCATION' }
        def isNew = false
        if (!address) {
            isNew = true
            address = new Address()
            address.metadataTypeId = 'PRIMARY_LOCATION'
            pUser.addresses.add(address)
        }
        if (address."$item" != value) {
            if (address.operation == AttributeOperationEnum.NO_CHANGE) {
                address.operation = isNew ? AttributeOperationEnum.ADD : AttributeOperationEnum.REPLACE
            }
            address."$item" = value
            return true
        }
        return false
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