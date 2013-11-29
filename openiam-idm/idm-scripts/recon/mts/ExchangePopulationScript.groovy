import org.openiam.base.AttributeOperationEnum
import org.openiam.idm.srvc.continfo.dto.Address
import org.openiam.idm.srvc.continfo.dto.EmailAddress
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService
import org.openiam.idm.srvc.res.service.ResourceDataService
import org.openiam.provision.dto.ProvisionUser
import org.openiam.idm.srvc.user.dto.UserStatusEnum
import org.openiam.idm.srvc.user.dto.UserAttribute

import java.text.SimpleDateFormat

public class ExchangePopulationScript extends org.openiam.idm.srvc.recon.service.AbstractPopulationScript {

    def dateFormat = new SimpleDateFormat("yyyy-MM-dd")

    public int execute(Map<String, String> line, ProvisionUser pUser) {
        int retval = 1
        for(String key: line.keySet()) {
            switch(key) {
                case "SamAccountName":
                    addAttribute(pUser, "sAMAccountName", line.get("SamAccountName"))
                    break
                case "WindowsEmailAddress":
                    if (insertPrimaryEmail(pUser, line.get("WindowsEmailAddress"))) {
                        retval = 0
                    }
                    addAttribute(pUser, "mail", line.get("WindowsEmailAddress"))
                    break
                case "DisplayName":
                    def displayName = line.get("DisplayName")
                    if (displayName) {
                        def tokens = displayName.split(',')
                        if (pUser.lastName != tokens[0]) {
                            pUser.lastName = tokens[0]
                            retval = 0
                        }
                        if (tokens.size() > 1 && pUser.firstName != tokens[1]) {
                            pUser.firstName = tokens[1]
                            retval = 0
                        }
                    }
                    addAttribute(pUser, "displayName", displayName)
                    break
                case "Database":
                    addAttribute(pUser, "homeMDB", line.get("Database"))
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

    boolean insertPrimaryEmail(ProvisionUser pUser, String emailAddress) {
        def email = pUser.emailAddresses?.find { EmailAddress e-> e.metadataTypeId == 'PRIMARY_EMAIL' }
        def isNew = false
        if (!email) {
            isNew = true
            email = new EmailAddress()
            email.metadataTypeId = 'PRIMARY_EMAIL'
            pUser.emailAddresses.add(email)
        }
        if (email.emailAddress != emailAddress) {
            email.operation = isNew ? AttributeOperationEnum.ADD : AttributeOperationEnum.REPLACE
            email.emailAddress = emailAddress
            return true
        }
        return false
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
            userAttr.operation = AttributeOperationEnum.REPLACE
        }
        pUser.userAttributes.put(attributeName, userAttr)
    }
}